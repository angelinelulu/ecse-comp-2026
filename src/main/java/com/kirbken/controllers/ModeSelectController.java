package com.kirbken.controllers;

import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ModeSelectController implements FxController {
    private SceneManager manager;

    @FXML private Button singleplayerButton;
    @FXML private Button multiplayerButton;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() ->
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
}