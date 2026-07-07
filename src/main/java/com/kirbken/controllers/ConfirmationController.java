package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ConfirmationController implements FxController {
    private SceneManager manager;
    private CharacterProfile pendingProfile;

    @FXML private ImageView previewImage;
    @FXML private Label characterNameLabel;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    public void setProfile(CharacterProfile profile) {
        this.pendingProfile = profile;
        GameState.setPendingCharacter(profile);

        characterNameLabel.setText(profile.getDisplayName());
        previewImage.setImage(new Image(getClass().getResourceAsStream(profile.getSpriteSheetPath())));
    }

    @FXML
    private void onConfirmClicked() {
        GameState.confirmCharacter();
        manager.goToArena();
    }

    @FXML
    private void onCancelClicked() {
        manager.goToCardScan(); //if they want to try different cards
    }
}