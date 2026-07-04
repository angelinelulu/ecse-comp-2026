package com.kirbken;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * Handles uniform scaling of a fixed-resolution container to fit
 * the actual window size, preserving aspect ratio and centering
 * the result. Keeps the design resolution decoupled from whatever
 * the user resizes/fullscreens the window to.
 */
public class ViewportScaler {
    private final double designWidth;
    private final double designHeight;
    private final Pane container;
    private final Scale scale;

    public ViewportScaler(Pane container, double designWidth, double designHeight) {
        this.container = container;
        this.designWidth = designWidth;
        this.designHeight = designHeight;

        this.scale = new Scale(1, 1, 0, 0);
        container.getTransforms().add(scale);
    }

    /** Call once after the Scene and Stage are created. */
    public void attach(Scene scene, Stage stage) {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> rescale(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> rescale(scene));

        stage.fullScreenProperty().addListener((obs, oldVal, newVal) ->
            Platform.runLater(() -> rescale(scene))
        );

        rescale(scene); // initial pass
    }

    private void rescale(Scene scene) {
        double scaleX = scene.getWidth() / designWidth;
        double scaleY = scene.getHeight() / designHeight;
        double finalScale = Math.min(scaleX, scaleY);

        scale.setX(finalScale);
        scale.setY(finalScale);

        container.setLayoutX((scene.getWidth() - designWidth * finalScale) / 2);
        container.setLayoutY((scene.getHeight() - designHeight * finalScale) / 2);
    }
}