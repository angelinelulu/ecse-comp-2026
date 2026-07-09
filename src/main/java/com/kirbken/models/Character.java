package com.kirbken.models;

import javafx.scene.image.ImageView;

public class Character {
    private ImageView sprite;
    private SpriteAnimator animator;
    private double x, y;
    private final double groundY;
    private boolean facingRight;

    private final int maxHealth;
    private int health;
    private final int attackPower;
    private final int defensePower;
    private final int speed;

    private boolean isAttacking = false;

    private double velocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.8;
    private static final double JUMP_STRENGTH = -18;

    private static final double SPRITE_WIDTH = 350;
    private static final double MARGIN = 10;
    private static final double MIN_X = -350;
    private static final double MAX_X = 1280 - SPRITE_WIDTH - MARGIN;
    private static final double VISUAL_CENTER_OFFSET = 375;
    private static final double VISUAL_CENTER_Y_OFFSET = 200;

    private long specialStartTime = 0;
    private boolean specialActive = false;
    private boolean specialThrowing = false;
    private long throwStartTime = 0;
    private int specialStage = 0;
    private static final long SPECIAL_STAGE_DURATION_NS = 300_000_000L; // 0.3s per stage
    private static final long SPECIAL_THROW_DURATION_NS = 300_000_000L;

    private boolean justThrew = false;

    public Character(ImageView sprite, double startX, double startY, boolean startsFacingRight,
                      int maxHealth, int attackPower, int defensePower, int speed) {
        this.sprite = sprite;
        this.animator = new SpriteAnimator(sprite);
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

    public SpriteAnimator getAnimator() {
        return animator;
    }

    public void moveLeft() {
        x = Math.max(MIN_X, x - speed);
        facingRight = false;
        animator.setState(SpriteAnimator.State.WALK);
        updatePosition();
    }

    public void moveRight() {
        x = Math.min(MAX_X, x + speed);
        facingRight = true;
        animator.setState(SpriteAnimator.State.WALK);
        updatePosition();
    }

    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

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

    public void triggerSpecial() {
        if (!specialActive && !specialThrowing) {
            specialActive = true;
            specialStage = 1;
            specialStartTime = System.nanoTime();
            animator.setState(SpriteAnimator.State.SPECIAL_WINDUP);
        }
    }

    public void updateSpecial() {
        if (specialActive) {
            long elapsed = System.nanoTime() - specialStartTime;
            if (elapsed >= SPECIAL_STAGE_DURATION_NS) {
                specialStage++;
                specialStartTime = System.nanoTime();

                boolean hasSecondWindup = animator.hasFrames(SpriteAnimator.State.SPECIAL_WINDUP_2);

                if (specialStage == 2 && hasSecondWindup) {
                    animator.setState(SpriteAnimator.State.SPECIAL_WINDUP_2);
                } else {
                    animator.setState(SpriteAnimator.State.SPECIAL_THROW);
                    specialActive = false;
                    specialThrowing = true;
                    throwStartTime = System.nanoTime();
                    justThrew = true;
                }
            }
        } else if (specialThrowing) {
            long elapsed = System.nanoTime() - throwStartTime;
            if (elapsed >= SPECIAL_THROW_DURATION_NS) {
                specialThrowing = false;
                animator.setState(SpriteAnimator.State.IDLE);
            }
        }
    }

    /** Call once per frame from ArenaController; returns true exactly once per throw. */
    public boolean consumeJustThrew() {
        if (justThrew) {
            justThrew = false;
            return true;
        }
        return false;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public boolean isSpecialActive() {
        return specialActive || specialThrowing;
    }

    private void updatePosition() {
        sprite.setLayoutX(x);
        sprite.setScaleX(facingRight ? 1 : -1);
    }

    public void takeDamage(int rawAmount) {
        int mitigated = Math.max(1, rawAmount - defensePower / 2);
        health = Math.max(0, health - mitigated);
    }

    public double getCenterX() {
        return x + VISUAL_CENTER_OFFSET;
    }

    public double getCenterY() {
        return y + VISUAL_CENTER_Y_OFFSET;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public double getX() { return x; }
    public ImageView getSprite() { return sprite; }
    public boolean isAttacking() { return isAttacking; }
    public void setAttacking(boolean attacking) { isAttacking = attacking; }
}