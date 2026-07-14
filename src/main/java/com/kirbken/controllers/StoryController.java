package com.kirbken.controllers;

import com.kirbken.CharacterRegistry;
import com.kirbken.GameState;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StoryController implements FxController {
    private static final String STORY_TEXT =
        "The world once shimmered with pastel light and laughter — until Vexthorn cracked open beneath the Starwell. Now, the balance of light and darkness teeters on the edge. You are Luma (cousin of Kirby), a brave puffling born from the glow of the Starwell itself.\n\n" +
        "Your mission: restore the light, defeat Vexthorn, and bring harmony back to Dream Springs.\n\n" +
        "But beware — every step you take ripples through the realms. The shadows whisper, the skies tremble, and the fate of every Puffling rests in your tiny, glowing hands.";

    private SceneManager manager;
    private Timeline typingTimeline;
    private boolean typingComplete;

    @FXML private Label storyText;
    @FXML private VBox storyBox;
    @FXML private VBox cardPromptBox;
    @FXML private Button enterButton;
    @FXML private ScrollPane storyScrollPane;
    @FXML private Label cardPromptLabel;
    @FXML private Button yesButton;
    @FXML private Button noButton;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        typingComplete = false;
        storyText.setText("");
        enterButton.setText("Skip");

        storyBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!typingComplete) {
                finishTyping();
                event.consume();
            }
        });

        typingTimeline = new Timeline();
        for (int index = 0; index < STORY_TEXT.length(); index++) {
            final int currentIndex = index;
            typingTimeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(25L * (index + 1)), event -> {
                    storyText.setText(STORY_TEXT.substring(0, currentIndex + 1));
                    scrollStoryToBottom();
                    if (currentIndex + 1 == STORY_TEXT.length()) {
                        enterButton.setDisable(false);
                    }
                })
            );
        }
        typingTimeline.setOnFinished(event -> finishTyping());
        typingTimeline.playFromStart();

        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(yesButton, yesButton, noButton)
        );
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

        String playerLabel = (GameState.getGameMode() == GameState.GameMode.MULTIPLAYER) ? "Player 1" : " ";
        cardPromptLabel.setText(playerLabel + " would you like to insert/scan your existing Kirby card?");
    }

    private void finishTyping() {
        if (typingComplete) {
            return;
        }

        if (typingTimeline != null) {
            typingTimeline.stop();
        }

        storyText.setText(STORY_TEXT);
        scrollStoryToBottom();
        typingComplete = true;
        enterButton.setText(" → ");
    }

    private void scrollStoryToBottom() {
        Platform.runLater(() -> storyScrollPane.setVvalue(1.0));
    }

    @FXML
    private void onNoClicked() {
        if (GameState.getGameMode() == GameState.GameMode.MULTIPLAYER) {
            manager.goToConfirmation(CharacterRegistry.getDefault(), false);
        } else {
            manager.goToConfirmation(CharacterRegistry.getDefault(), false);
        }
    }

    @FXML
    private void onYesClicked() {
        manager.goToCardScan();
    }
}