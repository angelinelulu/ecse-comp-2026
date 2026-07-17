package com.kirbken.controllers;

import com.kirbken.SceneManager;
import com.kirbken.models.Question;
import com.kirbken.service.AlgebraCalcGenerator;
import com.kirbken.service.ArithmeticQuestionGenerator;
import com.kirbken.utils.KeyboardNavHelper;
import com.kirbken.utils.MusicManager;
import com.kirbken.utils.QuizManager;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class QuizModeSelectController implements FxController {

  private static final int QUESTIONS_PER_MATCH = 2; // matches ArenaController.QUESTIONS_PER_MATCH

  private SceneManager manager;
  private final MusicManager musicManager = MusicManager.getInstance();

  @FXML private Button basicButton;
  @FXML private Button intermediateButton;
  @FXML private Button customPdfButton;
  @FXML private Button backButton;

  @FXML
  public void initialize() {
      javafx.application.Platform.runLater(() ->
          KeyboardNavHelper.enableHorizontalNav(basicButton, basicButton, intermediateButton, customPdfButton, backButton)
      );
  }

  @Override
  public void setSceneManager(SceneManager manager) {
    this.manager = manager;
  }

  @FXML
  private void onBasicSelected(ActionEvent event) {
    musicManager.playSound("buttonClick", 0.5);

    List<Question> questions = ArithmeticQuestionGenerator.generate(QUESTIONS_PER_MATCH);
    QuizManager.getInstance().setQuestions(questions);
    QuizManager.getInstance().setQuizModeEnabled(true);

    manager.goToModeSelect();
  }

  @FXML
  private void onIntermediateSelected(ActionEvent event) {
      musicManager.playSound("buttonClick", 0.5);

      List<Question> questions = AlgebraCalcGenerator.generate(QUESTIONS_PER_MATCH);
      QuizManager.getInstance().setQuestions(questions);
      QuizManager.getInstance().setQuizModeEnabled(true);

      manager.goToModeSelect();
  }

  @FXML
  private void onCustomPdfSelected(ActionEvent event) {
    musicManager.playSound("buttonClick", 0.5);
    manager.goToQuizSetup();
  }

  @FXML
  private void onBack(ActionEvent event) {
    musicManager.playSound("buttonClick", 0.5);
    manager.goToStart();
  }
}