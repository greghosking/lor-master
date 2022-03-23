package ghosking.lormaster.lor;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;

public final class LoRCard {

    private final ArrayList<String> associatedCardCodes;
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
    private final HashMap<String, String> assetURLs;
    private final HashMap<String, Image> assets;

    public LoRCard(ArrayList<String> associatedCardCodes, String code, ArrayList<LoRRegion> regions, String name, int cost, int attack, int health, ArrayList<String> keywords, String spellSpeed, String type, ArrayList<String> subtypes, String supertype, String rarity, boolean collectible, String description, String levelupDescription, HashMap<String, String> assetURLs) {
        this.associatedCardCodes = associatedCardCodes;
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
        this.assetURLs = assetURLs;
        assets = new HashMap<>();
    }

    public ArrayList<String> getAssociatedCardCodes() {
        return associatedCardCodes;
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

    public Image getGameAsset() {
        // If the image does not already exist in assets, create the image using
        // the corresponding URL and put it in assets.
        if (!assets.containsKey("game")) {
            assets.put("game", new Image(assetURLs.get("game")));
        }
        return assets.get("game");
    }

    public Image getFullAsset() {
        // If the image does not already exist in assets, create the image using
        // the corresponding URL and put it in assets.
        if (!assets.containsKey("full")) {
            assets.put("full", new Image(assetURLs.get("full")));
        }
        return assets.get("full");
    }
}