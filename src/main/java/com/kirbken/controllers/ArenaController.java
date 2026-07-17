package com.kirbken.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kirbken.CharacterProfile;
import com.kirbken.CharacterRegistry;
import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.models.Question;
import com.kirbken.models.Character;
import com.kirbken.models.CharacterAnimationRegistry;
import com.kirbken.models.Fight;
import com.kirbken.models.Projectile;
import com.kirbken.models.SpriteAnimator;
import com.kirbken.utils.MusicManager;
import com.kirbken.utils.QuizManager;
import com.kirbken.components.QuizPopup; 
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class ArenaController implements FxController {

  @FXML private ImageView p1Sprite, p2Sprite;
  @FXML private Region p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7;
  @FXML private Region p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7;
  @FXML private Pane rootPane;
  @FXML private Label timerLabel;
  @FXML private Arc timerArc;
  @FXML private ImageView imgMute;
  @FXML private ImageView arenaBackground;
  @FXML private Label countdownLabel;

  private SceneManager manager;
  private Character p1, p2;
  private CharacterProfile p1Profile, p2Profile;
  private Fight fight;
  private Region[] p1Segments, p2Segments;
  private Rectangle p1HitboxDebug;
  private Rectangle p2HitboxDebug; 
  private static final boolean SHOW_HITBOX_DEBUG = true; // flip to false to hide once tuned / delete

  private final Set<KeyCode> activeKeys = new HashSet<>();
  private AnimationTimer timer;

  private final MusicManager musicManager = MusicManager.getInstance();
  private final List<Projectile> projectiles = new ArrayList<>();
  private final Map<Character, String> projectileImages = new HashMap<>();
  private static final double CHARACTER_WIDTH = 150; // approximate hit-box width
  private static final double PROJECTILE_SPEED = 4;
  private static final int ROUND_DURATION_SECONDS = 180; // 3:00
  private int timeRemaining = ROUND_DURATION_SECONDS;
  private long lastSecondTick = 0;

  // --- singleplayer mode fields ---
  private long lastAIDecisionTime = 0;
  private static final long AI_DECISION_INTERVAL_NS = 400_000_000L;
  private static final double AI_ATTACK_RANGE = 160;
  private final Random aiRandom = new Random();

  // Current AI intent, persists between decision ticks
  private enum AIAction { APPROACH_LEFT, APPROACH_RIGHT, ATTACK, IDLE }
  private AIAction currentAIAction = AIAction.IDLE;

  // --- Quiz mode fields ---
  private static final int QUESTIONS_PER_MATCH = 2;
  private static final int FALLBACK_TRIGGER_SECONDS_REMAINING = 15; // force a quiz if time is running out
  private final List<Long> quizTriggerNanosList = new ArrayList<>();
  private int nextQuizIndex = 0;
  private int questionsAskedCount = 0;
  private static final int QUIZ_WRONG_PENALTY = 10;
  private static final int QUIZ_CORRECT_REWARD = 20;

  @FXML
  public void initialize() {
    java.net.URL fontUrl = getClass().getResource("/fonts/TekkenReg.ttf");
    if (fontUrl != null) {
      Font.loadFont(fontUrl.toExternalForm(), 28);
    }

    musicManager.loadSound("arena", "/sounds/arenaaudio.mp3");

    javafx.application.Platform.runLater(
        () -> {
          if (musicManager.isMuted()) {
            imgMute.setImage(new Image(getClass().getResourceAsStream("/images/muteOn.png")));
            musicManager.setVolume("arena", 0.0);
          } else {
            imgMute.setImage(new Image(getClass().getResourceAsStream("/images/muteOff.png")));
            musicManager.setVolume("arena", 0.3);
            musicManager.playSound("arena", 0.3);
          }
        });

    p1Profile = GameState.getSelectedCharacter();
    System.out.println("ARENA LOADED CHARACTER: " + p1Profile.getId());

    boolean isSingleplayer = GameState.getGameMode() == GameState.GameMode.SINGLEPLAYER;

    if (isSingleplayer) {
        p2Profile = (GameState.getCurrentRound() == 1)
            ? CharacterRegistry.getVexthorn()
            : CharacterRegistry.getVexthornBoss();

        if (GameState.getCurrentRound() == 2) {
            var bgUrl = getClass().getResource("/images/arena2.png");
            if (bgUrl != null) {
                arenaBackground.setImage(new Image(bgUrl.toExternalForm()));
            } else {
                System.out.println("Missing background: /images/arena2.png");
            }
        }
    } else {
        // Multiplayer: P2 is a real player who scanned their own card — Kirby vs Kirby, no Vexthorn, no rounds
        p2Profile = GameState.getSelectedCharacterP2();
        System.out.println("ARENA LOADED CHARACTER P2: " + (p2Profile != null ? p2Profile.getId() : "NULL - P2 never scanned!"));
    }

    setSpriteImage(p1Sprite, p1Profile);
    setSpriteImage(p2Sprite, p2Profile);

    p1 = new Character(p1Sprite, -230, 300, true,
        p1Profile.getHp(), p1Profile.getAttackPower(), p1Profile.getDefensePower(), p1Profile.getSpeed());
    p2 = new Character(p2Sprite, 750, 300, false,
        p2Profile.getHp(), p2Profile.getAttackPower(), p2Profile.getDefensePower(), p2Profile.getSpeed());

    applyAnimations(p1, p1Profile.getId());
    applyAnimations(p2, p2Profile.getId());
    
    if (SHOW_HITBOX_DEBUG) {
        p1HitboxDebug = createDebugRect();
        p2HitboxDebug = createDebugRect();
        rootPane.getChildren().addAll(p1HitboxDebug, p2HitboxDebug);
    }
    
    fight = new Fight(p1, p2);

    p1Segments = new Region[] {p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7};
    p2Segments = new Region[] {p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7};

    timerLabel.setText(formatTime(timeRemaining));

    if (QuizManager.getInstance().isQuizModeEnabled()) {
      initQuizTiming();
    }

    playCountdown(this::startGameLoop);
  }

  @Override
  public void setSceneManager(SceneManager manager) {
    this.manager = manager;
  }

  private void setSpriteImage(ImageView view, CharacterProfile profile) {
    var url = getClass().getResource(profile.getSpriteSheetPath());
    if (url != null) {
      view.setImage(new Image(url.toExternalForm()));
    } else {
      System.out.println(
          "No sprite found for " + profile.getDisplayName() + " at " + profile.getSpriteSheetPath());
    }
  }

  public void setupInput(Scene scene) {
      scene.setOnKeyPressed(e -> {
          activeKeys.add(e.getCode());
          if (e.getCode() == KeyCode.ESCAPE) {
              onSetting(null);
          } else if (e.getCode() == KeyCode.J) {
              onMute(null);
          }
      });
      scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
  }

  private void startGameLoop() {
    timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
          handleInput();
          p1.updatePhysics();
          p2.updatePhysics();
          p1.updateSpecial();
          p2.updateSpecial();

          if (p1.consumeJustThrew()) {
              spawnProjectile(p1);
          }
          if (p2.consumeJustThrew()) {
              spawnProjectile(p2);
          }

          updateProjectiles();

          fight.update();

          if (p1.consumeJustHit()) {
              playHitShake(p1Sprite);
              musicManager.playSound("punch", 0.6);
          }
          if (p2.consumeJustHit()) {
              playHitShake(p2Sprite);
              musicManager.playSound("punch", 0.6);
          }

          p1.getAnimator().update(now);
          p2.getAnimator().update(now);

          if (SHOW_HITBOX_DEBUG) {
              updateDebugRect(p1HitboxDebug, p1);
              updateDebugRect(p2HitboxDebug, p2);
          }

          updateHealthBars();
          updateTimer(now);

        // Quiz mode: fallback — force any remaining questions before time runs out.
        // Does NOT fire on a KO (fight.isOver()) — if a character's health hits 0,
        // the match ends immediately and no quiz pops up, even if questions are still owed.
        if (QuizManager.getInstance().isQuizModeEnabled()
            && !fight.isOver()
            && questionsAskedCount < QUESTIONS_PER_MATCH
            && timeRemaining <= FALLBACK_TRIGGER_SECONDS_REMAINING) {
            triggerQuizPopup();
            return; // skip the rest of this frame; time-up will be re-checked next frame
        }

          if (fight.isOver()) {
              stop();
              cleanupMatchAudio();
              showWinner(fight.getWinner());
              return;
          }

        if (timeRemaining <= 0) {
            stop();
            cleanupMatchAudio();
            handleTimeUp();
            return;
        }

        // Quiz mode: scheduled mid-match trigger
        if (QuizManager.getInstance().isQuizModeEnabled()
            && nextQuizIndex < quizTriggerNanosList.size()
            && now >= quizTriggerNanosList.get(nextQuizIndex)) {
            triggerQuizPopup();
        }
    }
    };
    timer.start();
  }

  private void handleInput() {
      if (activeKeys.contains(KeyCode.A)) p1.moveLeft();
      if (activeKeys.contains(KeyCode.D)) p1.moveRight();
      if (activeKeys.contains(KeyCode.W)) p1.jump();

      boolean p1Attacking = activeKeys.contains(KeyCode.F);
      p1.setAttacking(p1Attacking);
      if (p1Attacking) {
          p1.getAnimator().setState(SpriteAnimator.State.ATTACK);
      } else if (activeKeys.contains(KeyCode.G)) {
          p1.triggerSpecial();
      } else if (!activeKeys.contains(KeyCode.A) && !activeKeys.contains(KeyCode.D)) {
          p1.getAnimator().setState(SpriteAnimator.State.IDLE);
      }

      if (GameState.getGameMode() == GameState.GameMode.SINGLEPLAYER) {
          updateAI();
      } else {
          if (activeKeys.contains(KeyCode.LEFT)) p2.moveLeft();
          if (activeKeys.contains(KeyCode.RIGHT)) p2.moveRight();
          if (activeKeys.contains(KeyCode.UP)) p2.jump();

          boolean p2Attacking = activeKeys.contains(KeyCode.L);
          p2.setAttacking(p2Attacking);
          if (p2Attacking) {
              p2.getAnimator().setState(SpriteAnimator.State.ATTACK);
          } else if (activeKeys.contains(KeyCode.SEMICOLON)) {
              p2.triggerSpecial();
          } else if (!activeKeys.contains(KeyCode.LEFT) && !activeKeys.contains(KeyCode.RIGHT)) {
              p2.getAnimator().setState(SpriteAnimator.State.IDLE);
          }
      }
  }

  private void updateAI() {
      long now = System.nanoTime();

      // Only re-evaluate the AI's strategy periodically...
      if (now - lastAIDecisionTime >= AI_DECISION_INTERVAL_NS) {
          lastAIDecisionTime = now;
          decideAIAction();
      }

      // ...but execute movement every single frame, just like real player input
      switch (currentAIAction) {
          case APPROACH_LEFT -> {
              p2.moveLeft();
              p2.setAttacking(false);
          }
          case APPROACH_RIGHT -> {
              p2.moveRight();
              p2.setAttacking(false);
          }
          case ATTACK -> {
              p2.setAttacking(true);
              p2.getAnimator().setState(SpriteAnimator.State.ATTACK);
          }
          case IDLE -> {
              p2.setAttacking(false);
          }
      }
  }

  private void decideAIAction() {
      double distance = Math.abs(p1.getCenterX() - p2.getCenterX());

      if (distance > AI_ATTACK_RANGE) {
          currentAIAction = (p1.getCenterX() < p2.getCenterX())
              ? AIAction.APPROACH_LEFT
              : AIAction.APPROACH_RIGHT;
      } else {
          currentAIAction = AIAction.ATTACK;

          if (aiRandom.nextInt(100) < 15) {
              p2.triggerSpecial();
          }
      }

      if (aiRandom.nextInt(100) < 5) {
          p2.jump();
      }
  }

  private void updateHealthBars() {
    int p1Lit = (int) Math.ceil((p1.getHealth() / (double) p1.getMaxHealth()) * 8);
    int p2Lit = (int) Math.ceil((p2.getHealth() / (double) p2.getMaxHealth()) * 8);

    for (int i = 0; i < p1Segments.length; i++) {
      p1Segments[i].setStyle(
          i < p1Lit
              ? "-fx-background-color: #00d0ff; -fx-background-radius: 2;"
              : "-fx-background-color: #1a1a1a; -fx-background-radius: 2;");
    }
    for (int i = 0; i < p2Segments.length; i++) {
      p2Segments[i].setStyle(
          i < p2Lit
              ? "-fx-background-color: #8400ff; -fx-background-radius: 2;"
              : "-fx-background-color: #1a1a1a; -fx-background-radius: 2;");
    }
  }

  private String formatTime(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return String.format("%d:%02d", minutes, seconds);
  }

  private void updateTimer(long now) {
    if (lastSecondTick == 0) {
      lastSecondTick = now;
      return;
    }

    long elapsedSeconds = (now - lastSecondTick) / 1_000_000_000L;
    if (elapsedSeconds <= 0) {
      return;
    }

    lastSecondTick += elapsedSeconds * 1_000_000_000L;

    timeRemaining = Math.max(0, timeRemaining - (int) elapsedSeconds);
    timerLabel.setText(formatTime(timeRemaining));

    double progress = (double) timeRemaining / ROUND_DURATION_SECONDS;
    timerArc.setLength(360 * progress);

    if (timeRemaining <= 10) {
      timerArc.setStroke(Color.RED);
    }
  }

  private void applyAnimations(Character character, String characterId) {
      var set = CharacterAnimationRegistry.get(characterId);
      if (set == null) {
          System.out.println("No animation set found for: " + characterId + " — using static sprite only.");
          return;
      }
      character.getAnimator().addFrames(SpriteAnimator.State.IDLE, set.idle);
      character.getAnimator().addFrames(SpriteAnimator.State.WALK, set.walk);
      character.getAnimator().addFrames(SpriteAnimator.State.ATTACK, set.attack);
      character.getAnimator().addFrames(SpriteAnimator.State.SPECIAL_WINDUP, set.specialWindup);
      if (set.specialWindup2 != null) {
          character.getAnimator().addFrames(SpriteAnimator.State.SPECIAL_WINDUP_2, set.specialWindup2);
      }
      character.getAnimator().addFrames(SpriteAnimator.State.SPECIAL_THROW, set.specialThrow);
      character.getAnimator().addFrames(SpriteAnimator.State.DEATH, set.die);
      projectileImages.put(character, set.projectilePath);
  }

  private void spawnProjectile(Character thrower) {
      String stickImage = projectileImages.get(thrower);
      if (stickImage == null) {
          return;
      }

      Projectile projectile = new Projectile(
          stickImage,
          thrower.getX() + 400,
          500, 
          thrower.isFacingRight(),
          PROJECTILE_SPEED,
          thrower.rollAttackDamage(),
          rootPane
      );
      projectiles.add(projectile);
  }

  private void updateProjectiles() {
      Iterator<Projectile> it = projectiles.iterator();
      while (it.hasNext()) {
          Projectile p = it.next();
          boolean stillActive = p.update();

          if (stillActive) {
              Character target = p.isMovingRight() ? p2 : p1;
              if (p.checkHit(target, CHARACTER_WIDTH)) {
                  target.takeDamage(p.getDamage());
                  p.deactivate();
                  stillActive = false;
              }
          }

          if (!stillActive) {
              rootPane.getChildren().remove(p.getView());
              it.remove();
          }
      }
  }

  private void updateDebugRect(Rectangle rect, Character character) {
      double centerX = character.getCenterX();
      double centerY = character.getCenterY();
      rect.setX(centerX - CHARACTER_WIDTH / 2);
      rect.setY(centerY - Projectile.VERTICAL_HIT_TOLERANCE);
  }

  private Rectangle createDebugRect() {
      Rectangle rect = new Rectangle(CHARACTER_WIDTH, Projectile.VERTICAL_HIT_TOLERANCE * 2);
      rect.setFill(javafx.scene.paint.Color.TRANSPARENT);
      rect.setStroke(javafx.scene.paint.Color.RED);
      rect.setStrokeWidth(2);
      return rect;
  }

  private void handleTimeUp() {
    Character winner = (p1.getHealth() >= p2.getHealth()) ? p1 : p2;
    showWinner(winner);
  }

  private void showWinner(Character winner) {
      musicManager.stopSound("arena");
      CharacterProfile loserProfile = (winner == p2) ? p1Profile : p2Profile;
      boolean isMultiplayer = GameState.getGameMode() == GameState.GameMode.MULTIPLAYER;

      if (isMultiplayer) {
          musicManager.playSound("win", 0.7); // always a win sound in multiplayer, regardless of which player wins
      } else if (winner == p2) {
          musicManager.playSound("lose", 0.7);
      } else {
          musicManager.playSound("win", 0.7);
      } 

      javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
      pause.setOnFinished(e -> {
          if (isMultiplayer) {
              int winningPlayerNumber = (winner == p1) ? 1 : 2;
              manager.goToWin("Victory to Player " + winningPlayerNumber + "!");
          } else if (winner == p2) {
              manager.goToLose(loserProfile);
          } else if (GameState.getCurrentRound() == 1) {
              manager.goToRoundTransition();
          } else {
              manager.goToWin();
          }
      });
      pause.play();
  }

  private void cleanupMatchAudio() {
    musicManager.stopSound("arena");
  }

  // --- Quiz mode methods ---

  /**
   * Schedules two quiz triggers at fixed points in the match
   */
  private void initQuizTiming() {
    quizTriggerNanosList.clear();
    Random random = new Random();
    long startNanos = System.nanoTime();

    int q1MinSeconds = 5;
    int q1MaxSeconds = 20;  
    int q2MinSeconds = 20; 
    int q2MaxSeconds = 40;

    int q1DelaySeconds = q1MinSeconds + random.nextInt(q1MaxSeconds - q1MinSeconds + 1);
    int q2DelaySeconds = q2MinSeconds + random.nextInt(q2MaxSeconds - q2MinSeconds + 1);

    quizTriggerNanosList.add(startNanos + q1DelaySeconds * 1_000_000_000L);
    quizTriggerNanosList.add(startNanos + q2DelaySeconds * 1_000_000_000L);
  } 

  private void triggerQuizPopup() {
      timer.stop();

      Question q = QuizManager.getInstance().getRandomQuestion();
      if (q == null) {
          questionsAskedCount++;
          nextQuizIndex++;
          timer.start();
          return;
      }

      QuizPopup popup = new QuizPopup(q, this::onQuizAnswered);
      rootPane.getChildren().add(popup.getOverlay());
  }

  private void onQuizAnswered(boolean wasCorrect, int submittingPlayer) {
      rootPane.getChildren().removeIf(
          node -> node instanceof StackPane && "quizOverlay".equals(node.getId()));

      questionsAskedCount++;
      nextQuizIndex++;

      if (wasCorrect) {
          Character opponent = (submittingPlayer == 1) ? p2 : p1;
          opponent.takeDamage(QUIZ_CORRECT_REWARD);
      } else {
          Character self = (submittingPlayer == 1) ? p1 : p2;
          self.takeDamage(QUIZ_WRONG_PENALTY);
      }

      activeKeys.clear();
      setupInput(rootPane.getScene());

      lastSecondTick = 0;
      timer.start();
  }

  @FXML
  private void onMute(MouseEvent event) {
    boolean muted = musicManager.toggleBackgroundMuted();

    if (muted) {
      imgMute.setImage(new Image(getClass().getResourceAsStream("/images/muteOn.png")));
    } else {
      imgMute.setImage(new Image(getClass().getResourceAsStream("/images/muteOff.png")));
    }

    musicManager.playSound("buttonClick", 0.5);
  }

  @FXML
  private void onSetting(MouseEvent event) {
      musicManager.playSound("buttonClick", 0.5);
      timer.stop(); // freeze the game loop
      manager.goToSettingsFrom(rootPane, this::resumeGame);
  }

  private void resumeGame() {
      lastSecondTick = 0; // reset so the countdown doesn't jump when resuming
      timer.start();
  }

  private void playHitShake(ImageView sprite) {
      javafx.animation.TranslateTransition shake = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(50), sprite);
      shake.setFromX(-8);
      shake.setToX(8);
      shake.setCycleCount(4);
      shake.setAutoReverse(true);
      shake.setOnFinished(e -> sprite.setTranslateX(0));
      shake.play();
  }

  private void playCountdown(Runnable onFinished) {
      countdownLabel.setVisible(true);
      musicManager.playSound("countdown", 0.8);

      String[] steps = {"3", "2", "1", "FIGHT!"};
      countdownLabel.setText(steps[0]); // show "3" immediately, no initial delay

      javafx.animation.Timeline timeline = new javafx.animation.Timeline();
      for (int i = 1; i < steps.length; i++) {
          String stepText = steps[i];
          javafx.animation.KeyFrame frame = new javafx.animation.KeyFrame(
              javafx.util.Duration.seconds(0.8 * i),
              e -> countdownLabel.setText(stepText)
          );
          timeline.getKeyFrames().add(frame);
      }

      javafx.animation.KeyFrame endFrame = new javafx.animation.KeyFrame(
          javafx.util.Duration.seconds(0.8 * steps.length),
          e -> {
              countdownLabel.setVisible(false);
              onFinished.run();
          }
      );
      timeline.getKeyFrames().add(endFrame);

      timeline.play();
  }
}