package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;
import com.kirbken.components.CardDispenser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WinController implements FxController {
    private SceneManager manager;

    @FXML private Label lblVictory;
    @FXML private Label lblRank;
    @FXML private Label lblScore;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;
    @FXML private ImageView imgCharacter;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        CardDispenser.getInstance().dispenseCard();

        java.net.URL fontUrl = getClass().getResource("/fonts/GeistPixelRegular.ttf");
        if (fontUrl != null) {
            javafx.scene.text.Font.loadFont(fontUrl.toExternalForm(), 28);
        }
        javafx.application.Platform.runLater(() -> {
            KeyboardNavHelper.enableHorizontalNav(btnHome, btnHome, btnPlayAgain);
        });
    }

    @FXML
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

    // Called when reporting the winner; pulls the winner's portrait from GameState.
    public void setWinnerName(String name) {
        setWinLabel((name == null || name.isBlank()) ? "You Win!" : name + " wins!");
        setCharacterImage(GameState.getSelectedCharacter());
    }

    public void setCharacterImage(CharacterProfile profile) {
        if (profile == null || imgCharacter == null) {
            System.out.println("DEBUG: profile=" + profile + " imgCharacter=" + imgCharacter);
            return;
        }
        java.net.URL url = getClass().getResource(profile.getSpriteSheetPath());
        System.out.println("DEBUG: resolving " + profile.getSpriteSheetPath() + " -> " + url);
        if (url != null) {
            imgCharacter.setImage(new Image(url.toExternalForm()));
        }
    }
}