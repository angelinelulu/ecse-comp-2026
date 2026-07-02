package com.kirbken.controllers;

import com.kirbken.SceneManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class StoryController {
    private final SceneManager manager;

    public StoryController(SceneManager manager) {
        this.manager = manager;
    }

    public Scene build() {
        Label storyText = new Label(
            // TODO: replace with real storyline text
            "A challenger has appeared...\n\nWill you rise to the fight?"
        );
        storyText.setStyle("-fx-font-size: 20px;");
        storyText.setTextAlignment(TextAlignment.CENTER);
        storyText.setWrapText(true);

        Button continueButton = new Button("Continue");
        // Do this after implementing the arena scene
        //  continueButton.setOnAction(e -> manager.goToArena());

        VBox root = new VBox(30, storyText, continueButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40;");

        return new Scene(root, 800, 600);
    }
}