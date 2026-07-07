package com.kirbken.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import com.kirbken.models.Character;
import com.kirbken.models.Fight;

import java.util.Set;
import java.util.HashSet;

public class ArenaController {

    @FXML private ImageView p1Sprite, p2Sprite;
    @FXML private Region p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7;
    @FXML private Region p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7;
    @FXML private Pane rootPane;

    private Character p1, p2;
    private Fight fight;
    private Region[] p1Segments, p2Segments;

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private AnimationTimer timer;

    @FXML
    public void initialize() {
        p1 = new Character(p1Sprite, 200, 450, true);   // P1 starts facing right (toward P2)
        p2 = new Character(p2Sprite, 900, 450, false);  // P2 starts facing left (toward P1)
        fight = new Fight(p1, p2);

        p1Segments = new Region[]{p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7};
        p2Segments = new Region[]{p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7};

        startGameLoop();
    }

    public void setupInput(Scene scene) {
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
    }

    private void startGameLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handleInput();
                fight.update();
                updateHealthBars();

                if (fight.isOver()) {
                    stop();
                    showWinner(fight.getWinner());
                }
            }
        };
        timer.start();
    }

    private void handleInput() {
        if (activeKeys.contains(KeyCode.A)) p1.moveLeft();
        if (activeKeys.contains(KeyCode.D)) p1.moveRight();
        p1.setAttacking(activeKeys.contains(KeyCode.F));

        if (activeKeys.contains(KeyCode.LEFT)) p2.moveLeft();
        if (activeKeys.contains(KeyCode.RIGHT)) p2.moveRight();
        p2.setAttacking(activeKeys.contains(KeyCode.L));
    }

    private void updateHealthBars() {
        int p1Lit = (int) Math.ceil((p1.getHealth() / 100.0) * 8);
        int p2Lit = (int) Math.ceil((p2.getHealth() / 100.0) * 8);

        for (int i = 0; i < p1Segments.length; i++) {
            p1Segments[i].setStyle(i < p1Lit
                ? "-fx-background-color: #ff4444; -fx-background-radius: 2;"
                : "-fx-background-color: #1a1a1a; -fx-background-radius: 2;");
        }
        for (int i = 0; i < p2Segments.length; i++) {
            p2Segments[i].setStyle(i < p2Lit
                ? "-fx-background-color: #4488ff; -fx-background-radius: 2;"
                : "-fx-background-color: #1a1a1a; -fx-background-radius: 2;");
        }
    }

    private void showWinner(Character winner) {
        String name = (winner == p1) ? "Player 1" : "Player 2";
        System.out.println(name + " wins!");
        // TODO: show a label/overlay, disable further input, add a rematch button
    }
}