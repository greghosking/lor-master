package ghosking.lormaster.lor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public final class LoRPlayer {

    private final String puuid;
    private final String gameName;
    private final String tagLine;

    private int rank;
    private int lp;

    /**
     * Constructor is private to ensure that instances of this class are only created
     * through one of the methods below:
     * fromPUUID(String puuid), fromRiotID(String gameName, String tagLine).
     */
    private LoRPlayer(String puuid, String gameName, String tagLine) {
        this.puuid = puuid;
        this.gameName = gameName;
        this.tagLine = tagLine;
    }

    /**
     * Other constructor is public to allow instances of this class to be created
     * to store ranked information about a player without credentials.
     */
    public LoRPlayer(String gameName, int rank, int lp) {
        this.gameName = gameName;
        this.rank = rank;
        this.lp = lp;

        puuid = null;
        tagLine = null;
    }

    public String getPUUID() {
        return puuid;
    }

    public String getGameName() {
        return gameName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        if (rank > 0) {
            this.rank = rank;
        }
    }

    public int getLP() {
        return lp;
    }

    public void setLP(int lp) {
        if (lp >= 0) {
            this.lp = lp;
        }
    }

    @Override
    public String toString() {
        return rank + ". " + gameName + " (" + lp + ")";
    }

    public ArrayList<LoRMatch> getMatchHistory() {
        // Create the request URL for the player match history using the player PUUID.
        String url = "https://americas.api.riotgames.com/lor/match/v1/matches/by-puuid/" +
                puuid + "/ids?api_key=" + LoRRequest.apiKey;

        // Returns a string containing a list of the player most recent twenty
        // match IDs in JSON format,
        String matchIDsJSON = LoRRequest.get(url);
        JSONArray matchIDsJSONArray = new JSONArray(matchIDsJSON);

        // Parse each element of the matchIDs JSONArray to create a new LoRMatch
        // which is added to the matchHistory ArrayList.
        ArrayList<LoRMatch> matchHistory = new ArrayList<>();
        for (Object matchIDObj : matchIDsJSONArray) {
            matchHistory.add(LoRMatch.fromID(matchIDObj.toString()));
        }

        return matchHistory;
    }

    /**
     * @param puuid The player PUUID (ID).
     * @return An instance of LoRPlayer that corresponds to the PUUID, if one exists,
     *         otherwise throw an exception.
     */
    public static LoRPlayer fromPUUID(String puuid) {
        // Create the request URL for the player PUUID.
        String url = "https://americas.api.riotgames.com/riot/account/v1/accounts/by-puuid/" +
                puuid + "?api_key=" + LoRRequest.apiKey;

        // Returns a string containing the player data in JSON format, or null if
        // there is no existing player for the specified PUUID.
        String playerJSON = LoRRequest.get(url);
        if (playerJSON == null) {
            throw new RuntimeException("Unrecognized player PUUID: " + puuid);
        }

        // Get the JSONObject for the player and parse it for the player gameName
        // and tagLine.
        JSONObject playerJSONObject = new JSONObject(playerJSON);
        String gameName = playerJSONObject.getString("gameName");
        String tagLine = playerJSONObject.getString("tagLine");

        return new LoRPlayer(puuid, gameName, tagLine);
    }

    /**
     * @param gameName The player gameName (part one of the player Riot ID).
     * @param tagLine The player tagLine (part two of the player Riot ID).
     * @return An instance of LoRPlayer that corresponds to the Riot ID, if one
     *         exists, otherwise throw an exception.
     */
    public static LoRPlayer fromRiotID(String gameName, String tagLine) {
        // Ensure that the gameName and tagLine are properly formatted:
        // Replace whitespace in the gameName with %20.
        String formattedGameName = gameName.replace(" ", "%20");
        // Remove any # characters or whitespace from the tagLine and get up to
        // the first five characters (since the expected length is 3-5 characters).
        tagLine = tagLine.replace("#", "").replace(" ", "");
        tagLine = tagLine.substring(0, Math.min(tagLine.length(), 5));

        // Create the request URL for the player Riot ID.
        String url = "https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/" +
                gameName + "/" + tagLine + "?api_key=" + LoRRequest.apiKey;

        // Returns a string containing the player data in JSON format, or null if
        // there is no existing player for the specified Riot ID.
        String playerJSON = LoRRequest.get(url);
        if (playerJSON == null) {
            throw new RuntimeException("Unrecognized player Riot ID: " + gameName + " / " + tagLine);
        }

        // Get the JSONObject for the player and parse it for the player PUUID.
        JSONObject playerJSONObject = new JSONObject(playerJSON);
        String puuid = playerJSONObject.getString("puuid");

        return new LoRPlayer(puuid, gameName, tagLine);
    }
}
