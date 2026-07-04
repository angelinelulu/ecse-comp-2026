package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class StoryController implements FxController {
    private SceneManager manager;

    @FXML private VBox storyBox;
    @FXML private VBox cardPromptBox;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void onEnterClicked() {
        storyBox.setVisible(false);
        storyBox.setManaged(false);
        cardPromptBox.setVisible(true);
        cardPromptBox.setManaged(true);
    }

    @FXML
    private void onNoClicked() {
        manager.goToConfirmation(CharacterRegistry.getDefault());
    }

    @FXML
    private void onYesClicked() {
        manager.goToCardScan();
    }
}