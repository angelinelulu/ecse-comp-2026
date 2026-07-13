package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;

public class Player2PromptController implements FxController {
    private SceneManager manager;

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