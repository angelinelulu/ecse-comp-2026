package com.kirbken.controllers;

import com.kirbken.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoseController implements FxController {
    private SceneManager manager;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    // Add @FXML fields/methods once you know what buttons lose.fxml has

    @FXML
    public void onPlayAgain(ActionEvent event) {
        // Play again should return to the arena to rematch
        manager.goToArena();
    }

    // if player clicks home, go to start
    @FXML
    public void onHome(ActionEvent event) {
        manager.goToStart();
    }
}