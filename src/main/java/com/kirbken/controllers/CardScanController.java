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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class CardScanController implements FxController {
    private SceneManager manager;
    private Webcam webcam;
    private boolean forPlayer2 = false;
    private AnimationTimer scanLoop;
    private boolean scanned = false;

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

    public void setForPlayer2(boolean forPlayer2) {
        this.forPlayer2 = forPlayer2;
    }

    private void onCodeScanned(String qrCodeId) {
        stopCamera();
        statusLabel.setText("Loading character data...");

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
        manager.goToStory();
    }

    private void stopCamera() {
        if (scanLoop != null) scanLoop.stop();
        if (webcam != null && webcam.isOpen()) webcam.close();
    }
}