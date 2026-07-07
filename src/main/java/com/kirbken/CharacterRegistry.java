package com.kirbken;

import java.util.HashMap;
import java.util.Map;

public class CharacterRegistry {
    private static final Map<String, CharacterProfile> CHARACTERS = new HashMap<>();
    private static final CharacterProfile DEFAULT = new CharacterProfile(
        "default", "Kirby", "/images/characters/kirby_default.png",
        10, 10, 10
    );

    static {
        CHARACTERS.put("default", DEFAULT);
        CHARACTERS.put("fire_kirby", new CharacterProfile(
            "fire_kirby", "Fire Kirby", "/images/characters/kirby_fire.png",
            15, 8, 9
        ));
        CHARACTERS.put("ice_kirby", new CharacterProfile(
            "ice_kirby", "Ice Kirby", "/images/characters/kirby_ice.png",
            9, 15, 8
        ));
        // TODO: add more as you design more cards
    }

    public static CharacterProfile getDefault() {
        return DEFAULT;
    }

    /** Returns null if the scanned QR code doesn't match any known card. */
    public static CharacterProfile lookup(String qrCodeId) {
        return CHARACTERS.get(qrCodeId);
    }
}