package com.kirbken.models;

import javafx.scene.image.ImageView;

public class Character {
    private ImageView sprite;
    private double x, y;
    private boolean facingRight = true;
    private int health = 100;
    private boolean isAttacking = false;

    public Character(ImageView sprite, double startX, double startY) {
        this.sprite = sprite;
        this.x = startX;
        this.y = startY;
        sprite.setLayoutX(startX);
        sprite.setLayoutY(startY);
    }

    public void moveLeft() { x -= 5; facingRight = false; updatePosition(); }
    public void moveRight() { x += 5; facingRight = true; updatePosition(); }

    private void updatePosition() {
        sprite.setLayoutX(x);
        sprite.setScaleX(facingRight ? 1 : -1);
    }

    public void takeDamage(int amount) { health = Math.max(0, health - amount); }

    public int getHealth() { return health; }
    public double getX() { return x; }
    public ImageView getSprite() { return sprite; }
    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { isAttacking = attacking; }
}