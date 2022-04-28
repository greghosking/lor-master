package ghosking.lormaster.lor;

import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

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

        private final LoRType type;
        private final ArrayList<String> subtypes;
        private final String supertype;
        private final LoRRarity rarity;
        private final boolean collectible;

        private final String description;
        private final String levelupDescription;
        private final HashMap<String, String> assetURLs;
        private Image gameAsset;

        /**
         * Constructor is private to ensure that instances of this class are only
         * created through the LoRCardDatabase constructor when called through
         * the method getInstance().
         */
        private LoRCard(ArrayList<String> associatedCardCodes, String code, ArrayList<LoRRegion> regions, String name,
                        int cost, int attack, int health, ArrayList<String> keywords, String spellSpeed, LoRType type,
                        ArrayList<String> subtypes, String supertype, LoRRarity rarity, boolean collectible, String description,
                        String levelupDescription, HashMap<String, String> assetURLs) {
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
            gameAsset = null;
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

        public LoRType getType() {
            return type;
        }

        public ArrayList<String> getSubtypes() {
            return subtypes;
        }

        public String getSupertype() {
            return supertype;
        }

        public LoRRarity getRarity() {
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
            // These are the default width and height of the game asset...
            // (for memory-usage purposes, we scale this down during the load.)
            int w = 680;
            int h = 1024;
            // Load the image if it has not already been loaded.
            if (gameAsset == null) {
                gameAsset = new Image(assetURLs.get("game"), w / 3.5, h / 3.5, false, true, true);

            }
            return gameAsset;
        }

        public Image getFullAsset() {
            // For memory-usage purposes, always reload the full asset instead
            // of storing the loaded image.
            return new Image(assetURLs.get("full"));
        }
    }

    public static final class LoRCardFilter {

        private List<String> cardCodes;

        public LoRCardFilter() {
            this.cardCodes = getInstance().getCardCodes();
        }

        /**
         * Copy constructor
         */
        public LoRCardFilter(LoRCardFilter cardFilter) {
            this.cardCodes = cardFilter.getCardCodes();
        }

        /**
         * @param regionsToInclude A list of LoRRegions indicating which regions of cards
         *                         to include (Demacia, Freljord, etc.).
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match one of the given LoRRegions.
         */
        public LoRCardFilter byRegion(List<LoRRegion> regionsToInclude) {
            List<String> filteredCardCodes = new ArrayList<>();
            boolean sharedRegionFound;

            for (String cardCode : cardCodes) {
                sharedRegionFound = false;
                for (LoRRegion region : getInstance().getCard(cardCode).getRegions())
                    for (LoRRegion regionToInclude : regionsToInclude)
                        if (region.getID() == regionToInclude.getID() && !sharedRegionFound) {
                            filteredCardCodes.add(cardCode);
                            sharedRegionFound = true;
                        }
            }

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param costsToInclude A list of integers indicating which costs of cards
         *                       to include (0, 1, 2, ... 8, 9, 10, ...).
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match one of the given costs.
         */
        public LoRCardFilter byCost(List<Integer> costsToInclude) {
            List<String> filteredCardCodes = new ArrayList<>();

            for (String cardCode : cardCodes)
                if (costsToInclude.contains(getInstance().getCard(cardCode).getCost())) filteredCardCodes.add(cardCode);

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param typesToInclude A list of LoRTypes indicating which types of cards
         *                       to include (unit, spell, etc.).
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match one of the given LoRTypes.
         */
        public LoRCardFilter byType(List<LoRType> typesToInclude) {
            List<String> filteredCardCodes = new ArrayList<>();

            for (String cardCode : cardCodes)
                if (typesToInclude.contains(getInstance().getCard(cardCode).getType())) filteredCardCodes.add(cardCode);

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param supertypesToInclude A list of strings indicating which supertypes
         *                            of cards to include.
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match one of the given supertypes.
         */
        public LoRCardFilter bySupertype(List<String> supertypesToInclude) {
            List<String> filteredCardCodes = new ArrayList<>();

            for (String cardCode : cardCodes) {
                for (String supertype : supertypesToInclude)
                    if (supertype.equalsIgnoreCase(getInstance().getCard(cardCode).getSupertype()))
                        filteredCardCodes.add(cardCode);
            }

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param raritiesToInclude A list of LoRRarities indicating which rarities
         *                          of cards to include (common, rare, etc.).
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match one of the given LoRRarities.
         */
        public LoRCardFilter byRarity(List<LoRRarity> raritiesToInclude) {
            List<String> filteredCardCodes = new ArrayList<>();

            for (String cardCode : cardCodes)
                if (raritiesToInclude.contains(getInstance().getCard(cardCode).getRarity())) filteredCardCodes.add(cardCode);

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param collectible A boolean indicating whether to include collectible
         *                    cards or non-collectible cards.
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that match the given boolean.
         */
        public LoRCardFilter byCollectible(boolean collectible) {
            List<String> filteredCardCodes = new ArrayList<>();

            for (String cardCode : cardCodes)
                if (getInstance().getCard(cardCode).isCollectible() == collectible) filteredCardCodes.add(cardCode);

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @param query The string to search for.
         * @return An instance of LoRCardFilter with its contents being any cards
         *         that contain the query in the name, description, keywords, etc.
         */
        public LoRCardFilter search(String query) {
            List<String> filteredCardCodes = new ArrayList<>();
            query = query.toUpperCase();
            boolean queryFound;

            for (String cardCode : cardCodes) {
                queryFound = false;
                LoRCard card = getInstance().getCard(cardCode);
                // Check if the query string appears in the name, supertype, or descriptions of the card.
                if (card.getName().toUpperCase().contains(query) || card.getSupertype().toUpperCase().contains(query) ||
                        card.getDescription().toUpperCase().contains(query) || card.getLevelupDescription().toUpperCase().contains(query)) {
                    filteredCardCodes.add(cardCode);
                    break;
                }
                // Check if the query string appears in the keywords or subtypes of the card.
                for (String keyword : card.getKeywords())
                    if (keyword.toUpperCase().contains(query)) {
                        filteredCardCodes.add(cardCode);
                        queryFound = true;
                        break;
                    }
                for (String subtype : card.getSubtypes())
                    if (subtype.toUpperCase().contains(query) && !queryFound) {
                        filteredCardCodes.add(cardCode);
                        break;
                    }
            }

            cardCodes = filteredCardCodes;
            return this;
        }

        /**
         * @return An instance of LoRCardFilter with its contents sorted in ascending
         *         order by mana cost and then by name.
         */
        public LoRCardFilter sort() {
            // Filter the current list of card codes by rarity to separate
            // champions from the rest of the cards.
            List<String> championCardCodes = new LoRCardFilter(this).byRarity(List.of(LoRRarity.CHAMPION)).getCardCodes();
            List<String> nonChampionCardCodes = new LoRCardFilter(this).byRarity(Arrays.asList(LoRRarity.COMMON, LoRRarity.RARE, LoRRarity.EPIC, LoRRarity.NONE)).getCardCodes();

            // Sort the two lists by cost ascending (using name instead if two
            // cards have the same costs).
            for (int i = 0; i < championCardCodes.size() - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < championCardCodes.size(); j++) {
                    LoRCard card = getInstance().getCard(championCardCodes.get(j));
                    LoRCard minCostCard = getInstance().getCard(championCardCodes.get(minIndex));

                    if (card.getCost() < minCostCard.getCost())
                        minIndex = j;
                    else if (card.getCost() == minCostCard.getCost() && card.getName().compareToIgnoreCase(minCostCard.getName()) < 1)
                        minIndex = j;
                }
                String placeholder = championCardCodes.get(i);
                championCardCodes.set(i, championCardCodes.get(minIndex));
                championCardCodes.set(minIndex, placeholder);
            }

            for (int i = 0; i < nonChampionCardCodes.size() - 1; i++) {
                int minIndex = i;
                for (int j = i + 1; j < nonChampionCardCodes.size(); j++) {
                    LoRCard card = getInstance().getCard(nonChampionCardCodes.get(j));
                    LoRCard minCostCard = getInstance().getCard(nonChampionCardCodes.get(minIndex));

                    if (card.getCost() < minCostCard.getCost())
                        minIndex = j;
                    else if (card.getCost() == minCostCard.getCost() && card.getName().compareToIgnoreCase(minCostCard.getName()) < 1)
                        minIndex = j;
                }
                String placeholder = nonChampionCardCodes.get(i);
                nonChampionCardCodes.set(i, nonChampionCardCodes.get(minIndex));
                nonChampionCardCodes.set(minIndex, placeholder);
            }

            cardCodes.clear();
            cardCodes.addAll(championCardCodes);
            cardCodes.addAll(nonChampionCardCodes);
            return this;
        }

        /**
         * @return The contents of the LoRCardFilter.
         */
        public List<String> getCardCodes() {
            return cardCodes;
        }
    }

    private static LoRCardDatabase instance;
    private final HashMap<String, LoRCard> cards;

    private LoRCardDatabase() {
        cards = new HashMap<>();

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

                LoRType type = LoRType.valueOf(cardJSONObj.getString("type").toUpperCase());
                ArrayList<String> subtypes = new ArrayList<>();
                for (var subtypeObj : cardJSONObj.getJSONArray("subtypes")) {
                    subtypes.add(subtypeObj.toString());
                }
                String supertype = cardJSONObj.getString("supertype");
                LoRRarity rarity = LoRRarity.valueOf(cardJSONObj.getString("rarity").toUpperCase());
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
}
