package com.kirbken.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;

import com.kirbken.utils.MusicManager;
import com.kirbken.models.Character;
import com.kirbken.models.Fight;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



import java.util.Set;
import java.util.HashSet;

public class ArenaController {

    @FXML private ImageView p1Sprite, p2Sprite;
    @FXML private Region p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7;
    @FXML private Region p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7;
    @FXML private Pane rootPane;
    @FXML private Label timerLabel;
    @FXML private Arc timerArc;

    private Character p1, p2;
    private Fight fight;
    private Region[] p1Segments, p2Segments;

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private AnimationTimer timer;

    private MediaPlayer arenaAudio;

    private static final int ROUND_DURATION_SECONDS = 180; // 3:00
    private int timeRemaining = ROUND_DURATION_SECONDS;
    private long lastSecondTick = 0;

    @FXML
    public void initialize() {
        java.net.URL fontUrl = getClass().getResource("/fonts/TekkenReg.ttf");
        if (fontUrl != null) {
            Font.loadFont(fontUrl.toExternalForm(), 28);
        }

        MusicManager.getInstance().pause();

        java.net.URL audioUrl = getClass().getResource("/sounds/arenaaudio.mp3");
        if (audioUrl != null) {
            Media media = new Media(audioUrl.toExternalForm());
            arenaAudio = new MediaPlayer(media);
            arenaAudio.setCycleCount(MediaPlayer.INDEFINITE);
            arenaAudio.setVolume(0.3); // Set volume to 30%
            arenaAudio.play();
        } else {
            System.out.println("Audio file not found!");
        }

        p1 = new Character(p1Sprite, 200, 450, true);   // P1 starts facing right (toward P2)
        p2 = new Character(p2Sprite, 900, 450, false);  // P2 starts facing left (toward P1)
        fight = new Fight(p1, p2);

        p1Segments = new Region[]{p1Seg0, p1Seg1, p1Seg2, p1Seg3, p1Seg4, p1Seg5, p1Seg6, p1Seg7};
        p2Segments = new Region[]{p2Seg0, p2Seg1, p2Seg2, p2Seg3, p2Seg4, p2Seg5, p2Seg6, p2Seg7};

        timerLabel.setText(formatTime(timeRemaining));

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
                updateTimer(now);

                if (fight.isOver()) {
                    stop();
                    showWinner(fight.getWinner());
                    return;
                }

                if (timeRemaining <= 0) {
                    stop();
                    handleTimeUp();
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

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void updateTimer(long now) {
        if (lastSecondTick == 0) {
            lastSecondTick = now;
            return;
        }

        long elapsedSeconds = (now - lastSecondTick) / 1_000_000_000L;
        if (elapsedSeconds <= 0) {
            return;
        }

        lastSecondTick += elapsedSeconds * 1_000_000_000L;

        timeRemaining = Math.max(0, timeRemaining - (int) elapsedSeconds);
        timerLabel.setText(formatTime(timeRemaining));

        double progress = (double) timeRemaining / ROUND_DURATION_SECONDS;
        timerArc.setLength(360 * progress);

        if (timeRemaining <= 10) {
            timerArc.setStroke(Color.RED);
        }
    }

    private void handleTimeUp() {
        // Round timed out — decide winner by remaining health
        Character winner = (p1.getHealth() >= p2.getHealth()) ? p1 : p2;
        showWinner(winner);
    }

    private void showWinner(Character winner) {
        // MusicManager.getInstance().play();
        String name = (winner == p1) ? "Player 1" : "Player 2";
        System.out.println(name + " wins!");
        // TODO: show a label/overlay, disable further input, add a rematch button
    }
}