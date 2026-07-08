package com.kirbken.models;

import java.util.HashMap;
import java.util.Map;

public class CharacterAnimationRegistry {

    public static class AnimationSet {
        public String idle, walk, attack, specialWindup, specialThrow;
        public AnimationSet(String idle, String walk, String attack, String specialWindup, String specialThrow) {
            this.idle = idle; this.walk = walk; this.attack = attack;
            this.specialWindup = specialWindup; this.specialThrow = specialThrow;
        }
    }

    private static final Map<String, AnimationSet> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put("kirby_base", new AnimationSet(
            "/images/basic.png",
            "/images/basic_kirby/run.png",
            "/images/basic_kirby/punch.png",
            "/images/basic_kirby/throw1.png",
            "/images/basic_kirby/throw2.png"
        ));
        // TODO: add kirby_ninja, kirby_storm, kirby_angelic, kirby_buff, vexthorn_basic once filenames are confirmed
    }

    public static AnimationSet get(String characterId) {
        return REGISTRY.get(characterId);
    }
}