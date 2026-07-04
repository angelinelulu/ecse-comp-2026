package com.kirbken;

import com.kirbken.controllers.FxController;
import com.kirbken.controllers.StoryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
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

        Scale scale = new Scale(1, 1, 0, 0);
        container.getTransforms().add(scale);

        scene = new Scene(container, DESIGN_WIDTH, DESIGN_HEIGHT);
        stage.setScene(scene);
        stage.show();

        // Recalculate scale + centering whenever the window size changes
        scene.widthProperty().addListener((obs, oldVal, newVal) -> rescale(scale));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> rescale(scale));
    }

    private void rescale(Scale scale) {
        double scaleX = scene.getWidth() / DESIGN_WIDTH;
        double scaleY = scene.getHeight() / DESIGN_HEIGHT;
        double finalScale = Math.min(scaleX, scaleY);

        scale.setX(finalScale);
        scale.setY(finalScale);

        // center the scaled content within the window
        container.setLayoutX((scene.getWidth() - DESIGN_WIDTH * finalScale) / 2);
        container.setLayoutY((scene.getHeight() - DESIGN_HEIGHT * finalScale) / 2);
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