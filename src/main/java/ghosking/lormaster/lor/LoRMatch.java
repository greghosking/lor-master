package ghosking.lormaster.lor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public final class LoRMatch {

    public static final class LoRMatchParticipant {

        private final String puuid;
        private final String deckCode;
        private final boolean wonMatch;

        /**
         * Constructor is private to ensure that instances of this class are only
         * created through fromID(String id).
         */
        private LoRMatchParticipant(String puuid, String deckCode, boolean wonMatch) {
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

    private final String id;
    private final String type;
    private final LocalDateTime startDateTime;
    private final ArrayList<LoRMatchParticipant> participants;

    /**
     * Constructor is private to ensure that instances of this class are only created
     * through fromID(String id).
     */
    private LoRMatch(String id, String type, LocalDateTime startTime, ArrayList<LoRMatchParticipant> participants) {
        this.id = id;
        this.type = type;
        this.startDateTime = startTime;
        this.participants = participants;
    }

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public ArrayList<LoRMatchParticipant> getParticipants() {
        return participants;
    }

    /**
     * @param id The match ID.
     * @return An instance of LoRMatch that corresponds to the ID, if one exists,
     *         otherwise throw an exception.
     */
    public static LoRMatch fromID(String id) {
        // Create the request URL for the match ID.
        String url = "https://americas.api.riotgames.com/lor/match/v1/matches/" +
                id + "?api_key=" + LoRRequest.apiKey;

        // Returns a string containing the match data in JSON format, or null
        // if there is no existing match for the specified ID.
        String matchJSON = LoRRequest.get(url);
        if (matchJSON == null) {
            throw new RuntimeException("Unrecognized match ID: " + id);
        }

        // Get the JSONObject for the match info and parse it for the type of the
        // match, the start time, and the participants.
        JSONObject matchInfoJSONObj = new JSONObject(matchJSON).getJSONObject("info");
        String type = matchInfoJSONObj.getString("game_type");
        // Start time is formatted as "2022-03-11T18:36:51.7306788+00:00", but
        // this needs to be transformed to "2022-03-11T18:36:51Z"...
        String startDateTimeString = matchInfoJSONObj.getString("game_start_time_utc");
        startDateTimeString = startDateTimeString.substring(0, startDateTimeString.indexOf(".")) + "Z";
        LocalDateTime startDateTime = LocalDateTime.ofInstant((Instant.parse(startDateTimeString)), ZoneId.systemDefault());
        JSONArray participantsJSONArray = matchInfoJSONObj.getJSONArray("players");

        // Parse each element of the participants JSONArray to create a new LoRMatchParticipant
        // which is added to the participants ArrayList.
        ArrayList<LoRMatchParticipant> participants = new ArrayList<>();
        for (Object participantObj : participantsJSONArray) {
            JSONObject participantJSONObj = (JSONObject) participantObj;
            String puuid = participantJSONObj.getString("puuid");
            String deckCode = participantJSONObj.getString("deck_code");
            boolean wonMatch = participantJSONObj.getString("game_outcome").compareTo("win") == 0;
            participants.add(new LoRMatchParticipant(puuid, deckCode, wonMatch));
        }

        return new LoRMatch(id, type, startDateTime, participants);
    }
}
