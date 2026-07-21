package com.kirbken.controllers;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuizPopupController {

    private static final String DEFAULT_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String CORRECT_STYLE =
        "-fx-background-color: #007b33; -fx-text-fill: black; -fx-background-radius: 6; -fx-opacity: 0.7; -fx-font-size: 16px;";
    private static final String INCORRECT_STYLE =
        "-fx-background-color: #8d0e00; -fx-text-fill: black; -fx-background-radius: 6;-fx-opacity: 0.7; -fx-font-size: 16px;";
    private static final String OK_BUTTON_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String OK_BUTTON_HOVER_STYLE =
        "-fx-background-color: #a4a8a9; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";

    private static final String P2_BORDER = " -fx-border-color: #2288ff; -fx-border-width: 3px;";
    private static final String P1_BORDER = " -fx-border-color: #ff4444; -fx-border-width: 3px;";
    private static final String BOTH_BORDER = " -fx-border-color: #a233ff; -fx-border-width: 3px;";

    @FXML private StackPane overlay;
    @FXML private Label promptLabel;
    @FXML private VBox optionsBox;
    @FXML private Button okButton;

    private final List<Button> allButtons = new ArrayList<>();
    private Question question;
    private BiConsumer<Boolean, Integer> onAnswered;

    private int p1Index = 0;
    private int p2Index = 0;
    private boolean locked = false;
    private boolean wasCorrectAnswer = false;
    private int submittingPlayer = 0;

    public void setup(Question question, BiConsumer<Boolean, Integer> onAnswered) {
        this.question = question;
        this.onAnswered = onAnswered;

        promptLabel.setText(question.getPrompt());
        promptLabel.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 18));

        okButton.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 16));
        okButton.setStyle(OK_BUTTON_STYLE);
        okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_STYLE));
        okButton.setOnAction(e -> onAnswered.accept(wasCorrectAnswer, submittingPlayer));

        var options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            Button optionButton = new Button(options.get(i));
            optionButton.setId("quizOption" + i);
            optionButton.setPrefWidth(480);
            optionButton.setPrefHeight(44);
            optionButton.setFont(Font.font("Geist Pixel", 16));
            optionButton.setStyle(DEFAULT_STYLE);

            // Mouse click support (dev/testing convenience) — treated as Player 1 submitting directly
            optionButton.setOnAction(e -> submit(1, optionIndex));

            allButtons.add(optionButton);
            optionsBox.getChildren().add(optionButton);
        }

        renderHighlights();

        Platform.runLater(() -> {
            var scene = overlay.getScene();
            if (scene != null) {
                scene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }

    private void handleKeyPress(javafx.scene.input.KeyEvent e) {
        if (locked) {
            if (e.getCode() == KeyCode.F || e.getCode() == KeyCode.L
                || e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                okButton.fire();
            }
            return;
        }

        KeyCode code = e.getCode();
        int optionCount = allButtons.size();

        if (code == KeyCode.W) {
            p1Index = Math.max(0, p1Index - 1);
            renderHighlights();
        } else if (code == KeyCode.S) {
            p1Index = Math.min(optionCount - 1, p1Index + 1);
            renderHighlights();
        } else if (code == KeyCode.UP) {
            p2Index = Math.max(0, p2Index - 1);
            renderHighlights();
        } else if (code == KeyCode.DOWN) {
            p2Index = Math.min(optionCount - 1, p2Index + 1);
            renderHighlights();
        } else if (code == KeyCode.F) {
            submit(1, p1Index);
        } else if (code == KeyCode.L) {
            submit(2, p2Index);
        }
    }

    private void renderHighlights() {
        for (int i = 0; i < allButtons.size(); i++) {
            boolean isP1 = (p1Index == i);
            boolean isP2 = (p2Index == i);

            String style = DEFAULT_STYLE;
            if (isP1 && isP2) {
                style += BOTH_BORDER;
            } else if (isP1) {
                style += P1_BORDER;
            } else if (isP2) {
                style += P2_BORDER;
            }
            allButtons.get(i).setStyle(style);
        }
    }

    private void submit(int player, int chosenIndex) {
        if (locked) return;
        locked = true;
        submittingPlayer = player;
        wasCorrectAnswer = question.isCorrect(chosenIndex);

        for (int j = 0; j < allButtons.size(); j++) {
            Button b = allButtons.get(j);
            b.setStyle(question.isCorrect(j) ? CORRECT_STYLE : INCORRECT_STYLE);
            b.setDisable(true);
        }

        okButton.setVisible(true);
        okButton.setManaged(true);
    }

    public StackPane getOverlay() {
        return overlay;
    }
}