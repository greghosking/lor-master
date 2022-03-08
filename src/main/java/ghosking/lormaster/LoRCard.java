package ghosking.lormaster;

import java.util.ArrayList;

public class LoRCard {

    private final ArrayList<String> associatedCards;
    private final String code;

    private final ArrayList<LoRRegion> regions;
    private final String name;
    private final int cost;
    private final int attack;
    private final int health;
    private final ArrayList<String> keywords;
    private final String spellSpeed;

    private final String type;
    private final ArrayList<String> subtypes;
    private final String supertype;
    private final String rarity;
    private final boolean collectible;

    private final String description;
    private final String levelupDescription;
    private final ArrayList<String> assets;

    protected LoRCard(ArrayList<String> associatedCards, String code, ArrayList<LoRRegion> regions, String name, int cost, int attack, int health, ArrayList<String> keywords, String spellSpeed, String type, ArrayList<String> subtypes, String supertype, String rarity, boolean collectible, String description, String levelupDescription, ArrayList<String> assets) {
        this.associatedCards = associatedCards;
        this.code = code;
        this.regions = regions;
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.health = health;
        this.keywords = keywords;
        this.spellSpeed = spellSpeed;
        this.type = type;
        this.subtypes = subtypes;
        this.supertype = supertype;
        this.rarity = rarity;
        this.collectible = collectible;
        this.description = description;
        this.levelupDescription = levelupDescription;
        this.assets = assets;
    }

    public ArrayList<String> getAssociatedCards() {
        return associatedCards;
    }

    public String getCode() {
        return code;
    }

    public ArrayList<LoRRegion> getRegions() {
        return regions;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public String getSpellSpeed() {
        return spellSpeed;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getSubtypes() {
        return subtypes;
    }

    public String getSupertype() {
        return supertype;
    }

    public String getRarity() {
        return rarity;
    }

    public boolean isCollectible() {
        return collectible;
    }

    public String getDescription() {
        return description;
    }

    public String getLevelupDescription() {
        return levelupDescription;
    }

    public ArrayList<String> getAssets() {
        return assets;
    }
}