package com.kirbken.controllers;

import com.kirbken.models.Question;
import com.kirbken.utils.KeyboardNavHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuizPopupController {

    private static final String DEFAULT_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String HOVER_STYLE =
        "-fx-background-color: #a4a8a9; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String CORRECT_STYLE =
        "-fx-background-color: #007b33; -fx-text-fill: black; -fx-background-radius: 6;";
    private static final String INCORRECT_STYLE =
        "-fx-background-color: #8d0e00; -fx-text-fill: black; -fx-background-radius: 6;";
    private static final String OK_BUTTON_STYLE =
        "-fx-background-color: #ffffff; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String OK_BUTTON_HOVER_STYLE =
        "-fx-background-color: #a4a8a9; -fx-text-fill: black; -fx-background-radius: 6; -fx-cursor: hand;";
    private static final String FOCUS_BORDER =
        " -fx-border-color: yellow; -fx-border-width: 3px;";

    @FXML private StackPane overlay;
    @FXML private Label promptLabel;
    @FXML private VBox optionsBox;
    @FXML private Button okButton;

    private final List<Button> allButtons = new ArrayList<>();
    private final boolean[] wasCorrectHolder = new boolean[1];

    public void setup(Question question, Consumer<Boolean> onAnswered) {
        promptLabel.setText(question.getPrompt());
        promptLabel.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 18));

        okButton.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 16));
        okButton.setStyle(OK_BUTTON_STYLE);
        okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_STYLE));
        okButton.focusedProperty().addListener((obs, was, isFocused) ->
            okButton.setStyle(isFocused ? OK_BUTTON_STYLE + FOCUS_BORDER : OK_BUTTON_STYLE));

        okButton.setOnAction(e -> onAnswered.accept(wasCorrectHolder[0]));

        var options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            Button optionButton = new Button(options.get(i));
            optionButton.setId("quizOption" + i);
            optionButton.setPrefWidth(480);
            optionButton.setPrefHeight(44);
            optionButton.setFont(Font.font("Geist Pixel", 16));
            optionButton.setStyle(DEFAULT_STYLE);

            optionButton.setOnMouseEntered(e -> {
                if (!optionButton.isDisable()) optionButton.setStyle(HOVER_STYLE);
            });
            optionButton.setOnMouseExited(e -> {
                if (!optionButton.isDisable()) optionButton.setStyle(DEFAULT_STYLE);
            });
            optionButton.focusedProperty().addListener((obs, was, isFocused) -> {
                if (!optionButton.isDisable()) {
                    optionButton.setStyle(isFocused ? DEFAULT_STYLE + FOCUS_BORDER : DEFAULT_STYLE);
                }
            });

            optionButton.setOnAction(e -> {
                wasCorrectHolder[0] = question.isCorrect(optionIndex);

                for (int j = 0; j < allButtons.size(); j++) {
                    Button b = allButtons.get(j);
                    b.setStyle(question.isCorrect(j) ? CORRECT_STYLE : INCORRECT_STYLE);
                    b.setDisable(true);
                }

                okButton.setVisible(true);
                okButton.setManaged(true);

                Platform.runLater(() -> KeyboardNavHelper.enableVerticalNav(okButton, okButton));
            });

            allButtons.add(optionButton);
            optionsBox.getChildren().add(optionButton);
        }

        Platform.runLater(() ->
            KeyboardNavHelper.enableVerticalNav(optionsBox, allButtons.toArray(new Button[0]))
        );
    }

    public StackPane getOverlay() {
        return overlay;
    }
}