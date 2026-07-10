package com.kirbken;

import com.kirbken.controllers.ArenaController;
import com.kirbken.controllers.ConfirmationController;
import com.kirbken.controllers.FxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static final double DESIGN_WIDTH = 1280.0;
    private static final double DESIGN_HEIGHT = 720.0;

    private final Stage stage;
    private final Pane container = new Pane();
    private Scene scene;

    public SceneManager(Stage stage) {
        this.stage = stage;
        stage.setFullScreenExitHint("");

        container.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        container.setMinSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        container.setMaxSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        scene = new Scene(container, DESIGN_WIDTH, DESIGN_HEIGHT);
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

    public void goToLose() {
        loadFXML("/fxml/lose.fxml");
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

    public void goToConfirmation(CharacterProfile profile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/confirmation.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }
            if (controller instanceof ConfirmationController confirmationController) {
                confirmationController.setProfile(profile);
            }

            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToQuizSetup() {
        loadFXML("/fxml/quizsetup.fxml");
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
        container.getChildren().setAll(root);
    }
}