package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class ConfirmationController implements FxController {
    private SceneManager manager;
    private CharacterProfile pendingProfile;
    private boolean isForPlayer2 = false;

    @FXML private ImageView previewImage;
    @FXML private Label characterNameLabel;
    @FXML private Button yesButton;
    @FXML private Button noButton;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(yesButton, yesButton, noButton)
        );
    }

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    public void setProfile(CharacterProfile profile, boolean forPlayer2) {
        this.pendingProfile = profile;
        this.isForPlayer2 = forPlayer2;
        if (forPlayer2) {
            GameState.setPendingCharacterP2(profile);
        } else {
            GameState.setPendingCharacter(profile);
        }

        characterNameLabel.setText(profile.getDisplayName());

        var spriteUrl = getClass().getResource(profile.getSpriteSheetPath());
        if (spriteUrl != null) {
            previewImage.setImage(new Image(spriteUrl.toExternalForm()));
        } else {
            System.out.println("No sprite found at: " + profile.getSpriteSheetPath() + " - skipping image.");
            previewImage.setImage(null);
        }
    }

    @FXML
    private void onConfirmClicked() {
        if (isForPlayer2) {
            GameState.confirmCharacterP2();
            manager.goToArena();
        } else {
            GameState.confirmCharacter();
            if (GameState.getGameMode() == GameState.GameMode.MULTIPLAYER) {
                manager.goToPlayer2Prompt(); // ask P2 first, don't jump straight to camera
            } else {
                manager.goToArena();
            }
        }
    }

    @FXML
    private void onCancelClicked() {
        if (isForPlayer2) {
            manager.goToCardScanP2();
        } else {
            manager.goToCardScan();
        }
    }
}