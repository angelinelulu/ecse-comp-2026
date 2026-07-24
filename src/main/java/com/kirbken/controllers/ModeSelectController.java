package com.kirbken.controllers;

import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class ModeSelectController implements FxController {
  private SceneManager manager;
  private boolean isTransitioning = false;

  @FXML private Pane modeSelectRootPane;
  @FXML private Button singleplayerButton;
  @FXML private Button multiplayerButton;

  @Override
  public void setSceneManager(SceneManager manager) {
    this.manager = manager;
  }

  @FXML
  public void initialize() {
    Platform.runLater(
        () ->
            KeyboardNavHelper.enableHorizontalNav(
                singleplayerButton, singleplayerButton, multiplayerButton));

    modeSelectRootPane.setOpacity(0.0);
    FadeTransition fadeIn = new FadeTransition(Duration.millis(400), modeSelectRootPane);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    fadeIn.play();
  }

  @FXML
  private void onSingleplayerClicked() {
    if (isTransitioning) {
      return;
    }
    GameState.setGameMode(GameState.GameMode.SINGLEPLAYER);
    zoomInto(singleplayerButton);
  }

  @FXML
  private void onMultiplayerClicked() {
    if (isTransitioning) {
      return;
    }
    GameState.setGameMode(GameState.GameMode.MULTIPLAYER);
    zoomInto(multiplayerButton);
  }

  private void zoomInto(Button target) {
    isTransitioning = true;

    javafx.geometry.Bounds boundsInScene = target.localToScene(target.getBoundsInLocal());
    javafx.geometry.Bounds boundsInPane = modeSelectRootPane.sceneToLocal(boundsInScene);

    double pivotX = boundsInPane.getMinX() + boundsInPane.getWidth() / 2;
    double pivotY = boundsInPane.getMinY() + boundsInPane.getHeight() / 2;

    modeSelectRootPane.setScaleX(1.0);
    modeSelectRootPane.setScaleY(1.0);

    // Cache the node as a bitmap for smoother scaling
    modeSelectRootPane.setCache(true);
    modeSelectRootPane.setCacheHint(javafx.scene.CacheHint.SPEED);

    Scale scaleTransform = new Scale(1.0, 1.0, pivotX, pivotY);
    modeSelectRootPane.getTransforms().add(scaleTransform);

    Timeline zoomTimeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(scaleTransform.xProperty(), 1.0, Interpolator.EASE_IN),
                new KeyValue(scaleTransform.yProperty(), 1.0, Interpolator.EASE_IN)),
            new KeyFrame(
                Duration.millis(700),
                new KeyValue(scaleTransform.xProperty(), 3.0, Interpolator.EASE_IN),
                new KeyValue(scaleTransform.yProperty(), 3.0, Interpolator.EASE_IN)));

    zoomTimeline.setOnFinished(
        e -> {
          modeSelectRootPane.setCache(false);
          proceedToGame();
        });
    zoomTimeline.play();
  }

  private void proceedToGame() {
    GameState.resetRounds();
    GameState.unlockForNewRound();

    FadeTransition fadeOut = new FadeTransition(Duration.millis(350), modeSelectRootPane);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setOnFinished(e -> manager.goToStory());
    fadeOut.play();
  }
}
