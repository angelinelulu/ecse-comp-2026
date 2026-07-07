package com.kirbken;

public class CharacterProfile {
    private final String id;
    private final String displayName;
    private final String spriteSheetPath;
    private final int attackPower;
    private final int defensePower;
    private final int speed;

    public CharacterProfile(String id, String displayName, String spriteSheetPath,
                             int attackPower, int defensePower, int speed) {
        this.id = id;
        this.displayName = displayName;
        this.spriteSheetPath = spriteSheetPath;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.speed = speed;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getSpriteSheetPath() { return spriteSheetPath; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }
    public int getSpeed() { return speed; }
}