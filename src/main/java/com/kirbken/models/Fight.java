package com.kirbken.models;

public class Fight {

    private static final double ATTACK_RANGE = 120;

    private final Character p1;
    private final Character p2;
    private boolean isOver = false;
    private Character winner = null;

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
        double distance = Math.abs(p1.getX() - p2.getX());

        if (distance < ATTACK_RANGE) {
            if (p1.isAttacking()) p2.takeDamage(p1.getAttackPower());
            if (p2.isAttacking()) p1.takeDamage(p2.getAttackPower());
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