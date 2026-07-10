package com.kirbken;

public class GameState {
    private static boolean characterLocked = false;
    private static CharacterProfile selectedCharacter = CharacterRegistry.getDefault();
    private static int currentRound = 1;

    public static void setPendingCharacter(CharacterProfile profile) {
        if (!characterLocked) {
            selectedCharacter = profile;
        }
    }

    public static void confirmCharacter() {
        characterLocked = true;
    }

    public static CharacterProfile getSelectedCharacter() {
        return selectedCharacter;
    }

    public static boolean isCharacterLocked() {
        return characterLocked;
    }

    public static int getCurrentRound() {
        return currentRound;
    }

    public static void advanceToNextRound() {
        currentRound++;
    }

    public static void resetRounds() {
        currentRound = 1;
    }

    public static void unlockForNewRound() {
        characterLocked = false;
    }
}