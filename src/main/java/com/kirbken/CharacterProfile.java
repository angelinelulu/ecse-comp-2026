package com.kirbken;

public class CharacterProfile {
    private final String id;
    private final String displayName;
    private final String spriteSheetPath;
    private final int hp;
    private final int attackPower;
    private final int defensePower;
    private final int speed;
    private final String rankingTier;
    private final String primaryAction;
    private final String secondaryAction;
    private final String signatureBurst;
    private final String visualDescription;

    public CharacterProfile(String id, String displayName, String spriteSheetPath,
                             int hp, int attackPower, int defensePower, int speed,
                             String rankingTier, String primaryAction,
                             String secondaryAction, String signatureBurst,
                             String visualDescription) {
        this.id = id;
        this.displayName = displayName;
        this.spriteSheetPath = spriteSheetPath;
        this.hp = hp;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.speed = speed;
        this.rankingTier = rankingTier;
        this.primaryAction = primaryAction;
        this.secondaryAction = secondaryAction;
        this.signatureBurst = signatureBurst;
        this.visualDescription = visualDescription;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getSpriteSheetPath() { return spriteSheetPath; }
    public int getHp() { return hp; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }
    public int getSpeed() { return speed; }
    public String getRankingTier() { return rankingTier; }
    public String getPrimaryAction() { return primaryAction; }
    public String getSecondaryAction() { return secondaryAction; }
    public String getSignatureBurst() { return signatureBurst; }
    public String getVisualDescription() { return visualDescription; }
}