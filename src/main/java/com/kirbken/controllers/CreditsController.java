package com.kirbken.controllers;

import com.kirbken.SceneManager;
import javafx.fxml.FXML;

public class CreditsController implements FxController {
    private SceneManager manager;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void onBackClicked() {
        manager.goToSettings();
    }
}