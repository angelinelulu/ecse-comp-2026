package com.kirbken;

public class GameState {
    private static boolean characterLocked = false;
    private static CharacterProfile selectedCharacter = CharacterRegistry.getDefault();

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
}