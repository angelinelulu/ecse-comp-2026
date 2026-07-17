package com.kirbken.components;

import com.kirbken.controllers.QuizPopupController;
import com.kirbken.models.Question;
import java.io.IOException;
import java.util.function.BiConsumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class QuizPopup {
    private final StackPane overlay;

    public QuizPopup(Question question, BiConsumer<Boolean, Integer> onAnswered) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quiz_popup.fxml"));
            overlay = loader.load();
            QuizPopupController controller = loader.getController();
            controller.setup(question, onAnswered);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load quiz_popup.fxml", e);
        }
    }

    public StackPane getOverlay() {
        return overlay;
    }
}