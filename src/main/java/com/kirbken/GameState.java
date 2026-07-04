package com.kirbken;

public class GameState {
    private static boolean characterLocked = false;
    private static String selectedCharacter = "Kirby";

    public static void lockCharacter(String character) {
        if (!characterLocked) {
            selectedCharacter = character;
            characterLocked = true;
        }
    }

    public static String getSelectedCharacter() {
        return selectedCharacter;
    }

    public static boolean isCharacterLocked() {
        return characterLocked;
    }
}