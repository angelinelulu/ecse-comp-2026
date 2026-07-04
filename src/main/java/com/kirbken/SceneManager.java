package com.kirbken;

import com.kirbken.controllers.FxController;
import com.kirbken.controllers.StoryController;
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

    public void goToStory() {
        StoryController controller = new StoryController(this);
        setRoot(controller.build());
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