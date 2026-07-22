package com.kirbken.controllers;

import com.kirbken.SceneManager;
import com.kirbken.utils.MusicManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

public class SettingsController implements FxController {
    private SceneManager manager;
    private final MusicManager musicManager = MusicManager.getInstance();

    @FXML private Slider masterVolumeSlider;
    @FXML private Slider musicVolumeSlider;
    @FXML private Slider sfxVolumeSlider;
    @FXML private CheckBox fullscreenCheckBox;
    @FXML private Button backButton;
    @FXML private Button btnQuit;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        masterVolumeSlider.setValue(musicManager.getMasterVolume() * 100);
        musicVolumeSlider.setValue(musicManager.getMusicVolume() * 100);
        sfxVolumeSlider.setValue(musicManager.getSfxVolume() * 100);

        masterVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setMasterVolume(newVal.doubleValue() / 100.0);
        });

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setMusicVolume(newVal.doubleValue() / 100.0);
        });

        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setSfxVolume(newVal.doubleValue() / 100.0);
        });

        fullscreenCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            manager.setFullscreen(newVal);
        });

        javafx.application.Platform.runLater(() -> {
            var scene = backButton.getScene();
            if (scene != null) {
                backButton.requestFocus(); // ensures Enter works on Back immediately
            }
        });
    }

    @FXML
    private void onControlsClicked() {
        manager.goToControls();
    }

    @FXML
    private void onCreditsClicked() {
        manager.goToCredits();
    }

    @FXML
    private void BackToStart() {
        manager.returnFromSettings();
    }

    @FXML
    private void onQuitClicked() {
        manager.goToQuitConfirmation();
    }
}