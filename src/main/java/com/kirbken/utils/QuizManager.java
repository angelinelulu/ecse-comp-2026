package com.kirbken.utils;

import com.kirbken.models.Question;
import java.util.List;
import java.util.Random;


public class QuizManager {
    private static QuizManager instance;
    private List<Question> questions;
    private boolean quizModeEnabled = false;

    private QuizManager() {}

    public static QuizManager getInstance() {
        if (instance == null) instance = new QuizManager();
        return instance;
    }

    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public boolean isQuizModeEnabled() { return quizModeEnabled; }
    public void setQuizModeEnabled(boolean enabled) { this.quizModeEnabled = enabled; }

    public Question getRandomQuestion() {
        if (questions == null || questions.isEmpty()) return null;
        return questions.get(new Random().nextInt(questions.size()));
    }
}