package com.kirbken.controllers;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.kirbken.CharacterProfile;
import com.kirbken.CharacterRegistry;
import com.kirbken.FirebaseCharacterService;
import com.kirbken.SceneManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class CardScanController implements FxController {
    private SceneManager manager;
    private Webcam webcam;
    private AnimationTimer scanLoop;
    private boolean scanned = false;

    public enum ReturnDestination { STORY, PLAYER2_PROMPT, ROUND_TRANSITION }
    private ReturnDestination returnDestination = ReturnDestination.STORY;

    @FXML private ImageView cameraView;
    @FXML private Label statusLabel;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        webcam = Webcam.getDefault();
        if (webcam == null) {
            statusLabel.setText("No camera found. Please connect a webcam.");
            return;
        }

        webcam.setViewSize(new java.awt.Dimension(640, 480));
        webcam.open();

        scanLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (scanned) return;

                BufferedImage frame = webcam.getImage();
                if (frame == null) return;

                cameraView.setImage(SwingFXUtils.toFXImage(frame, null));
                tryDecodeQRCode(frame);
            }
        };
        scanLoop.start();
    }

    private void tryDecodeQRCode(BufferedImage frame) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(frame);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);

            String qrText = result.getText().trim();
            System.out.println("SCANNED RAW TEXT: [" + qrText + "]");
            scanned = true;
            onCodeScanned(qrText);
        } catch (NotFoundException e) {
            // if no QR code visible in this frame, just keep scanning
        }
    }

    public void setReturnDestination(ReturnDestination destination) {
        this.returnDestination = destination;
    }

    /** Kept for backward compatibility with existing callers (e.g. SceneManager.goToCardScanP2). */
    public void setForPlayer2(boolean forPlayer2) {
        this.returnDestination = forPlayer2 ? ReturnDestination.PLAYER2_PROMPT : ReturnDestination.STORY;
    }

    private void onCodeScanned(String qrCodeId) {
        stopCamera();
        statusLabel.setText("Loading character data...");

        boolean forPlayer2 = (returnDestination == ReturnDestination.PLAYER2_PROMPT);

        new Thread(() -> {
            CharacterProfile profile = FirebaseCharacterService.fetchCharacter(qrCodeId);

            Platform.runLater(() -> {
                if (profile == null) {
                    statusLabel.setText("Card not recognized — using default character.");
                    manager.goToConfirmation(CharacterRegistry.getDefault(), forPlayer2);
                } else {
                    manager.goToConfirmation(profile, forPlayer2);
                }
            });
        }).start();
    }

    @FXML
    private void onCancelClicked() {
        stopCamera();
        switch (returnDestination) {
            case PLAYER2_PROMPT -> manager.goToPlayer2Prompt();
            case ROUND_TRANSITION -> manager.goToRoundTransition();
            default -> manager.goToStory();
        }
    }

    private void stopCamera() {
        if (scanLoop != null) scanLoop.stop();
        if (webcam != null && webcam.isOpen()) webcam.close();
    }
}