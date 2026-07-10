package com.kirbken.service;

import com.kirbken.models.Question;
import java.net.http.*;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class QuestionGenerator {
    private static final String API_KEY = System.getenv("ANTHROPIC_API_KEY"); // need to add API key to environment variables
    private static final String API_URL = "https://api.anthropic.com/v1/messages";

    public static List<Question> generate(String pdfText, int numQuestions) throws Exception {
        String prompt = """
            Based on the following text, generate %d multiple choice quiz questions.
            Respond ONLY with valid JSON, no markdown, no preamble, in this exact format:
            [{"prompt": "...", "options": ["...","...","...","..."], "correctIndex": 0}]

            Text:
            %s
            """.formatted(numQuestions, pdfText);

        JSONObject body = new JSONObject()
            .put("model", "claude-sonnet-4-5")
            .put("max_tokens", 1500)
            .put("messages", new JSONArray()
                .put(new JSONObject().put("role", "user").put("content", prompt)));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("x-api-key", API_KEY)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());

        String rawText = new JSONObject(response.body())
            .getJSONArray("content").getJSONObject(0).getString("text");

        JSONArray arr = new JSONArray(rawText);
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject q = arr.getJSONObject(i);
            List<String> opts = new ArrayList<>();
            q.getJSONArray("options").forEach(o -> opts.add((String) o));
            questions.add(new Question(q.getString("prompt"), opts, q.getInt("correctIndex")));
        }
        return questions;
    }
}