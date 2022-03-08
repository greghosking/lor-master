package ghosking.lormaster;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoRCardDatabase {

    private static LoRCardDatabase instance;
    private HashMap<String, LoRCard> cards;

    private LoRCardDatabase() {

        cards = new HashMap<>();

        // Allocate LoRCard fields for future construction.
        ArrayList<String> associatedCards;
        String code;
        ArrayList<LoRRegion> regions;
        String name;
        int cost;
        int attack;
        int health;
        ArrayList<String> keywords;
        String spellSpeed;
        String type;
        ArrayList<String> subtypes;
        String supertype;
        String rarity;
        boolean collectible;
        String description;
        String levelupDescription;
        ArrayList<String> assets;
        String temp;

        int set = 1;
        while (true) {
            // Create the request URL for the current set.
            String url = "https://dd.b.pvp.net/latest/set" + set + "/en_us/data/set" + set + "-en_us.json";

            // Returns a string containing a list all cards in the current set in JSON format,
            // or null if the set does not exist and thus all cards are in the database.
            String cardsJSON = LoRAPIRequest.get(url);
            if (cardsJSON == null) {
                break;
            }
            JSONArray cardsJSONArray = new JSONArray(cardsJSON);

            // Parse each element of the JSONArray to create a card to be added to the database.
            for (var element : cardsJSONArray) {
                JSONObject obj = (JSONObject) element;

                associatedCards = new ArrayList<>();
                for (var cardCodeObj : obj.getJSONArray("associatedCardRefs")) {
                    associatedCards.add(cardCodeObj.toString());
                }
                code = obj.getString("cardCode");
                regions = new ArrayList<>();
                for (var regionObj : obj.getJSONArray("regions")) {
                    regions.add(LoRRegion.fromName(regionObj.toString()));
                }
                name = obj.getString("name");
                cost = obj.getInt("cost");
                attack = obj.getInt("attack");
                health = obj.getInt("health");
                keywords = new ArrayList<>();
                for (var keywordObj : obj.getJSONArray("keywords")) {
                    keywords.add(keywordObj.toString());
                }
                spellSpeed = obj.getString("spellSpeed");
                type = obj.getString("type");
                subtypes = new ArrayList<>();
                for (var subtypeObj : obj.getJSONArray("subtypes")) {
                    subtypes.add(subtypeObj.toString());
                }
                supertype = obj.getString("supertype");
                rarity = obj.getString("rarity");
                collectible = obj.getBoolean("collectible");
                description = obj.getString("descriptionRaw");
                levelupDescription = obj.getString("levelupDescriptionRaw");
                // These assets are image urls, but must be formatted to https instead of http.
                assets = new ArrayList<>();
                temp = ((JSONObject) obj.getJSONArray("assets").get(0)).getString("gameAbsolutePath");
                temp = temp.substring(0, temp.indexOf(':')) + 's' + temp.substring(temp.indexOf(':'));
                assets.add(temp);
                temp = ((JSONObject) obj.getJSONArray("assets").get(0)).getString("fullAbsolutePath");
                temp = temp.substring(0, temp.indexOf(':')) + 's' + temp.substring(temp.indexOf(':'));
                assets.add(temp);

                cards.put(code, new LoRCard(associatedCards, code, regions, name, cost, attack, health, keywords, spellSpeed, type, subtypes, supertype, rarity, collectible, description, levelupDescription, assets));
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

        ArrayList<String> cardCodes = new ArrayList<>();

        for (String code : cards.keySet()) {
            cardCodes.add(code);
        }

        return cardCodes;
    }
}
