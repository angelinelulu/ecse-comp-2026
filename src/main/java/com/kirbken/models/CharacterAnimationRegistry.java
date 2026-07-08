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
        REGISTRY.put("kirby_buff", new AnimationSet(
            "/images/boss.png",
            "/images/boss_kirby/run.png",
            "/images/boss_kirby/punch.png",
            "/images/boss_kirby/special_punch.png",
            "/images/boss_kirby/special_punch.png"
        ));
        REGISTRY.put("kirby_storm", new AnimationSet(
            "/images/thunder.png",
            "/images/storm_kirby/run.png",
            "/images/storm_kirby/throw1.png",   // normal attack, frame 1
            "/images/storm_kirby/special.png",
            "/images/storm_kirby/special.png"
        ));
        REGISTRY.put("kirby_ninja", new AnimationSet(
            "/images/ninja.png",
            "/images/ninja_kirby/run.png",
            "/images/ninja_kirby/punch.png",
            "/images/ninja_kirby/throw1.png",
            "/images/ninja_kirby/throw2.png"
        ));
        REGISTRY.put("kirby_angelic", new AnimationSet(
            "/images/sailor.png",
            "/images/sailor_kirby/run.png",
            "/images/sailor_kirby/punch.png",
            "/images/sailor_kirby/special.png",
            "/images/sailor_kirby/special.png"
        ));
    }

    public static AnimationSet get(String characterId) {
        return REGISTRY.get(characterId);
    }
}