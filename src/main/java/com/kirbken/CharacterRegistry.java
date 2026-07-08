package com.kirbken;

public class CharacterRegistry {
    private static final CharacterProfile DEFAULT = new CharacterProfile(
        "kirby_base", "Classic Pink", "/images/basic.png",
        100, 12, 6, 10,
        "F", "Standard Inhale", "Star Spit", "Warp Star Strike",
        "Standard pink spherical protagonist, unequipped base variant"
    );

    private static final CharacterProfile VEXTHORN = new CharacterProfile(
        "vexthorn_basic", "Vexthorn", "/images/vexthorn_basic.png",
        120, 16, 10, 8,
        "S", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
        "Menacing horned entity born from the crack beneath the Starwell"
    );

    public static CharacterProfile getDefault() {
        return DEFAULT;
    }

    public static CharacterProfile getVexthorn() {
        return VEXTHORN;
    }
}