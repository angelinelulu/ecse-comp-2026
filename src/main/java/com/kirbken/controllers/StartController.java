package com.kirbken.controllers;

import com.kirbken.SceneManager;
import com.kirbken.utils.QuizManager;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class StartController implements FxController {
    private SceneManager manager;
    private boolean isTransitioning = false;

    @FXML private Button start_button;
    @FXML private Button settings_button;
    @FXML private Pane startRootPane;
    @FXML private Button quiz_mode_button;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void StartGame() {
        if (isTransitioning) {
            return;
        }
        isTransitioning = true;

        QuizManager.getInstance().setQuizModeEnabled(false);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), startRootPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> manager.goToModeSelect());
        fadeOut.play();
    }

    @FXML
    private void GoToQuizModeSelect() {
        manager.goToQuizModeSelect();
    }

    @FXML
    private void GameSettingsClicked() {
        manager.goToSettingsFrom(startRootPane, null);
    }
}