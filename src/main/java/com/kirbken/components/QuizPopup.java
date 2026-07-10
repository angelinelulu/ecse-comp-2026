package com.kirbken.components;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.List;
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
 * removed once the player confirms with the OK button.
 *
 * On selecting an option: the correct option always turns green and every other option
 * turns red, regardless of which one the player picked, so the right answer is always
 * revealed. An "OK" button then appears — the match only resumes once the player clicks
 * it, giving them as long as they want to read the reveal.
 */
public class QuizPopup {

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

  static {
    java.net.URL fontUrl = QuizPopup.class.getResource("/fonts/GeistPixelRegular.ttf");
    if (fontUrl != null) {
      Font loaded = Font.loadFont(fontUrl.toExternalForm(), 16);
      if (loaded != null) {
        System.out.println("QuizPopup: loaded font family name = \"" + loaded.getFamily() + "\"");
      } else {
        System.out.println("QuizPopup: found the file but JavaFX failed to parse it as a font.");
      }
    } else {
      System.out.println("QuizPopup: could not find /fonts/GeistPixelRegular.ttf on classpath.");
    }
  }

  private final StackPane overlay;

  public QuizPopup(Question question, Consumer<Boolean> onAnswered) {
    overlay = new StackPane();
    overlay.setId("quizOverlay"); // used by ArenaController to find + remove this node
    overlay.setPrefSize(1280, 720); 
    overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

    VBox card = new VBox(20);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(40));
    card.setMaxWidth(600);
    card.setStyle(
        "-fx-background-color: #f2abe2;"
            + "-fx-background-radius: 12;"
            + "-fx-border-color:  #f2abe2;"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 12;");

    Label promptLabel = new Label(question.getPrompt());
    promptLabel.setWrapText(true);
    promptLabel.setTextFill(Color.BLACK);
    promptLabel.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 22));
    promptLabel.setMaxWidth(520);
    promptLabel.setAlignment(Pos.CENTER);

    VBox optionsBox = new VBox(12);
    optionsBox.setAlignment(Pos.CENTER);

    // OK button — hidden until an option is picked, then confirms and resumes the match.
    Button okButton = new Button("OK");
    okButton.setPrefWidth(160);
    okButton.setPrefHeight(40);
    okButton.setFont(Font.font("Geist Pixel", FontWeight.BOLD, 16));
    okButton.setStyle(OK_BUTTON_STYLE);
    okButton.setVisible(false);
    okButton.setManaged(false); // don't reserve layout space until it's shown

    okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
    okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_STYLE));

    // Holds the outcome of whichever option was picked, so the OK button's own
    // handler (fired separately, whenever the player is ready) knows what to report.
    final boolean[] wasCorrectHolder = new boolean[1];

    okButton.setOnAction(e -> onAnswered.accept(wasCorrectHolder[0]));

    var options = question.getOptions();
    List<Button> allButtons = new ArrayList<>();

    for (int i = 0; i < options.size(); i++) {
      final int optionIndex = i;
      Button optionButton = new Button(options.get(i));
      optionButton.setPrefWidth(480);
      optionButton.setPrefHeight(44);
      optionButton.setFont(Font.font("Geist Pixel", 16));
      optionButton.setStyle(DEFAULT_STYLE);

      optionButton.setOnMouseEntered(e -> {
        if (!optionButton.isDisable()) {
          optionButton.setStyle(HOVER_STYLE);
        }
      });
      optionButton.setOnMouseExited(e -> {
        if (!optionButton.isDisable()) {
          optionButton.setStyle(DEFAULT_STYLE);
        }
      });

      optionButton.setOnAction(e -> {
        wasCorrectHolder[0] = question.isCorrect(optionIndex);

        // Reveal the correct answer in green and every other option in red,
        // regardless of which one was clicked.
        for (int j = 0; j < allButtons.size(); j++) {
          Button b = allButtons.get(j);
          b.setStyle(question.isCorrect(j) ? CORRECT_STYLE : INCORRECT_STYLE);
          b.setDisable(true);
        }

        // Reveal the OK button — match stays paused until the player clicks it.
        okButton.setVisible(true);
        okButton.setManaged(true);
      });

      allButtons.add(optionButton);
      optionsBox.getChildren().add(optionButton);
    }

    card.getChildren().addAll(promptLabel, optionsBox, okButton);
    overlay.getChildren().add(card);
  }

  /** The root node to add to (and later remove from) the Arena's root pane. */
  public StackPane getOverlay() {
    return overlay;
  }
}