package com.kirbken.controllers;

import com.kirbken.GameState;
import com.kirbken.SceneManager;
import javafx.fxml.FXML;

public class RoundTransitionController implements FxController {
    private SceneManager manager;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    private void onScanNewCard() {
        GameState.advanceToNextRound();
        GameState.unlockForNewRound();
        manager.goToCardScan();
    }

    @FXML
    private void onKeepCurrent() {
        GameState.advanceToNextRound();
        manager.goToArena();
    }
}