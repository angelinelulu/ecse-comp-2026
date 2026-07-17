package com.kirbken.controllers;

import com.kirbken.SceneManager;
import com.kirbken.models.Question;
import com.kirbken.service.PdfTextExtractor;
import com.kirbken.service.QuestionGenerator;
import com.kirbken.utils.KeyboardNavHelper;
import com.kirbken.utils.MusicManager;
import com.kirbken.utils.QuizManager;
import java.io.File;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class QuizSetupController implements FxController {

  @FXML private Label fileNameLabel;
  @FXML private Label statusLabel;
  @FXML private Button chooseFileButton;
  @FXML private Button generateButton;
  @FXML private Button storyButton;
  @FXML private Button backButton;

  private SceneManager manager;
  private final MusicManager musicManager = MusicManager.getInstance();

  private File selectedPdf;

  @FXML
  public void initialize() {
    generateButton.setDisable(true);
    storyButton.setDisable(true);
    statusLabel.setText("Choose a PDF to generate quiz questions from.");

    javafx.application.Platform.runLater(() ->
        KeyboardNavHelper.enableHorizontalNav(chooseFileButton, chooseFileButton, generateButton, storyButton, backButton)
    );
  }

  @Override
  public void setSceneManager(SceneManager manager) {
    this.manager = manager;
  }

  @FXML
  private void onChooseFile(ActionEvent event) {
    musicManager.playSound("buttonClick", 0.5);

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select a PDF");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    Stage stage = (Stage) chooseFileButton.getScene().getWindow();
    File file = fileChooser.showOpenDialog(stage);

    if (file != null) {
      selectedPdf = file;
      fileNameLabel.setText(file.getName());
      generateButton.setDisable(false);
      storyButton.setDisable(true);
      statusLabel.setText("Ready to generate questions.");
    }
  }

  @FXML
  private void onGenerateQuestions(ActionEvent event) {
    if (selectedPdf == null) {
      return;
    }

    musicManager.playSound("buttonClick", 0.5);
    setBusy(true, "Reading PDF and generating questions…");

    Task<List<Question>> task = new Task<>() {
      @Override
      protected List<Question> call() throws Exception {
        String text = PdfTextExtractor.extractText(selectedPdf);
        return QuestionGenerator.generate(text, 8); // 8 questions per match
      }
    };

    task.setOnSucceeded(
        e -> {
          List<Question> questions = task.getValue();
          QuizManager.getInstance().setQuestions(questions);
          QuizManager.getInstance().setQuizModeEnabled(true);

          setBusy(false, questions.size() + " questions generated. Ready to fight!");
          storyButton.setDisable(false);
        });

    task.setOnFailed(
        e -> {
          Throwable ex = task.getException();
          System.out.println("Question generation failed: " + ex);
          setBusy(false, "Something went wrong generating questions. Try a different PDF.");
          storyButton.setDisable(true);
        });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  @FXML
  private void onStoryButtonClicked(ActionEvent event) {
      musicManager.playSound("buttonClick", 0.5);
      manager.goToModeSelect();
  }

  @FXML
  private void onBack(ActionEvent event) {
    musicManager.playSound("buttonClick", 0.5);
    QuizManager.getInstance().setQuizModeEnabled(false);
    manager.goToQuizModeSelect(); // 
  }

  private void setBusy(boolean busy, String message) {
    Platform.runLater(
        () -> {
          chooseFileButton.setDisable(busy);
          generateButton.setDisable(busy || selectedPdf == null);
          statusLabel.setText(message);
        });
  }
}