package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public class Player2PromptController implements FxController {
    private SceneManager manager;
    @FXML private Button yesButton;
    @FXML private Button noButton;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(yesButton, yesButton, noButton)
        );
    }

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void onYesClicked() {
        manager.goToCardScanP2();
    }

    @FXML
    private void onNoClicked() {
        manager.goToConfirmation(CharacterRegistry.getDefault(), true);
    }
}