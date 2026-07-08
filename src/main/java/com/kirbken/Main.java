package com.kirbken;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println(Font.loadFont(
            getClass().getResourceAsStream("/fonts/GeistPixelRegular.ttf"), 20
        ).getName());
        SceneManager manager = new SceneManager(primaryStage);
        primaryStage.setTitle("Puff Daddy");
        manager.goToArena();
    }

    public static void main(String[] args) {
        launch(args);
    }
}