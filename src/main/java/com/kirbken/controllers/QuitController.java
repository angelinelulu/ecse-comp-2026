package com.kirbken.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class QuitController {
    @FXML Button btnYes;
    @FXML Button btnNo;
    @FXML Label lblConfirmation;

    @FXML
    private void onYesClicked() {
        // Exit the application
    }

    @FXML
    private void onNoClicked() {
        // Return to SettingsController without quitting the application
    }
}
