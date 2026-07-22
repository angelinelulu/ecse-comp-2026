package com.kirbken.controllers;

import com.kirbken.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class QuitController implements FxController {
    @FXML private Button btnYes;
    @FXML private Button btnNo;
    @FXML private Label lblConfirmation;

    private SceneManager manager;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void onYesClicked() {
        manager.goToStart(); // Navigate to the start screen before quitting
    }

    @FXML
    private void onNoClicked() {
        manager.goToSettings();
    }
}