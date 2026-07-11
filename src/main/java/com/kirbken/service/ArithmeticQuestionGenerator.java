package com.kirbken.service;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

 
// Generates simple arithmetic questions (addition, subtraction, multiplication)

public class ArithmeticQuestionGenerator {

  private static final Random RANDOM = new Random();

  public static List<Question> generate(int count) {
    List<Question> questions = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      questions.add(generateOne());
    }
    return questions;
  }

  private static Question generateOne() {
    int a = 1 + RANDOM.nextInt(20); // 1-20
    int b = 1 + RANDOM.nextInt(20);
    int operation = RANDOM.nextInt(3); // 0 = add, 1 = subtract, 2 = multiply

    String prompt;
    int correctAnswer;

    switch (operation) {
      case 0 -> {
        prompt = "What is " + a + " + " + b + "?";
        correctAnswer = a + b;
      }
      case 1 -> {
        // ensure a non-negative result for a cleaner question
        int larger = Math.max(a, b);
        int smaller = Math.min(a, b);
        prompt = "What is " + larger + " - " + smaller + "?";
        correctAnswer = larger - smaller;
      }
      default -> {
        int x = 1 + RANDOM.nextInt(12); // keep multiplication friendlier (1-12)
        int y = 1 + RANDOM.nextInt(12);
        prompt = "What is " + x + " x " + y + "?";
        correctAnswer = x * y;
      }
    }

    return buildQuestionWithDistractors(prompt, correctAnswer);
  }

  /** Builds 3 wrong answers close to the correct one, shuffles all 4, and tracks the correct index. */
  private static Question buildQuestionWithDistractors(String prompt, int correctAnswer) {
    List<Integer> optionValues = new ArrayList<>();
    optionValues.add(correctAnswer);

    while (optionValues.size() < 4) {
      int offset = 1 + RANDOM.nextInt(5); // +/- 1 to 5 away from the correct answer
      int distractor = RANDOM.nextBoolean() ? correctAnswer + offset : correctAnswer - offset;
      if (!optionValues.contains(distractor)) {
        optionValues.add(distractor);
      }
    }

    Collections.shuffle(optionValues);

    List<String> optionStrings = new ArrayList<>();
    int correctIndex = 0;
    for (int i = 0; i < optionValues.size(); i++) {
      optionStrings.add(String.valueOf(optionValues.get(i)));
      if (optionValues.get(i) == correctAnswer) {
        correctIndex = i;
      }
    }

    return new Question(prompt, optionStrings, correctIndex);
  }
}