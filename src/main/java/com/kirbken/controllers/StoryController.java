package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StoryController implements FxController {
    private static final String STORY_TEXT =
        "The world once shimmered with pastel light and laughter — until Vexthorn cracked open beneath the Starwell. Now, the balance of light and darkness teeters on the edge. You are Luma (cousin of Kirby), a brave puffling born from the glow of the Starwell itself.\n" +
        "Your mission: restore the light, defeat Vexthorn, and bring harmony back to Dream Springs.\n" +
        "But beware — every step you take ripples through the realms. The shadows whisper, the skies tremble, and the fate of every Puffling rests in your tiny, glowing hands.";

    private SceneManager manager;
    private Timeline typingTimeline;
    private boolean typingComplete;

    @FXML private Label storyText;
    @FXML private VBox storyBox;
    @FXML private VBox cardPromptBox;
    @FXML private Button enterButton;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        typingComplete = false;
        storyText.setText("");
        enterButton.setText("Skip");

        typingTimeline = new Timeline();
        typingTimeline.setCycleCount(STORY_TEXT.length());
        for (int index = 0; index < STORY_TEXT.length(); index++) {
            final int currentIndex = index;
            typingTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(25L * (index + 1)), event -> {
                    storyText.setText(STORY_TEXT.substring(0, currentIndex + 1));
                    if (currentIndex + 1 == STORY_TEXT.length()) {
                        enterButton.setDisable(false);
                    }
                })
            );
        }
        typingTimeline.setOnFinished(event -> finishTyping());
        typingTimeline.playFromStart();
    }

    @FXML
    private void onEnterClicked() {
        if (!typingComplete) {
            finishTyping();
            return;
        }

        if (typingTimeline != null) {
            typingTimeline.stop();
        }
        storyBox.setVisible(false);
        storyBox.setManaged(false);
        cardPromptBox.setVisible(true);
        cardPromptBox.setManaged(true);
    }

    private void finishTyping() {
        if (typingComplete) {
            return;
        }

        if (typingTimeline != null) {
            typingTimeline.stop();
        }

        storyText.setText(STORY_TEXT);
        typingComplete = true;
        enterButton.setText(" → ");
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