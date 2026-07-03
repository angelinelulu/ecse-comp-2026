package com.kirbken;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager manager = new SceneManager(primaryStage);
        primaryStage.setTitle("Puff Daddy");
        manager.goToStart();
    }

    public static void main(String[] args) {
        launch(args);
    }
}