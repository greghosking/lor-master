package ghosking.lormaster.lor;

import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class LoRCardDatabase {

    public static final class LoRCard {

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

        /**
         * Constructor is private to ensure that instances of this class are only
         * created through the LoRCardDatabase constructor when called through
         * the method getInstance().
         */
        private LoRCard(ArrayList<String> associatedCardCodes, String code, ArrayList<LoRRegion> regions, String name, int cost, int attack, int health, ArrayList<String> keywords, String spellSpeed, String type, ArrayList<String> subtypes, String supertype, String rarity, boolean collectible, String description, String levelupDescription, HashMap<String, String> assetURLs) {
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

        public int getSet() {
            return Integer.parseInt(code.substring(0, 2));
        }

        public LoRRegion getRegion() {
            return LoRRegion.fromCode(code.substring(2, 4));
        }

        public int getID() {
            return Integer.parseInt(code.substring(4, 7));
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

    private static LoRCardDatabase instance;
    private final HashMap<String, LoRCard> cards;

    private LoRCardDatabase() {
        cards = new HashMap<>();

        // @TODO: Find a better way to handle this... (Custom exceptions in LoRRequest class.)
        int set = 1;
        while (true) {
            // Create the request URL for the current set.
            String url = "https://dd.b.pvp.net/latest/set" + set + "/en_us/data/set" + set + "-en_us.json";

            // Returns a string containing a list of all cards in the current set
            // in JSON format, or null if the set does not exist and thus all cards
            // are already in the database.
            String cardsJSON = LoRRequest.get(url);
            if (cardsJSON == null) {
                break;
            }

            JSONArray cardsJSONArray = new JSONArray(cardsJSON);
            // Parse each element of the JSONArray to create a card to be added
            // to the database.
            for (Object cardObj : cardsJSONArray) {
                JSONObject cardJSONObj = (JSONObject) cardObj;

                ArrayList<String> associatedCardCodes = new ArrayList<>();
                for (var cardCodeObj : cardJSONObj.getJSONArray("associatedCardRefs")) {
                    associatedCardCodes.add(cardCodeObj.toString());
                }
                String code = cardJSONObj.getString("cardCode");

                ArrayList<LoRRegion> regions = new ArrayList<>();
                for (var regionObj : cardJSONObj.getJSONArray("regions")) {
                    regions.add(LoRRegion.fromName(regionObj.toString()));
                }
                String name = cardJSONObj.getString("name");
                int cost = cardJSONObj.getInt("cost");
                int attack = cardJSONObj.getInt("attack");
                int health = cardJSONObj.getInt("health");
                ArrayList<String> keywords = new ArrayList<>();
                for (var keywordObj : cardJSONObj.getJSONArray("keywords")) {
                    keywords.add(keywordObj.toString());
                }
                String spellSpeed = cardJSONObj.getString("spellSpeed");

                String type = cardJSONObj.getString("type");
                ArrayList<String> subtypes = new ArrayList<>();
                for (var subtypeObj : cardJSONObj.getJSONArray("subtypes")) {
                    subtypes.add(subtypeObj.toString());
                }
                String supertype = cardJSONObj.getString("supertype");
                String rarity = cardJSONObj.getString("rarity");
                boolean collectible = cardJSONObj.getBoolean("collectible");

                String description = cardJSONObj.getString("descriptionRaw");
                String levelupDescription = cardJSONObj.getString("levelupDescriptionRaw");

                // The asset URLs must be formatted to https instead of http.
                HashMap<String, String> assetURLs = new HashMap<>();
                String gameAssetURL = ((JSONObject) cardJSONObj.getJSONArray("assets").get(0)).getString("gameAbsolutePath");
                gameAssetURL = gameAssetURL.replace(":", "s:");
                assetURLs.put("game", gameAssetURL);
                String fullAssetURL = ((JSONObject) cardJSONObj.getJSONArray("assets").get(0)).getString("fullAbsolutePath");
                fullAssetURL = fullAssetURL.replace(":", "s:");
                assetURLs.put("full", fullAssetURL);

                cards.put(code, new LoRCard(associatedCardCodes, code, regions, name, cost, attack, health, keywords, spellSpeed, type, subtypes, supertype, rarity, collectible, description, levelupDescription, assetURLs));
            }

            set++;
        }
    }

    public static LoRCardDatabase getInstance() {
        if (instance == null) {
            instance = new LoRCardDatabase();
        }
        return instance;
    }

    public LoRCard getCard(String code) {
        return cards.get(code);
    }

    public ArrayList<String> getCardCodes() {
        return new ArrayList<>(cards.keySet());
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByRegion(ArrayList<String> cardCodes, boolean includeDE, boolean includeFR, boolean includeIO, boolean includeNX, boolean includePZ, boolean includeSI, boolean includeBW, boolean includeMT, boolean includeSH, boolean includeBC) {

        return cardCodes;
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByName(ArrayList<String> cardCodes, String name) {

        return cardCodes;
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByCost(ArrayList<String> cardCodes, boolean include1OrLessCost, boolean include2Cost) {

        return cardCodes;
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByKeyword(ArrayList<String> cardCodes, String keyword) {

        return cardCodes;
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByType(ArrayList<String> cardCodes, boolean includeUnits, boolean includeSpells, boolean includeLandmarks) {

        return cardCodes;
    }

    // @TODO: Add documentation.
    public ArrayList<String> getCardCodesByRarity(ArrayList<String> cardCodes, boolean includeCommon, boolean includeRare, boolean includeEpic, boolean includeChampion) {
        // Search through the given list of card codes, removing any cards that
        // do not have any of the requested rarities.
        for (int i = cardCodes.size() - 1; i >= 0; i--) {
            String rarity = getCard(cardCodes.get(i)).getRarity();
            String type = getCard(cardCodes.get(i)).getType();
            String supertype = getCard(cardCodes.get(i)).getSupertype();
            if (!((includeCommon && rarity.equalsIgnoreCase("COMMON")) || (includeRare && rarity.equalsIgnoreCase("RARE")) || (includeEpic && rarity.equalsIgnoreCase("EPIC")) || (includeChampion && type.equalsIgnoreCase("UNIT") && supertype.equalsIgnoreCase("CHAMPION")))) {
                cardCodes.remove(i);
            }
        }

        return cardCodes;
    }

    // @TODO: Implement this and add documentation.
    public ArrayList<String> getCardCodesByCollectible(ArrayList<String> cardCodes, boolean includeCollectible) {

        return cardCodes;
    }
}
