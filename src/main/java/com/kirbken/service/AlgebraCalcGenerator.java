package com.kirbken.service;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates randomized algebra/calculus questions using formula templates,
 * rather than a fixed hardcoded bank. Each call produces fresh numbers.
 */
public class AlgebraCalcGenerator {

    private static final Random RANDOM = new Random();

    public static List<Question> generate(int count) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            questions.add(generateOne());
        }
        return questions;
    }

    private static Question generateOne() {
        int templateType = RANDOM.nextInt(4);
        return switch (templateType) {
            case 0 -> generateLinearEquation();
            case 1 -> generatePowerDerivative();
            case 2 -> generateSlopeQuestion();
            default -> generateFactoring();
        };
    }

    /** ax + b = c, solve for x */
    private static Question generateLinearEquation() {
        int a = 2 + RANDOM.nextInt(8);       // 2-9
        int x = 1 + RANDOM.nextInt(10);      // the actual solution, 1-10
        int b = 1 + RANDOM.nextInt(20);      // 1-20
        int c = a * x + b;

        String prompt = "Solve for x: " + a + "x + " + b + " = " + c;
        return buildQuestionWithNumericDistractors(prompt, x);
    }

    /** derivative of x^n */
    private static Question generatePowerDerivative() {
        int n = 2 + RANDOM.nextInt(4); // exponent 2-5
        String prompt = "What is the derivative of x^" + n + "?";
        String correctAnswer = n + "x^" + (n - 1);

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.add((n - 1) + "x^" + n); // common mistake: swapped coefficient/exponent
        options.add(n + "x^" + n);        // forgot to reduce exponent
        options.add("x^" + (n - 1));      // forgot the coefficient

        return buildQuestionWithStringOptions(prompt, correctAnswer, options);
    }

    /** slope of y = mx + b */
    private static Question generateSlopeQuestion() {
        int m = 1 + RANDOM.nextInt(9);
        int b = RANDOM.nextInt(15);
        String prompt = "What is the slope of the line y = " + m + "x + " + b + "?";
        return buildQuestionWithNumericDistractors(prompt, m);
    }

    /** factor x^2 - n^2 = (x-n)(x+n) */
    private static Question generateFactoring() {
        int n = 2 + RANDOM.nextInt(9); // 2-10
        String prompt = "Factor: x^2 - " + (n * n);
        String correctAnswer = "(x-" + n + ")(x+" + n + ")";

        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.add("(x-" + (n * n) + ")(x+1)"); // wrong factor pairing
        options.add("(x-" + n + ")^2");           // forgot the sign difference
        options.add("(x+" + (n * n) + ")(x-1)");  // another wrong pairing

        return buildQuestionWithStringOptions(prompt, correctAnswer, options);
    }

    private static Question buildQuestionWithNumericDistractors(String prompt, int correctAnswer) {
        List<Integer> values = new ArrayList<>();
        values.add(correctAnswer);

        while (values.size() < 4) {
            int offset = 1 + RANDOM.nextInt(4);
            int distractor = RANDOM.nextBoolean() ? correctAnswer + offset : correctAnswer - offset;
            if (!values.contains(distractor)) {
                values.add(distractor);
            }
        }

        Collections.shuffle(values);

        List<String> optionStrings = new ArrayList<>();
        int correctIndex = 0;
        for (int i = 0; i < values.size(); i++) {
            optionStrings.add(String.valueOf(values.get(i)));
            if (values.get(i) == correctAnswer) {
                correctIndex = i;
            }
        }

        return new Question(prompt, optionStrings, correctIndex);
    }

    private static Question buildQuestionWithStringOptions(String prompt, String correctAnswer, List<String> options) {
        Collections.shuffle(options);
        int correctIndex = options.indexOf(correctAnswer);
        return new Question(prompt, options, correctIndex);
    }
}