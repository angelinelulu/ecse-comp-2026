package com.kirbken.controllers;

import com.kirbken.CharacterProfile;
import com.kirbken.GameState;
import com.kirbken.MatchStats;
import com.kirbken.SceneManager;
import com.kirbken.utils.KeyboardNavHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

public class LoseController implements FxController {
    private SceneManager manager;

    @FXML private Label lblDefeat;
    @FXML private ImageView imgLoser;
    @FXML private TitledPane paneResults;
    @FXML private Button btnPlayAgain;
    @FXML private Button btnHome;

    private CharacterProfile pendingLoserProfile;
    private MatchStats pendingMatchStats;

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

        java.net.URL fontUrl = getClass().getResource("/fonts/GeistPixelRegular.ttf");
        if (fontUrl != null) {
        Font.loadFont(fontUrl.toExternalForm(), 28);
    }
        if (lblDefeat != null) {
            lblDefeat.setText("Defeat");
        }

        if (paneResults != null) {
            paneResults.setContent(createResultsPlaceholder(null, null));
        }

        javafx.application.Platform.runLater(() ->
            KeyboardNavHelper.enableHorizontalNav(btnPlayAgain, btnHome)
        );
    }

    public void setGameOverLabel(String text) {
        if (lblDefeat != null) {
            lblDefeat.setText(text == null || text.isBlank() ? "Defeat" : text);
        }
    }

    /**
     * Sets the loser's profile only, with no match stats. Kept for backward compatibility
     * with any existing callers of the single-arg SceneManager.goToLose(profile).
     * Prefer setLoserProfile(profile, stats) so the results panel shows real match data.
     */
    public void setLoserProfile(CharacterProfile loserProfile) {
        setLoserProfile(loserProfile, null);
    }

    public void setLoserProfile(CharacterProfile loserProfile, MatchStats matchStats) {
        this.pendingLoserProfile = loserProfile;
        this.pendingMatchStats = matchStats;

        if (loserProfile == null) {
            if (paneResults != null) {
                paneResults.setContent(createResultsPlaceholder(null, null));
            }
            return;
        }

        if (imgLoser != null) {
            String imagePath = resolveLoserImagePath(loserProfile);
            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                imgLoser.setImage(new Image(imageUrl.toExternalForm()));
            }
        }

        if (paneResults != null) {
            paneResults.setContent(createResultsPlaceholder(loserProfile, matchStats));
        }
    }

    private VBox createResultsPlaceholder(CharacterProfile profile, MatchStats stats) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(12, 14, 12, 14));

        Label header = new Label(profile == null ? "Results Preview" : profile.getDisplayName() + " — Match Results");

        java.util.List<Label> labels = new java.util.ArrayList<>();
        labels.add(header);

        if (stats == null) {
            // No match stats available yet (e.g. initial placeholder before a match has run) —
            // fall back to the static base-stat preview so the panel is never blank.
            Label line1 = new Label(profile == null ? "Character: --" : "Character: " + profile.getDisplayName());
            Label line2 = new Label(profile == null ? "HP: --   Attack: --" : "HP: " + profile.getHp() + "   Attack: " + profile.getAttackPower());
            Label line3 = new Label(profile == null ? "Defense: --   Speed: --" : "Defense: " + profile.getDefensePower() + "   Speed: " + profile.getSpeed());
            Label footer = new Label(profile == null ? "Match results will be detailed here." : "Better luck next time!");
            labels.add(line1);
            labels.add(line2);
            labels.add(line3);
            labels.add(footer);
        } else {
            labels.add(new Label("Damage Dealt: " + stats.getDamageDealt()));
            labels.add(new Label("Damage Taken: " + stats.getDamageTaken()));
            if (stats.getQuizCorrect() + stats.getQuizWrong() > 0) {
                labels.add(new Label("Quiz: " + stats.getQuizCorrect() + " correct / " + stats.getQuizWrong() + " wrong"));
            }
            labels.add(new Label("Match Length: " + stats.getFormattedDuration()));
        }

        for (Label label : labels) {
            label.getStyleClass().add("geist-pixel");
            label.setWrapText(true);
        }

        header.setStyle("-fx-font-size: 22px; -fx-text-fill: #000000;");
        for (int i = 1; i < labels.size(); i++) {
            labels.get(i).setStyle("-fx-font-size: 18px; -fx-text-fill: #000000;");
        }

        content.getChildren().addAll(labels);
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