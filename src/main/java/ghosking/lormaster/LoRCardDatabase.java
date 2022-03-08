package ghosking.lormaster;

import java.util.HashMap;

public class LoRCardDatabase {

    private static LoRCardDatabase instance;
    private HashMap<String, LoRCard> cards;

    private LoRCardDatabase() {

        int set = 1;
    }

    public static LoRCardDatabase getInstance() {
        if (instance == null) {
            instance = new LoRCardDatabase();
        }
        return instance;
    }
}
