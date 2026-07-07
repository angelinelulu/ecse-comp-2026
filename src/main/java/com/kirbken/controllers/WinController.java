package com.kirbken.controllers;

import com.kirbken.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WinController implements FxController {
    private SceneManager manager;

    @FXML private Label lblWinLose;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    // Add @FXML fields/methods once you know what buttons win.fxml has

    // if player clicks play again, go to story
    @FXML
    public void onPlayAgain(ActionEvent event) {
        manager.goToStory();
    }

    // if player clicks home, go to start
    @FXML
    public void onHome(ActionEvent event) {
        manager.goToStart();
    }
}