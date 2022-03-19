package ghosking.lormaster;

import java.util.HashMap;

public class LoRPlayerDatabase {

    private static LoRPlayerDatabase instance;
    private LoRPlayer activePlayer;
    private HashMap<String, LoRPlayer> players;

    private LoRPlayerDatabase() {

    }

    public static LoRPlayerDatabase getInstance() {
        if (instance == null) {
            instance = new LoRPlayerDatabase();
        }
        return instance;
    }

    public LoRPlayer getPlayer(String puuid) {
        return null;
    }

}
