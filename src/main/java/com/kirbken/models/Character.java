package com.kirbken.models;

import javafx.scene.image.ImageView;

public class Character {
    private ImageView sprite;
    private double x, y;
    private boolean facingRight;

    private final int maxHealth;
    private int health;
    private final int attackPower;
    private final int defensePower;
    private final int speed;

    private boolean isAttacking = false;

    public Character(ImageView sprite, double startX, double startY, boolean startsFacingRight,
                      int maxHealth, int attackPower, int defensePower, int speed) {
        this.sprite = sprite;
        this.x = startX;
        this.y = startY;
        this.facingRight = startsFacingRight;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.speed = speed;
        sprite.setLayoutX(startX);
        sprite.setLayoutY(startY);
        updatePosition();
    }

    public void moveLeft() { x -= speed; facingRight = false; updatePosition(); }
    public void moveRight() { x += speed; facingRight = true; updatePosition(); }

    private void updatePosition() {
        sprite.setLayoutX(x);
        sprite.setScaleX(facingRight ? 1 : -1);
    }

    public void takeDamage(int rawAmount) {
        int mitigated = Math.max(1, rawAmount - defensePower / 2); // simple defense mitigation, tune as needed
        health = Math.max(0, health - mitigated);
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public double getX() { return x; }
    public ImageView getSprite() { return sprite; }
    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { isAttacking = attacking; }
}