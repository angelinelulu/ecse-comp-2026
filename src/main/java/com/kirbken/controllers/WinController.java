package com.kirbken.controllers;

import com.kirbken.SceneManager;

public class WinController implements FxController {
    private SceneManager manager;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    // Add @FXML fields/methods once you know what buttons win.fxml has
}