package com.kirbken.controllers;

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
    private void onYesClicked() {
        manager.goToCardScan(); // placeholder
    }

    @FXML
    private void onNoClicked() {
        manager.useDefaultCharacter();
    }
}