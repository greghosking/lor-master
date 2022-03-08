package ghosking.lormaster;

import java.util.ArrayList;
import java.util.HashMap;

public class LoRDeck {

    private HashMap<String, Integer> cards;

    public LoRDeck() {
        cards = new HashMap<>();
    }

    public LoRDeck(String deckCode) {
        // @TODO: Parse deck code into individual card codes and create deck.
        cards = new HashMap<>();
    }

    public String getDeckCode() {
        // @TODO: Encode the card codes into a deck code.
        return "";
    }

    public HashMap<String, Integer> getCards() {
        return null;
    }

    public int getNumberOfCards() {
        return -1;
    }

    public int getNumberOfChampions() {
        return -1;
    }

    public ArrayList<LoRRegion> getRegions() {
        return null;
    }

    public void add(String cardCode) {

    }

    public void remove(String cardCode) {

    }

    public void clear() {
        // reset deck
    }
}
