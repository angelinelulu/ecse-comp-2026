package com.kirbken;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.kirbken.utils.MusicManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println(Font.loadFont(
            getClass().getResourceAsStream("/fonts/GeistPixelRegular.ttf"), 20
        ).getName());
        System.out.println(Font.loadFont(
            getClass().getResourceAsStream("/fonts/TekkenReg.ttf"), 20
        ).getName());
        SceneManager manager = new SceneManager(primaryStage);
        primaryStage.setTitle("Puff Daddy");
        manager.goToStart();

        MusicManager.getInstance().play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}