package ghosking.lormaster.lor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public final class LoRCardDatabase {

    private static LoRCardDatabase instance;
    private HashMap<String, LoRCard> cards;

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
        System.out.println(cards.size());
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
        ArrayList<String> cardCodes = new ArrayList<>();
        cardCodes.addAll(cards.keySet());
        return cardCodes;
    }

    // @TODO
    public ArrayList<String> getCardCodesByRegion(ArrayList<String> cardCodes, boolean includeRegionX) {

        return null;
    }

    // @TODO
    public ArrayList<String> getCardCodesByCost(ArrayList<String> cardCodes, boolean includeXCost) {

        return null;
    }

//    // @TODO
//    public ArrayList<String> getCardCodesByAttack() {
//
//        return null;
//    }
//
//    // @TODO:
//    public ArrayList<String> getCardCodesByHealth() {
//
//        return null;
//    }
}
