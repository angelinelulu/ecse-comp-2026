package com.kirbken.models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile {
    private final ImageView view;
    private double x, y;
    private final double speed;
    private final boolean movingRight;
    private final int damage;
    private boolean active = true;

    public Projectile(String imagePath, double startX, double startY, boolean movingRight,
                      double speed, int damage, javafx.scene.layout.Pane parent) {
        this.x = startX;
        this.y = startY;
        this.movingRight = movingRight;
        this.speed = speed;
        this.damage = damage;

        this.view = new ImageView();
        var url = getClass().getResource(imagePath);
        if (url != null) {
            view.setImage(new Image(url.toExternalForm()));
            System.out.println("Projectile image loaded: " + imagePath);
        } else {
            System.out.println("PROJECTILE IMAGE FAILED TO LOAD: " + imagePath);
        }
        view.setFitWidth(60);
        view.setFitHeight(60);
        view.setPreserveRatio(true);
        view.setLayoutX(x);
        view.setLayoutY(y);

        parent.getChildren().add(view);
    }

    /** Call every frame. Returns false once the projectile should be removed. */
    public boolean update() {
        if (!active) return false;

        x += movingRight ? speed : -speed;
        view.setLayoutX(x);

        // Deactivate once off-screen either side
        if (x < -100 || x > 1380) {
            deactivate();
            return false;
        }
        return true;
    }

    /** Simple bounding-box overlap check against a Character's current position. */
    public boolean checkHit(Character target, double targetWidth) {
        if (!active) return false;
        double targetCenterX = target.getCenterX();
        return Math.abs(x - targetCenterX) < (targetWidth / 2);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public int getDamage() {
        return damage;
    }

    public ImageView getView() {
        return view;
    }

    public boolean isMovingRight() {
        return movingRight;
    }
}