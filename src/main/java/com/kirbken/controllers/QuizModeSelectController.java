package com.kirbken.controllers;

import com.kirbken.SceneManager;
import com.kirbken.models.Question;
import com.kirbken.service.AlgebraCalcQuestionBank;
import com.kirbken.service.ArithmeticQuestionGenerator;
import com.kirbken.utils.MusicManager;
import com.kirbken.utils.QuizManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class QuizModeSelectController implements FxController {

  private static final int QUESTIONS_PER_MATCH = 2; // matches ArenaController.QUESTIONS_PER_MATCH

  private SceneManager manager;
  private final MusicManager musicManager = MusicManager.getInstance();

  @Override
  public void setSceneManager(SceneManager manager) {
    this.manager = manager;
  }

  @FXML
  private void onBasicSelected(MouseEvent event) {
    musicManager.playSound("buttonClick", 0.5);

    List<Question> questions = ArithmeticQuestionGenerator.generate(QUESTIONS_PER_MATCH);
    QuizManager.getInstance().setQuestions(questions);
    QuizManager.getInstance().setQuizModeEnabled(true);

    manager.goToArena();
  }

  @FXML
  private void onIntermediateSelected(MouseEvent event) {
    musicManager.playSound("buttonClick", 0.5);

    List<Question> questions = AlgebraCalcQuestionBank.getAll();
    QuizManager.getInstance().setQuestions(questions);
    QuizManager.getInstance().setQuizModeEnabled(true);

    manager.goToArena();
  }

  @FXML
  private void onCustomPdfSelected(MouseEvent event) {
    musicManager.playSound("buttonClick", 0.5);
    manager.goToQuizSetup(); // existing PDF upload + AI generation page
  }

  @FXML
  private void onBack(MouseEvent event) {
    musicManager.playSound("buttonClick", 0.5);
    manager.goToStart(); // TODO: confirm this matches your actual SceneManager method name
  }
}