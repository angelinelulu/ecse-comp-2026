package com.kirbken.controllers;

import java.util.List;

import com.kirbken.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class SettingsController implements FxController {
    private SceneManager manager;

    @FXML private Slider masterVolumeSlider;
    @FXML private Slider musicVolumeSlider;
    @FXML private Slider sfxVolumeSlider;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox fullscreenCheckBox;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void initialize() {
        languageComboBox.getItems().addAll(List.of("English", "Māori"));
        languageComboBox.setValue("English");

        masterVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // TODO: hook into your actual audio manager
            System.out.println("Master volume: " + newVal.intValue());
        });

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Music volume: " + newVal.intValue());
        });

        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("SFX volume: " + newVal.intValue());
        });

        languageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            // TODO: hook into your localization system
            System.out.println("Language changed to: " + newVal);
        });

        fullscreenCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            manager.setFullscreen(newVal);
        });
    }

    @FXML
    private void BackToStart() {
        manager.goToStart();
    }
}