package com.kirbken.controllers;

import com.kirbken.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WinController implements FxController {
    private SceneManager manager;

    @FXML private Label lblVictory;
    @FXML private Label lblRank;
    @FXML private Label lblScore;
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
        // Play again should return to the arena to rematch
        manager.goToArena();
    }

    // if player clicks home, go to start
    @FXML
    public void onHome(ActionEvent event) {
        manager.goToStart();
    }
    
    // Set the victory message for the win screen.
    @FXML
    public void setWinLabel(String text) {
        lblVictory.setText(text == null || text.isBlank() ? "You Win!" : text);
        lblVictory.getStyleClass().remove("lose-text");
        if (!lblVictory.getStyleClass().contains("win-text")) {
            lblVictory.getStyleClass().add("win-text");
        }
    }

    // Convenience method for arena to report a winner by name.
    public void setWinnerName(String name) {
        setWinLabel((name == null || name.isBlank()) ? "You Win!" : name + " wins!");
    }
}