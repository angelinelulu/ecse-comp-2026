package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StoryController implements FxController {
    private SceneManager manager;

    @FXML private Label storyText;
    @FXML private VBox storyBox;
    @FXML private VBox cardPromptBox;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        storyText.setText(
            "The world once shimmered with pastel light and laughter — until Vexthorn cracked open beneath the Starwell. Now, the balance of light and darkness teeters on the edge. You are Luma (cousin of Kirby), a brave puffling born from the glow of the Starwell itself.\n" +
            "Your mission: restore the light, defeat Vexthorn, and bring harmony back to Dream Springs.\n" +
            "But beware — every step you take ripples through the realms. The shadows whisper, the skies tremble, and the fate of every Puffling rests in your tiny, glowing hands."
        );
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