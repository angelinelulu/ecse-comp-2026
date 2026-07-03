package com.kirbken;

import com.kirbken.controllers.FxController;
import com.kirbken.controllers.StoryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
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

    public void goToStory() {
        StoryController controller = new StoryController(this);
        setScene(controller.build());
    }

    private void loadFXML(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof FxController fxController) {
                fxController.setSceneManager(this);
            }

            setScene(new Scene(root, 1280, 720));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}