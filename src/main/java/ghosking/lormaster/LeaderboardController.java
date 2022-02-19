package ghosking.lormaster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    private enum Region {
        AMERICAS, EUROPE, SEA
    }

    public static ArrayList<LeaderboardEntry> getRegionLeaderboard(Region region) {

        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();

        // Create the request URL from the given region name and existing API key.
        String url = "https://" + region.name().toLowerCase() + ".api.riotgames.com"
                + "/lor/ranked/v1/leaderboards?api_key=" + RequestHandler.API_KEY;

        // Returns a JSON string containing a list of all players in Masters rank in a given region.
        // The string is then parsed into an array of JSON elements (with each element representing a player).
        /**
         * @TODO: JsonParser is deprecated. Investigate alternatives to parse JSON.
         */
        String leaderboardJsonString = RequestHandler.get(url);
        JsonArray leaderboardJsonArray = new JsonParser().parse(leaderboardJsonString)
                .getAsJsonObject().get("players").getAsJsonArray();

        for (var leaderboardEntryJson : leaderboardJsonArray) {
            JsonObject leaderboardEntryObj = leaderboardEntryJson.getAsJsonObject();

            String name = leaderboardEntryObj.get("name").getAsString();
            int rank = leaderboardEntryObj.get("rank").getAsInt() + 1;
            int lp = leaderboardEntryObj.get("lp").getAsInt();

            leaderboard.add(new LeaderboardEntry(name, rank, lp));
        }

        return leaderboard;
    }

    public static void printLeaderboard(ArrayList<LeaderboardEntry> leaderboard) {

        for (LeaderboardEntry entry : leaderboard) {
            System.out.println(entry);
        }
    }

    @FXML
    private ListView<String> americasListView;
    @FXML
    private ListView<String> europeListView;
    @FXML
    private ListView<String> seaListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // There are three leaderboards for the regions of the world: Americas, Europe, and Sea.
        ArrayList<LeaderboardEntry> americasLeaderboard = getRegionLeaderboard(Region.AMERICAS);
        ArrayList<LeaderboardEntry> europeLeaderboard   = getRegionLeaderboard(Region.EUROPE);
        ArrayList<LeaderboardEntry> seaLeaderboard      = getRegionLeaderboard(Region.SEA);

        System.out.println("AMERICAS LEADERBOARD " + "(" + americasLeaderboard.size() + " Masters players)");
        System.out.println("--------------------------------------------------");
        printLeaderboard(americasLeaderboard);
        System.out.println();

        System.out.println("EUROPE LEADERBOARD " + "(" + europeLeaderboard.size() + " Masters players)");
        System.out.println("--------------------------------------------------");
        printLeaderboard(europeLeaderboard);
        System.out.println();

        System.out.println("SEA LEADERBOARD " + "(" + seaLeaderboard.size() + " Masters players)");
        System.out.println("--------------------------------------------------");
        printLeaderboard(seaLeaderboard);
        System.out.println();

        for (LeaderboardEntry entry : americasLeaderboard) {
            americasListView.getItems().add(entry.toString());
        }
        for (LeaderboardEntry entry : europeLeaderboard) {
            europeListView.getItems().add(entry.toString());
        }
        for (LeaderboardEntry entry : seaLeaderboard) {
            seaListView.getItems().add(entry.toString());
        }
    }
}
