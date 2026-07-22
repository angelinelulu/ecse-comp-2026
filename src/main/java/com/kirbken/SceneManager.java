package com.kirbken;

import com.kirbken.controllers.ArenaController;
import com.kirbken.controllers.CardScanController;
import com.kirbken.controllers.ConfirmationController;
import com.kirbken.controllers.FxController;
import com.kirbken.controllers.LoseController;
import com.kirbken.controllers.WinController;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class SceneManager {
    private static final double DESIGN_WIDTH = 1280.0;
    private static final double DESIGN_HEIGHT = 720.0;

    private final Stage stage;
    private final Pane container = new Pane();
    private Scene scene;
    private Parent previousRoot;
    private Runnable onReturnFromSettings;

    public SceneManager(Stage stage) {
        this.stage = stage;
        stage.setFullScreenExitHint("");

        container.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        container.setMinSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        container.setMaxSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        container.setStyle("-fx-background-color: black;"); 

        scene = new Scene(container, DESIGN_WIDTH, DESIGN_HEIGHT);
        scene.setFill(javafx.scene.paint.Color.BLACK);
        stage.setScene(scene);
        stage.show();

        ViewportScaler scaler = new ViewportScaler(container, DESIGN_WIDTH, DESIGN_HEIGHT);
        scaler.attach(scene, stage);
    }

    public void setFullscreen(boolean fullscreen) {
        stage.setFullScreen(fullscreen);
    }

    public void goToStart() {
        loadFXML("/fxml/start.fxml");
    }

    public void goToWin() {
        loadFXML("/fxml/win.fxml");
    }

    public void goToQuitConfirmation() {
        loadFXML("/fxml/quit-screen.fxml");
    }

    public void goToWin(String winLabelText) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/win.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof WinController winController) {
                winController.setWinLabel(winLabelText);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToLose() {
        goToLose(CharacterRegistry.getDefault());
    }

    /** Kept for backward compatibility with any caller that doesn't have MatchStats to pass. */
    public void goToLose(CharacterProfile loserProfile) {
        goToLose(loserProfile, null);
    }

    public void goToLose(CharacterProfile loserProfile, MatchStats matchStats) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lose.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof LoseController loseController) {
                loseController.setLoserProfile(loserProfile, matchStats);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSettings() {
        loadFXML("/fxml/settings.fxml");
    }
    
    public void goToArena() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/arena.fxml"));
            Parent root = loader.load();

            ArenaController controller = loader.getController();
            controller.setSceneManager(this);
            controller.setupInput(scene);

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToStory() {
        loadFXML("/fxml/story.fxml");
    }

    public void goToCardScan() {
        loadFXML("/fxml/card_scan.fxml");
    }

    public void goToCardScanP2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/card_scan.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof CardScanController cardScanController) {
                cardScanController.setForPlayer2(true);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToCardScanForRoundTransition() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/card_scan.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof CardScanController cardScanController) {
                cardScanController.setReturnDestination(CardScanController.ReturnDestination.ROUND_TRANSITION);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToConfirmation(CharacterProfile profile, boolean forPlayer2) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/confirmation.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof ConfirmationController confirmationController) {
                confirmationController.setProfile(profile, forPlayer2);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToQuizSetup() {
        loadFXML("/fxml/quizsetup.fxml");
    }

    public void goToQuizModeSelect() {
        loadFXML("/fxml/quiz_mode_select.fxml");
    }

    public void goToModeSelect() {
        loadFXML("/fxml/mode_select.fxml");
    }

    private void loadFXML(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRoot(Parent root) {
        Node oldRoot = container.getChildren().isEmpty() ? null : container.getChildren().get(0);

        if (oldRoot == null) {
            root.setOpacity(0.0);
            container.getChildren().setAll(root);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            return;
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), oldRoot);
        fadeOut.setFromValue(oldRoot.getOpacity());
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            container.getChildren().remove(oldRoot);
            root.setOpacity(0.0);
            container.getChildren().setAll(root);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        SequentialTransition transition = new SequentialTransition(fadeOut);
        transition.play();
    }

    public void goToRoundTransition() {
        loadFXML("/fxml/round_transition.fxml");
    }

    public void goToSettingsFrom(Parent currentRoot, Runnable onReturn) {
        this.previousRoot = currentRoot;
        this.onReturnFromSettings = onReturn;
        loadFXML("/fxml/settings.fxml");
    }

    public void returnFromSettings() {
        if (previousRoot != null) {
            setRoot(previousRoot);
            if (onReturnFromSettings != null) {
                onReturnFromSettings.run();
                onReturnFromSettings = null;
            }
            previousRoot = null;
        } else {
            goToStart(); // fallback if Settings was opened with no stored return point
        }
    }

    // for multiplayer mode
    public void goToPlayer2Prompt() {
        loadFXML("/fxml/player2_prompt.fxml");
    }

    public void goToControls() {
        loadFXML("/fxml/controls.fxml");
    }

    public void goToCredits() {
        loadFXML("/fxml/credits.fxml");
    }
}