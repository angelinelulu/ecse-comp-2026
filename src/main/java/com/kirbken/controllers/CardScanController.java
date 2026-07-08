package com.kirbken.controllers;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.kirbken.CharacterProfile;
import com.kirbken.CharacterRegistry;
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

            String qrText = result.getText();
            scanned = true;
            onCodeScanned(qrText);
        } catch (NotFoundException e) {
            // no QR code visible in this frame — normal, just keep scanning
        }
    }

    private void onCodeScanned(String qrCodeId) {
        Platform.runLater(() -> {
            stopCamera();

            CharacterProfile profile = CharacterRegistry.lookup(qrCodeId);
            if (profile == null) {
                profile = CharacterRegistry.getDefault();
                statusLabel.setText("Card not recognized — using default character.");
            }

            manager.goToConfirmation(profile);
        });
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