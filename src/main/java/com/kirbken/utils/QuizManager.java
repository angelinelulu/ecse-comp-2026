package com.kirbken.utils;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuizManager {
    private static QuizManager instance;
    private List<Question> questions;
    private final List<Question> remainingQuestions = new ArrayList<>();
    private static final Random RANDOM = new Random();
    private boolean quizModeEnabled = false;

    private QuizManager() {}

    public static QuizManager getInstance() {
        if (instance == null) instance = new QuizManager();
        return instance;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        this.remainingQuestions.clear();
        if (questions != null) {
            this.remainingQuestions.addAll(questions);
        }
    }

    public boolean isQuizModeEnabled() { return quizModeEnabled; }
    public void setQuizModeEnabled(boolean enabled) { this.quizModeEnabled = enabled; }

    public Question getRandomQuestion() {
        if (remainingQuestions.isEmpty()) {
            if (questions == null || questions.isEmpty()) return null;
            remainingQuestions.addAll(questions); // reshuffle pool once exhausted
        }
        int index = RANDOM.nextInt(remainingQuestions.size());
        return remainingQuestions.remove(index);
    }
}