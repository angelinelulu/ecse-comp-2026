package com.kirbken.controllers;

import com.kirbken.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class StartController implements FxController {
    private SceneManager manager;

    @FXML
    private Button start_button;

    @FXML
    private Button settings_button;

    @FXML
    private AnchorPane startRootPane;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void StartGame() {
        manager.goToStory();
    }

    @FXML
    private void GameSettingsClicked() {
        manager.goToSettingsFrom(startRootPane, null);
    }
}