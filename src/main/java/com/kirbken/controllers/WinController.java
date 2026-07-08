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

    private String outcomeText = "";

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    // Add @FXML fields/methods once you know what buttons win.fxml has

    // if player clicks play again, go to story
    @FXML
    public void onPlayAgain(ActionEvent event) {
        // Play again should return to the arena to rematch
        manager.goToStory();
    }

    // if player clicks home, go to start
    @FXML
    public void onHome(ActionEvent event) {
        manager.goToStart();
    }
    
    // Method to set the win/lose text based on the game outcome
    @FXML
    public void setWinLoseText(String text) {
        // normalize and store outcome
        outcomeText = text == null ? "" : text;
        lblWinLose.setText(outcomeText);
        // simple visual cue: add style class based on outcome
        lblWinLose.getStyleClass().removeAll("win-text", "lose-text");
        if (outcomeText.toLowerCase().contains("win")) {
            lblWinLose.getStyleClass().add("win-text");
        } else if (outcomeText.toLowerCase().contains("lose")) {
            lblWinLose.getStyleClass().add("lose-text");
        }
    }

    // Convenience method for arena to report a winner by name
    public void setWinnerName(String name) {
        if (name == null || name.isBlank()) {
            setWinLoseText("You Lose!");
        } else {
            setWinLoseText(name + " wins!");
        }
    }
}