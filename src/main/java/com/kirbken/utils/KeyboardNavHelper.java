package com.kirbken.utils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

public class KeyboardNavHelper {

    /**
     * Enables left/right (and A/D) arrow-key focus traversal between a horizontal
     * row of buttons, plus lets Tab/Shift+Tab work as usual. Call once per screen,
     * after the scene is available (e.g. inside Platform.runLater in initialize()).
     */
    public static void enableHorizontalNav(Node anchorNode, Button... buttonsInOrder) {
        Scene scene = anchorNode.getScene();
        if (scene == null) return;

        scene.setOnKeyPressed(e -> {
            int currentIndex = -1;
            for (int i = 0; i < buttonsInOrder.length; i++) {
                if (buttonsInOrder[i].isFocused()) {
                    currentIndex = i;
                    break;
                }
            }

            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                int nextIndex = (currentIndex <= 0) ? 0 : currentIndex - 1;
                buttonsInOrder[nextIndex].requestFocus();
            } else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                int nextIndex = (currentIndex == -1) ? 0 : Math.min(currentIndex + 1, buttonsInOrder.length - 1);
                buttonsInOrder[nextIndex].requestFocus();
            }
        });

        // Ensure something is focused by default so arrow keys work immediately
        if (buttonsInOrder.length > 0) {
            buttonsInOrder[0].requestFocus();
        }
    }
}