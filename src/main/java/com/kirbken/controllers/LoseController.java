package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class LoseController implements FxController {
    private SceneManager manager;

    @FXML private Label lblGameOver;
    @FXML private Label lblLossReason;
    @FXML private ImageView imgLoser;
    @FXML private TitledPane paneResults;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;

    @Override
    public void setSceneManager(SceneManager manager) {
        this.manager = manager;
    }

    @FXML
    public void onPlayAgain(ActionEvent event) {
        manager.goToArena();
    }

    @FXML
    public void onHome(ActionEvent event) {
        GameState.resetRounds();
        GameState.unlockForNewRound();
        manager.goToStart();
    }

    @FXML
    public void initialize() {
        if (lblGameOver != null) {
            lblGameOver.setText("Game Over : You Lost");
        }

        if (lblLossReason != null) {
            lblLossReason.setText("Defeated in battle");
        }

        if (paneResults != null) {
            paneResults.setContent(createResultsPlaceholder(null));
        }
    }

    public void setGameOverLabel(String text) {
        if (lblGameOver != null) {
            lblGameOver.setText(text == null || text.isBlank() ? "Game Over : You Lost" : text);
        }
    }

    public void setLoserProfile(CharacterProfile loserProfile) {
        if (loserProfile == null) {
            setGameOverLabel("Game Over : You Lost");
            if (lblLossReason != null) {
                lblLossReason.setText("Defeated in battle");
            }
            if (paneResults != null) {
                paneResults.setContent(createResultsPlaceholder(null));
            }
            return;
        }

        setGameOverLabel("Game Over : " + loserProfile.getDisplayName() + " Lost");

        if (lblLossReason != null) {
            lblLossReason.setText("Defeated in battle");
        }

        if (imgLoser != null) {
            String imagePath = resolveLoserImagePath(loserProfile);
            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                imgLoser.setImage(new Image(imageUrl.toExternalForm()));
            }
        }

        if (paneResults != null) {
            paneResults.setContent(createResultsPlaceholder(loserProfile));
        }
    }

    private VBox createResultsPlaceholder(CharacterProfile profile) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(12, 14, 12, 14));

        Label header = new Label(profile == null ? "Results Preview" : profile.getDisplayName() + " Stats Preview");
        Label line1 = new Label(profile == null ? "Character: --" : "Character: " + profile.getDisplayName());
        Label line2 = new Label(profile == null ? "HP: --   Attack: --" : "HP: " + profile.getHp() + "   Attack: " + profile.getAttackPower());
        Label line3 = new Label(profile == null ? "Defense: --   Speed: --" : "Defense: " + profile.getDefensePower() + "   Speed: " + profile.getSpeed());
        Label footer = new Label("Match results will be detailed here.");

        for (Label label : new Label[] {header, line1, line2, line3, footer}) {
            label.getStyleClass().add("geist-pixel");
            label.setWrapText(true);
        }

        header.setStyle("-fx-font-size: 22px; -fx-text-fill: #000000;");
        line1.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");
        line2.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");
        line3.setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");
        footer.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");

        content.getChildren().addAll(header, line1, line2, line3, footer);
        return content;
    }

    private String resolveLoserImagePath(CharacterProfile profile) {
        String profileId = profile.getId();
        if (profileId == null) {
            return profile.getSpriteSheetPath();
        }

        return switch (profileId) {
            case "kirby_base" -> "/images/basic_kirby/die.png";
            case "kirby_ninja" -> "/images/ninja_kirby/die.png";
            case "kirby_storm" -> "/images/storm_kirby/die.png";
            case "kirby_angelic" -> "/images/sailor_kirby/die.png";
            case "kirby_buff" -> "/images/boss_kirby/die.png";
            case "vexthorn_basic" -> "/images/vexthorn_basic.png";
            case "vexthorn_boss" -> "/images/vexthorn_boss.png";
            default -> profile.getSpriteSheetPath();
        };
    }
}