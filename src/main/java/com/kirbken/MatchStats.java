package com.kirbken;

/**
 * Snapshot of a single match's outcome, built once when the fight ends.
 * Passed to result screens (LoseController, WinController) so they can show
 * real numbers instead of a static base-stat preview.
 */
public class MatchStats {

    private final int damageDealt;   // damage this character's opponent took
    private final int damageTaken;   // damage this character took
    private final int quizCorrect;
    private final int quizWrong;
    private final int matchDurationSeconds;

    public MatchStats(int damageDealt, int damageTaken, int quizCorrect, int quizWrong, int matchDurationSeconds) {
        this.damageDealt = damageDealt;
        this.damageTaken = damageTaken;
        this.quizCorrect = quizCorrect;
        this.quizWrong = quizWrong;
        this.matchDurationSeconds = matchDurationSeconds;
    }

    public int getDamageDealt() { return damageDealt; }
    public int getDamageTaken() { return damageTaken; }
    public int getQuizCorrect() { return quizCorrect; }
    public int getQuizWrong() { return quizWrong; }
    public int getMatchDurationSeconds() { return matchDurationSeconds; }

    public String getFormattedDuration() {
        int minutes = matchDurationSeconds / 60;
        int seconds = matchDurationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}