package com.kirbken;

public class CharacterRegistry {
    private static final CharacterProfile DEFAULT = new CharacterProfile(
        "kirby_base", "Classic Pink", "/images/basic.png",
        100, 12, 7, 10,
        "F", "Standard Inhale", "Star Spit", "Warp Star Strike",
        "Standard pink spherical protagonist, unequipped base variant"
    );

    private static final CharacterProfile NINJA = new CharacterProfile(
        "kirby_ninja", "Shadow Shinobi", "/images/kirby_ninja.png",
        115, 18, 8, 16,
        "D", "Kunai Slash", "Smoke Bomb Vanish", "Shadow Clone Flurry",
        "Dark blue stealth cowl variant wielding a ceremonial kunai dagger"
    );

    private static final CharacterProfile STORM = new CharacterProfile(
        "kirby_storm", "Storm Summoner", "/images/kirby_storm.png",
        130, 24, 10, 12,
        "C", "Spark Discharge", "Nimbus Shield", "Thunderbolt Judgment",
        "Pink variant with an active static localized lightning raincloud hovering above"
    );

    private static final CharacterProfile ANGELIC = new CharacterProfile(
        "kirby_angelic", "Seraphic Monarch", "/images/kirby_angelic.png",
        150, 30, 14, 15,
        "B", "Lunar Ray", "Divine Sanctuary", "Crescent Nova Blast",
        "Ornate white robed angel variant with feathered wings, wielding a crescent moon staff"
    );

    private static final CharacterProfile BUFF = new CharacterProfile(
        "kirby_buff", "Titan Overlord", "/images/kirby_buff.png",
        200, 45, 22, 8,
        "A", "Scepter Smash", "Iron Flex Armor", "Supermassive Earthshaker",
        "Hyper-muscular brawny variant wearing a royal ruby crown, holding a golden sun scepter"
    );

    private static final CharacterProfile VEXTHORN = new CharacterProfile(
        "vexthorn_basic", "Vexthorn", "/images/vexthorn_basic.png",
        100, 8, 6, 3,
        "B", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
        "Menacing horned entity born from the crack beneath the Starwell"
    );

    private static final CharacterProfile VEXTHORN_BOSS = new CharacterProfile(
        "vexthorn_boss", "Vexthorn (Boss Form)", "/images/vexthorn_boss.png",
        160, 8, 6, 7,
        "S", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
        "Vexthorn's true, unleashed form — larger, faster, and far more dangerous"
    );

    private static final CharacterProfile VEXTHORN_BOSS_STRONG = new CharacterProfile(
    "vexthorn_boss", "Vexthorn (Boss Form)", "/images/vexthorn_boss.png",
    220, 22, 14, 7,
    "S", "Shadow Claw", "Dark Pulse", "Abyssal Ruin",
    "Vexthorn's true, unleashed form — larger, faster, and far more dangerous"
);

    public static CharacterProfile getDefault() {
        return DEFAULT;
    }

    public static CharacterProfile getNinja() {
        return NINJA;
    }

    public static CharacterProfile getStorm() {
        return STORM;
    }

    public static CharacterProfile getAngelic() {
        return ANGELIC;
    }

    public static CharacterProfile getBuff() {
        return BUFF;
    }

    public static CharacterProfile getVexthorn() {
        return VEXTHORN;
    }

    public static CharacterProfile getVexthornBoss() {
        return VEXTHORN_BOSS;
    }

    public static CharacterProfile getVexthornBossStrong() {
    return VEXTHORN_BOSS_STRONG;
}
}