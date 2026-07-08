package com.kirbken.models;

import javafx.scene.image.ImageView;

public class Character {
    private ImageView sprite;
    private double x, y;
    private final double groundY; // the Y position where the character stands normally
    private boolean facingRight;

    private final int maxHealth;
    private int health;
    private final int attackPower;
    private final int defensePower;
    private final int speed;

    private boolean isAttacking = false;
    

    // Jump physics
    private double velocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.8;
    private static final double JUMP_STRENGTH = -18;

    // Arena boundaries (adjust to match your arena.png's visible floor width)
    private static final double SPRITE_WIDTH = 350; // matches new fitWidth
    private static final double MARGIN = 10;
    private static final double MIN_X = -350;
    private static final double MAX_X = 1280 - SPRITE_WIDTH - MARGIN; // symmetric allowance matching p2's start

    public Character(ImageView sprite, double startX, double startY, boolean startsFacingRight,
                      int maxHealth, int attackPower, int defensePower, int speed) {
        this.sprite = sprite;
        this.x = startX;
        this.y = startY;
        this.groundY = startY;
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

    public void moveLeft() {
        x = Math.max(MIN_X, x - speed);
        facingRight = false;
        updatePosition();
    }

    public void moveRight() {
        x = Math.min(MAX_X, x + speed);
        facingRight = true;
        updatePosition();
    }

    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

    /** Call this every frame from the game loop to apply gravity/jump physics. */
    public void updatePhysics() {
        if (isJumping) {
            velocityY += GRAVITY;
            y += velocityY;

            if (y >= groundY) {
                y = groundY;
                velocityY = 0;
                isJumping = false;
            }
            sprite.setLayoutY(y);
        }
    }

    private void updatePosition() {
        sprite.setLayoutX(x);
        sprite.setScaleX(facingRight ? 1 : -1);
    }

    public void takeDamage(int rawAmount) {
        int mitigated = Math.max(1, rawAmount - defensePower / 2);
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