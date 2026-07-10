package com.kirbken.models;

import java.util.List;

public class Question {
    private final String prompt;
    private final List<String> options;
    private final int correctIndex;

    public Question(String prompt, List<String> options, int correctIndex) {
        this.prompt = prompt;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getPrompt() { return prompt; }
    public List<String> getOptions() { return options; }
    public boolean isCorrect(int chosenIndex) { return chosenIndex == correctIndex; }
}