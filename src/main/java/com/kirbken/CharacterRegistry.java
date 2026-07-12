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
        100, 11, 7, 3,
        "B", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
        "Menacing horned entity born from the crack beneath the Starwell"
    );

    private static final CharacterProfile VEXTHORN_BOSS = new CharacterProfile(
        "vexthorn_boss", "Vexthorn (Boss Form)", "/images/vexthorn_boss.png",
        200, 22, 14, 7,
        "S", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
        "Vexthorn's true, unleashed form — larger, faster, and far more dangerous"
    );

    public static CharacterProfile getVexthornBoss() {
        return VEXTHORN_BOSS;
    }

    public static CharacterProfile getDefault() {
        return DEFAULT;
    }

    public static CharacterProfile getVexthorn() {
        return VEXTHORN;
    }
}