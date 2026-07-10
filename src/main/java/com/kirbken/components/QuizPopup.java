package com.kirbken.components;

import com.kirbken.models.Question;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A programmatically-built overlay shown mid-match when quiz mode triggers a question.
 * Not tied to an FXML file — built and added directly to the Arena's root pane, then
 * removed once the player answers.
 */
public class QuizPopup {

  private final StackPane overlay;

  public QuizPopup(Question question, Consumer<Boolean> onAnswered) {
    overlay = new StackPane();
    overlay.setId("quizOverlay"); // used by ArenaController to find + remove this node
    overlay.setPrefSize(1280, 720); // TODO: match your Arena scene's actual dimensions
    overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

    VBox card = new VBox(20);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(40));
    card.setMaxWidth(600);
    card.setStyle(
        "-fx-background-color: #1a1a1a;"
            + "-fx-background-radius: 12;"
            + "-fx-border-color: #00d0ff;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 12;");

    Label promptLabel = new Label(question.getPrompt());
    promptLabel.setWrapText(true);
    promptLabel.setTextFill(Color.WHITE);
    promptLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
    promptLabel.setMaxWidth(520);
    promptLabel.setAlignment(Pos.CENTER);

    VBox optionsBox = new VBox(12);
    optionsBox.setAlignment(Pos.CENTER);

    var options = question.getOptions();
    for (int i = 0; i < options.size(); i++) {
      final int chosenIndex = i;
      Button optionButton = new Button(options.get(i));
      optionButton.setPrefWidth(480);
      optionButton.setPrefHeight(44);
      optionButton.setFont(Font.font("System", 16));
      optionButton.setStyle(
          "-fx-background-color: #2a2a2a;"
              + "-fx-text-fill: white;"
              + "-fx-background-radius: 6;"
              + "-fx-cursor: hand;");

      optionButton.setOnMouseEntered(
          e -> optionButton.setStyle(
              "-fx-background-color: #3a3a3a;"
                  + "-fx-text-fill: white;"
                  + "-fx-background-radius: 6;"
                  + "-fx-cursor: hand;"));
      optionButton.setOnMouseExited(
          e -> optionButton.setStyle(
              "-fx-background-color: #2a2a2a;"
                  + "-fx-text-fill: white;"
                  + "-fx-background-radius: 6;"
                  + "-fx-cursor: hand;"));

      optionButton.setOnAction(
          e -> {
            boolean wasCorrect = question.isCorrect(chosenIndex);
            // brief visual feedback before closing
            optionButton.setStyle(
                (wasCorrect ? "-fx-background-color: #2ecc71;" : "-fx-background-color: #e74c3c;")
                    + "-fx-text-fill: white; -fx-background-radius: 6;");
            disableAllButtons(optionsBox);

            // short delay so the player sees the correct/incorrect flash, then resume
            javafx.animation.PauseTransition pause =
                new javafx.animation.PauseTransition(javafx.util.Duration.millis(600));
            pause.setOnFinished(evt -> onAnswered.accept(wasCorrect));
            pause.play();
          });

      optionsBox.getChildren().add(optionButton);
    }

    card.getChildren().addAll(promptLabel, optionsBox);
    overlay.getChildren().add(card);
  }

  private void disableAllButtons(VBox optionsBox) {
    for (var node : optionsBox.getChildren()) {
      if (node instanceof Button b) {
        b.setDisable(true);
      }
    }
  }

  /** The root node to add to (and later remove from) the Arena's root pane. */
  public StackPane getOverlay() {
    return overlay;
  }
}