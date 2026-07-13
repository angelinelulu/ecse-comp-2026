package com.kirbken;

public class GameState {
    private static boolean characterLocked = false;
    private static CharacterProfile selectedCharacter = CharacterRegistry.getDefault();
    private static int currentRound = 1;
    public enum GameMode { SINGLEPLAYER, MULTIPLAYER, UNSET }
    private static GameMode gameMode = GameMode.UNSET;
    private static CharacterProfile selectedCharacterP2 = null;
    private static boolean characterLockedP2 = false;

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
        characterLockedP2 = false;
        selectedCharacterP2 = null;
    }

    public static void setGameMode(GameMode mode) {
        gameMode = mode;
    }

    public static GameMode getGameMode() {
        return gameMode;
    }

    public static void setPendingCharacterP2(CharacterProfile profile) {
        if (!characterLockedP2) {
            selectedCharacterP2 = profile;
        }
    }

    public static void confirmCharacterP2() {
        characterLockedP2 = true;
    }

    public static CharacterProfile getSelectedCharacterP2() {
        return selectedCharacterP2;
    }

    public static boolean isCharacterLockedP2() {
        return characterLockedP2;
    }

    public static void unlockForNewRoundP2() {
        characterLockedP2 = false;
    }
}