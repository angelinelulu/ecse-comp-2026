package com.kirbken.controllers;

import com.kirbken.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;

public class LoseController implements FxController {
    private SceneManager manager;

    @FXML private Label lblGameOver;
    @FXML private ImageView imgLoser;
    @FXML private TitledPane paneResults;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void onPlayAgain(ActionEvent event) {
        manager.goToArena();
    }

    @FXML
    public void onHome(ActionEvent event) {
        manager.goToStart();
    }

    @FXML
    public void initialize() {
        if (lblGameOver != null) {
            lblGameOver.setText("Game Over : You Lost");
        }
    }

    public void setGameOverLabel(String text) {
        if (lblGameOver != null) {
            lblGameOver.setText(text == null || text.isBlank() ? "Game Over : You Lost" : text);
        }
    }

    public void setLoserName(String name) {
        setGameOverLabel((name == null || name.isBlank()) ? "Game Over : You Lost" : "Game Over : " + name + " Wins");
    }
}