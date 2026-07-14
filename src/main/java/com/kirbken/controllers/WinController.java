package com.kirbken.controllers;

import com.kirbken.GameState;
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
    
    @FXML 
    public void initialize() {
        java.net.URL fontUrl = getClass().getResource("/fonts/GeistPixelRegular.ttf");
        if (fontUrl != null) {
            javafx.scene.text.Font.loadFont(fontUrl.toExternalForm(), 28);
        }
    }
    
    public void onPlayAgain(ActionEvent event) {
        // Play again should return to the arena to rematch
        manager.goToArena();
    }

    // if player clicks home, go to start
    @FXML
    public void onHome(ActionEvent event) {
        GameState.resetRounds();
        GameState.unlockForNewRound();
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

    public void setRankLabel(String text) {
        lblRank.setText(text == null || text.isBlank() ? "Rank: N/A" : text);
    }

    public void setScoreLabel(String text) {
        lblScore.setText(text == null || text.isBlank() ? "Score: 0" : text);
    }

    // Convenience method for arena to report a winner by name.
    public void setWinnerName(String name) {
        setWinLabel((name == null || name.isBlank()) ? "You Win!" : name + " wins!");
    }
}