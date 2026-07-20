package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.MatchStats;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

public class LoseController implements FxController {
    private SceneManager manager;

    @FXML private Label lblDefeat;
    @FXML private ImageView imgLoser;
    @FXML private TitledPane paneResults;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;

    private CharacterProfile pendingLoserProfile;
    private MatchStats pendingMatchStats;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void onPlayAgain(ActionEvent event) {
        manager.goToArena();
    }

    @FXML
    public void onHome(ActionEvent event) {
        GameState.resetRounds();
        GameState.unlockForNewRound();
        manager.goToStart();
    }

    @FXML
    public void initialize() {

        java.net.URL fontUrl = getClass().getResource("/fonts/GeistPixelRegular.ttf");
        if (fontUrl != null) {
        Font.loadFont(fontUrl.toExternalForm(), 28);
    }
        if (lblDefeat != null) {
            lblDefeat.setText("Defeat");
        }

        if (paneResults != null) {
            paneResults.setContent(createResultsPlaceholder(null, null));
        }

        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(btnPlayAgain, btnHome)
        );
    }

    public void setGameOverLabel(String text) {
        if (lblDefeat != null) {
            lblDefeat.setText(text == null || text.isBlank() ? "Defeat" : text);
        }
    }

    /**
     * Sets the loser's profile only, with no match stats. Kept for backward compatibility
     * with any existing callers of the single-arg SceneManager.goToLose(profile).
     * Prefer setLoserProfile(profile, stats) so the results panel shows real match data.
     */
    public void setLoserProfile(CharacterProfile loserProfile) {
        setLoserProfile(loserProfile, null);
    }

    public void setLoserProfile(CharacterProfile loserProfile, MatchStats matchStats) {
        this.pendingLoserProfile = loserProfile;
        this.pendingMatchStats = matchStats;

        if (loserProfile == null) {
            if (paneResults != null) {
                paneResults.setContent(createResultsPlaceholder(null, null));
            }
            return;
        }

        if (imgLoser != null) {
            String imagePath = resolveLoserImagePath(loserProfile);
            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                imgLoser.setImage(new Image(imageUrl.toExternalForm()));
            }
        }

        if (paneResults != null) {
            VBox resultsContent = createResultsPlaceholder(loserProfile, matchStats);
            paneResults.setContent(resultsContent);
            animateResultsIn(paneResults, resultsContent);
        }
    }

    private static final double TYPEWRITER_CHAR_MS = 22;   // delay per character
    private static final double TYPEWRITER_LINE_GAP_MS = 160; // pause between lines

    private void animateResultsIn(TitledPane pane, VBox content) {
        // Capture each label's full text and blank it out before the panel fades in,
        // so the typewriter reveal has something to type once the panel is visible.
        java.util.List<Label> textLabels = new java.util.ArrayList<>();
        java.util.List<String> fullTexts = new java.util.ArrayList<>();
        for (javafx.scene.Node node : content.getChildren()) {
            if (node instanceof Label label) {
                textLabels.add(label);
                fullTexts.add(label.getText());
                label.setText("");
            }
        }

        pane.setOpacity(0.0);
        pane.setTranslateX(30);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(450), pane);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(450), pane);
        slide.setFromX(30);
        slide.setToX(0);
        slide.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.ParallelTransition entrance = new javafx.animation.ParallelTransition(fade, slide);
        entrance.setDelay(javafx.util.Duration.millis(300)); // let "Defeat" register first
        entrance.setOnFinished(e -> playTypewriter(textLabels, fullTexts));
        entrance.play();
    }

    /** Types out each label's text in sequence, one line finishing before the next starts. */
    private void playTypewriter(java.util.List<Label> labels, java.util.List<String> fullTexts) {
        javafx.animation.SequentialTransition sequence = new javafx.animation.SequentialTransition();

        for (int i = 0; i < labels.size(); i++) {
            sequence.getChildren().add(createTypewriterTimeline(labels.get(i), fullTexts.get(i)));
            if (i < labels.size() - 1) {
                sequence.getChildren().add(new javafx.animation.PauseTransition(javafx.util.Duration.millis(TYPEWRITER_LINE_GAP_MS)));
            }
        }

        sequence.play();
    }

    /** Builds a Timeline that reveals fullText onto label one character at a time. */
    private javafx.animation.Timeline createTypewriterTimeline(Label label, String fullText) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();

        for (int i = 1; i <= fullText.length(); i++) {
            String partial = fullText.substring(0, i);
            javafx.animation.KeyFrame frame = new javafx.animation.KeyFrame(
                javafx.util.Duration.millis(TYPEWRITER_CHAR_MS * i),
                e -> label.setText(partial)
            );
            timeline.getKeyFrames().add(frame);
        }

        return timeline;
    }

    private VBox createResultsPlaceholder(CharacterProfile profile, MatchStats stats) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(14, 16, 14, 16));
        content.getStyleClass().add("arcade-pane-content"); // transparent — lets the TitledPane's own white/pink content styling show through

        Label header = new Label(profile == null ? "Results Preview" : profile.getDisplayName() + " — Match Results");
        header.getStyleClass().add("results-subheader"); // see CSS note below

        java.util.List<Label> labels = new java.util.ArrayList<>();

        if (stats == null) {
            // No match stats available yet (e.g. initial placeholder before a match has run) —
            // fall back to the static base-stat preview so the panel is never blank.
            labels.add(new Label(profile == null ? "Character: --" : "Character: " + profile.getDisplayName()));
            labels.add(new Label(profile == null ? "HP: --   Attack: --" : "HP: " + profile.getHp() + "   Attack: " + profile.getAttackPower()));
            labels.add(new Label(profile == null ? "Defense: --   Speed: --" : "Defense: " + profile.getDefensePower() + "   Speed: " + profile.getSpeed()));
            labels.add(new Label(profile == null ? "Match results will be detailed here." : "Better luck next time!"));
            for (Label l : labels) l.getStyleClass().add("arcade-stat-label");
        } else {
            Label damageDealt = new Label("Damage Dealt: " + stats.getDamageDealt());
            Label damageTaken = new Label("Damage Taken: " + stats.getDamageTaken());
            damageDealt.getStyleClass().add("arcade-stat-highlight"); // pop the number the player will care about most
            damageTaken.getStyleClass().add("arcade-stat-label");
            labels.add(damageDealt);
            labels.add(damageTaken);

            if (stats.getQuizCorrect() + stats.getQuizWrong() > 0) {
                Label quiz = new Label("Quiz: " + stats.getQuizCorrect() + " correct / " + stats.getQuizWrong() + " wrong");
                quiz.getStyleClass().add("arcade-stat-label");
                labels.add(quiz);
            }

            Label duration = new Label("Match Length: " + stats.getFormattedDuration());
            duration.getStyleClass().add("arcade-stat-label");
            labels.add(duration);
        }

        for (Label label : labels) {
            label.setWrapText(true);
        }

        content.getChildren().add(header);
        content.getChildren().addAll(labels);
        return content;
    }

    private String resolveLoserImagePath(CharacterProfile profile) {
        String profileId = profile.getId();
        if (profileId == null) {
            return profile.getSpriteSheetPath();
        }

        return switch (profileId) {
            case "kirby_base" -> "/images/basic_kirby/die.png";
            case "kirby_ninja" -> "/images/ninja_kirby/die.png";
            case "kirby_storm" -> "/images/storm_kirby/die.png";
            case "kirby_angelic" -> "/images/sailor_kirby/die.png";
            case "kirby_buff" -> "/images/boss_kirby/die.png";
            case "vexthorn_basic" -> "/images/vexthorn_basic.png";
            case "vexthorn_boss" -> "/images/vexthorn_boss.png";
            default -> profile.getSpriteSheetPath();
        };
    }
}