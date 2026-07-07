package com.kirbken.controllers;

import com.kirbken.SceneManager;
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
}