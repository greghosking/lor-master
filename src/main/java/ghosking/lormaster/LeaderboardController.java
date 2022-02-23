package ghosking.lormaster;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

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

        // Returns a string containing a list of all players in Masters rank in a given region in JSON format.
        // The string is then parsed into a JSONArray (with each element representing a player).
        String leaderboardJSONString = RequestHandler.get(url);
        JSONArray leaderboardJSONArray = (JSONArray) new JSONObject(leaderboardJSONString).get("players");

        // Parse each element of the JSONArray to create an entry using the name, rank, and lp of the player
        // to be added to the leaderboard.
        for (var element : leaderboardJSONArray) {
            JSONObject obj = (JSONObject) element;
            leaderboard.add(new LeaderboardEntry(obj.getString("name"), obj.getInt("rank") + 1, obj.getInt("lp")));
        }

        return leaderboard;
    }

    public static ArrayList<LeaderboardEntry> getGlobalLeaderboard() {

        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();

        // Add the leaderboards from each region together.
        for (Region region : Region.values())
            leaderboard.addAll(getRegionLeaderboard(region));

        // Sort the global leaderboard by LP descending and reassign rank as necessary.
        LeaderboardEntry placeholder;
        for (int i = 0; i < leaderboard.size() - 1; i++) {
            int indexOfMostLP = i;
            for (int j = i + 1; j < leaderboard.size(); j++) {
                if (leaderboard.get(j).getLP() > leaderboard.get(indexOfMostLP).getLP())
                    indexOfMostLP = j;
            }
            placeholder = leaderboard.get(i);
            leaderboard.set(i, leaderboard.get(indexOfMostLP));
            leaderboard.set(indexOfMostLP, placeholder);
        }
        for (int i = 0; i < leaderboard.size(); i++)
            leaderboard.get(i).setRank(i + 1);

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
    @FXML
    private ListView<String> globalListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // There are three leaderboards for the regions of the world: Americas, Europe, and Sea.
        ArrayList<LeaderboardEntry> americasLeaderboard = getRegionLeaderboard(Region.AMERICAS);
        ArrayList<LeaderboardEntry> europeLeaderboard   = getRegionLeaderboard(Region.EUROPE);
        ArrayList<LeaderboardEntry> seaLeaderboard      = getRegionLeaderboard(Region.SEA);

        // To see the top player rankings regardless of region, get the global leaderboard.
        ArrayList<LeaderboardEntry> globalLeaderboard   = getGlobalLeaderboard();

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

        System.out.println("GLOBAL LEADERBOARD " + "(" + globalLeaderboard.size() + " Masters players)");
        System.out.println("--------------------------------------------------");
        printLeaderboard(globalLeaderboard);
        System.out.println();

        // Update each list view with the respective leaderboard data.
        for (LeaderboardEntry entry : americasLeaderboard)
            americasListView.getItems().add(entry.toString());
        for (LeaderboardEntry entry : europeLeaderboard)
            europeListView.getItems().add(entry.toString());
        for (LeaderboardEntry entry : seaLeaderboard)
            seaListView.getItems().add(entry.toString());
        for (LeaderboardEntry entry : globalLeaderboard)
            globalListView.getItems().add(entry.toString());
    }
}
