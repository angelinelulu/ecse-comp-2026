package com.kirbken;

public class CharacterRegistry {
    private static final CharacterProfile DEFAULT = new CharacterProfile(
        "kirby_base", "Classic Pink", "/images/characters/kirby_base.png",
        100, 12, 6, 10,
        "F", "Standard Inhale", "Star Spit", "Warp Star Strike",
        "Standard pink spherical protagonist, unequipped base variant"
    );

    public static CharacterProfile getDefault() {
        return DEFAULT;
    }
}