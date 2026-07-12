package com.kirbken.controllers;

import com.kirbken.GameState;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class StartController implements FxController {
    private SceneManager manager;

    @FXML private Button start_button;
    @FXML private Button settings_button;
    @FXML private AnchorPane startRootPane;
    @FXML private Button quiz_mode_button;
    @FXML private VBox modeSelectBox;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void StartGame() {
        modeSelectBox.setVisible(true);
        modeSelectBox.setManaged(true);
    }

    @FXML
    private void onSingleplayerClicked() {
        GameState.setGameMode(GameState.GameMode.SINGLEPLAYER);
        proceedToGame();
    }

    @FXML
    private void onMultiplayerClicked() {
        GameState.setGameMode(GameState.GameMode.MULTIPLAYER);
        proceedToGame();
    }

    private void proceedToGame() {
        GameState.resetRounds();
        GameState.unlockForNewRound();
        manager.goToStory();
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