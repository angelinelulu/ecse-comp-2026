package com.kirbken;

import javafx.application.Application;
import javafx.scene.image.Image;
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

        var iconUrl = getClass().getResource("/images/pfp.png");
        if (iconUrl != null) {
            primaryStage.getIcons().add(new Image(iconUrl.toExternalForm()));
        } else {
            System.out.println("App icon not found at /images/pfp.png");
        }

        SceneManager manager = new SceneManager(primaryStage);
        primaryStage.setTitle("Puff Daddy");
        manager.goToStart();

        MusicManager.getInstance().play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}