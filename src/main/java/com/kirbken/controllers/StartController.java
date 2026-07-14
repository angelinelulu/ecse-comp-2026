package com.kirbken.controllers;

import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;
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
    @FXML private Button singleplayerButton;
    @FXML private Button multiplayerButton;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void StartGame() {
        modeSelectBox.setVisible(true);
        modeSelectBox.setManaged(true);
        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(singleplayerButton, singleplayerButton, multiplayerButton)
        );
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