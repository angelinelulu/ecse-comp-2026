package com.kirbken.utils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public class KeyboardNavHelper {

    public static void enableHorizontalNav(Node anchorNode, Button... buttonsInOrder) {
        runWhenSceneReady(anchorNode, scene -> wireNav(scene, KeyCode.A, KeyCode.LEFT, KeyCode.D, KeyCode.RIGHT, buttonsInOrder));
    }

    public static void enableVerticalNav(Node anchorNode, Button... buttonsInOrder) {
        runWhenSceneReady(anchorNode, scene -> wireNav(scene, KeyCode.W, KeyCode.UP, KeyCode.S, KeyCode.DOWN, buttonsInOrder));
    }

    private static void runWhenSceneReady(Node anchorNode, java.util.function.Consumer<Scene> onReady) {
        Scene existing = anchorNode.getScene();
        if (existing != null) {
            onReady.accept(existing);
            return;
        }
        anchorNode.sceneProperty().addListener(new javafx.beans.value.ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    anchorNode.sceneProperty().removeListener(this);
                    onReady.accept(newScene);
                }
            }
        });
    }

    private static void wireNav(Scene scene, KeyCode backA, KeyCode backB, KeyCode fwdA, KeyCode fwdB, Button... buttonsInOrder) {
        scene.setOnKeyPressed(e -> {
            int currentIndex = -1;
            for (int i = 0; i < buttonsInOrder.length; i++) {
                if (buttonsInOrder[i].isFocused()) {
                    currentIndex = i;
                    break;
                }
            }

            if (e.getCode() == backA || e.getCode() == backB) {
                int nextIndex = (currentIndex <= 0) ? 0 : currentIndex - 1;
                buttonsInOrder[nextIndex].requestFocus();
            } else if (e.getCode() == fwdA || e.getCode() == fwdB) {
                int nextIndex = (currentIndex == -1) ? 0 : Math.min(currentIndex + 1, buttonsInOrder.length - 1);
                buttonsInOrder[nextIndex].requestFocus();
            }
        });

        if (buttonsInOrder.length > 0) {
            buttonsInOrder[0].requestFocus();
        }
    }
}