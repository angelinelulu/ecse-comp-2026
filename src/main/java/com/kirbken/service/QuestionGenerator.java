package com.kirbken.service;

import com.kirbken.models.Question;
import java.util.ArrayList;
import java.util.List;

/**
 * STUB VERSION — returns hardcoded questions instead of calling the Anthropic API.
 * Use this to test the full quiz popup / pause / resume flow before setting up
 * an ANTHROPIC_API_KEY. Once your key is ready, swap the body of generate()
 * back to the real API call (see the version with HttpClient/JSONObject).
 */
public class QuestionGenerator {

  public static List<Question> generate(String pdfText, int numQuestions) throws Exception {
    // Ignoring pdfText and numQuestions for now — always returns the same 3 test questions.
    List<Question> questions = new ArrayList<>();

    questions.add(new Question(
        "What data structure does a B+ tree primarily optimize for?",
        List.of("Range queries", "Random access", "Recursion depth", "Hash collisions"),
        0));

    questions.add(new Question(
        "Which JavaFX class is used to run a repeating game loop?",
        List.of("Timeline", "AnimationTimer", "ScheduledService", "PauseTransition"),
        1));

    questions.add(new Question(
        "In the Banker's Algorithm, what does 'safe state' mean?",
        List.of(
            "All processes have finished",
            "No deadlock can occur under some execution order",
            "Memory is fully allocated",
            "All mutexes are unlocked"),
        1));

    return questions;
  }
}