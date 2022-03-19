package ghosking.lormaster;

import java.util.ArrayList;
import java.util.Date;

public final class LoRMatch {

    public final class LoRMatchPlayer {

        private final String puuid;
        private final String deckCode;
        private final boolean wonMatch;

        private LoRMatchPlayer(String puuid, String deckCode, boolean wonMatch) {
            this.puuid = puuid;
            this.deckCode = deckCode;
            this.wonMatch = wonMatch;
        }

        public String getPuuid() {
            return puuid;
        }

        public String getDeckCode() {
            return deckCode;
        }

        public boolean isWonMatch() {
            return wonMatch;
        }
    }

//    private final String gameMode;
//    private final String gameType;
//    private final String gameStartTime;
    // or
//    private final Date gameStartTime;

    private final String id;
    private final String type;
    private final String startTime;

    private ArrayList<LoRMatchPlayer> players;


    // Or something like this...
    private LoRMatch(String id, String type, String startTime, ArrayList<LoRMatchPlayer> players) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.players = players;
    }


//    public static LoRMatch fromID(String id) {
//
//    }

    /**
     * @TODO: make some important design decisions...
     *
     * namely,
     *
     * how would we want this class to be used?
     * the goal is to allow the LoRPlayer class access to getting matches
     * or implementing a getMatchHistory() method which can then be used to display recent games
     * and calculate winrate and meta statistics...
     *
     * do we need this inner LoRMatchPlayer class?
     * or should we instead have three or more ArrayLists for puuids, deckCodes, and wonMatch,
     * where index 0 would correspond to player 1 and index 1 to the other player.
     *
     * either way, a match should only be created given a match puuid, and the LoRMatchPlayer,
     * if we decide to move forward with it, can only be created in this class.
     */

}
