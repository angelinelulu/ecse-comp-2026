package com.kirbken.models;

public class Fight {

    private static final double ATTACK_RANGE = 120;

    private final Character p1;
    private final Character p2;
    private boolean isOver = false;
    private Character winner = null;
    private static final double VERTICAL_HIT_TOLERANCE = 80;

    public Fight(Character p1, Character p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void update() {
        if (isOver) return;
        checkCollisions();
        checkWinCondition();
    }

    private void checkCollisions() {
        double horizontalDistance = Math.abs(p1.getCenterX() - p2.getCenterX());
        double verticalDistance = Math.abs(p1.getCenterY() - p2.getCenterY());

        boolean inRange = horizontalDistance < ATTACK_RANGE && verticalDistance < VERTICAL_HIT_TOLERANCE;

        if (inRange) {
            if (p1.isAttacking() && p1.isFacingToward(p2.getCenterX())) {
                p2.takeDamage(p1.getAttackPower());
            }
            if (p2.isAttacking() && p2.isFacingToward(p1.getCenterX())) {
                p1.takeDamage(p2.getAttackPower());
            }
        }
    }

    private void checkWinCondition() {
        if (p1.getHealth() <= 0) {
            isOver = true;
            winner = p2;
        } else if (p2.getHealth() <= 0) {
            isOver = true;
            winner = p1;
        }
    }

    public boolean isOver() { return isOver; }
    public Character getWinner() { return winner; }
}