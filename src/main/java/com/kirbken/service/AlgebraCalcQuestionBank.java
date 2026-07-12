package com.kirbken.service;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.List;

/**
 * A fixed bank of algebra/calculus questions for Intermediate difficulty.
 * QuizManager.getRandomQuestion() picks randomly from
 * whatever subset gets loaded into it (see getAll()).
 *
 * Add more questions here any time — no other code needs to change.
 */
public class AlgebraCalcQuestionBank {

  public static List<Question> getAll() {
    List<Question> questions = new ArrayList<>();

    questions.add(new Question(
        "Solve for x: 2x + 6 = 14",
        List.of("2", "4", "6", "8"),
        1));

    questions.add(new Question(
        "What is the derivative of x^2?",
        List.of("x", "2x", "x^2", "2"),
        1));

    questions.add(new Question(
        "Factor: x^2 - 9",
        List.of("(x-3)(x+3)", "(x-9)(x+1)", "(x-3)^2", "(x+9)(x-1)"),
        0));

    questions.add(new Question(
        "What is the derivative of a constant (e.g. 5)?",
        List.of("5", "1", "0", "x"),
        2));

    questions.add(new Question(
        "Solve for x: 3(x - 2) = 12",
        List.of("4", "6", "8", "2"),
        1));

    questions.add(new Question(
        "What is the slope of the line y = 3x + 7?",
        List.of("7", "3", "1", "10"),
        1));

    questions.add(new Question(
        "What is the integral of 2x dx?",
        List.of("x^2 + C", "2x^2 + C", "x + C", "2 + C"),
        0));

    questions.add(new Question(
        "Simplify: (x^2)(x^3)",
        List.of("x^5", "x^6", "2x^5", "x^2"),
        0));

    return questions;
  }
}