package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRPlayer;
import ghosking.lormaster.lor.LoRRegion;
import ghosking.lormaster.lor.LoRRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    @FXML
    Button profileNavigationButton;
    @FXML
    Button communityNavigationButton;
    @FXML
    Button collectionNavigationButton;
    @FXML
    Button decksNavigationButton;
    @FXML
    Button metaNavigationButton;
    @FXML
    ListView<String> americasLeaderboardListView;
    @FXML
    ListView<String> europeLeaderboardListView;
    @FXML
    ListView<String> seaLeaderboardListView;
    @FXML
    ListView<String> worldLeaderboardListView;

    private static ArrayList<LoRPlayer> worldLeaderboard;

    private ArrayList<LoRPlayer> getRegionLeaderboard(String region) {
        // Create the request URL for the region.
        String url = "https://" + region.toLowerCase() + ".api.riotgames.com" +
                "/lor/ranked/v1/leaderboards?api_key=" + LoRRequest.apiKey;

        // Returns a string containing a list of all players in Masters rank for
        // the given region in JSON format, or null if the region does not exist.
        String leaderboardJSONString = LoRRequest.get(url);
        if (leaderboardJSONString == null) {
            throw new RuntimeException("Unrecognized region: " + region);
        }

        JSONArray leaderboardJSONArray = (JSONArray) new JSONObject(leaderboardJSONString).get("players");
        // Parse each element of the leaderboard JSONArray to create a new LoRPlayer
        // which is added to the leaderboard ArrayList and worldLeaderboard ArrayList.
        ArrayList<LoRPlayer> leaderboard = new ArrayList<>();
        for (Object playerObj : leaderboardJSONArray) {
            JSONObject playerJSONObj = (JSONObject)  playerObj;
            String gameName = playerJSONObj.getString("name");
            int rank = playerJSONObj.getInt("rank") + 1;
            int lp = playerJSONObj.getInt("lp");
            LoRPlayer player = new LoRPlayer(gameName, rank, lp);
            leaderboard.add(player);
            worldLeaderboard.add(player);
        }

        return leaderboard;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link the other navigation buttons to their respective scenes.
        profileNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        communityNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCommunityScene());
        collectionNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        Thread leaderboardThread = new Thread(() -> {
            // Get the leaderboards for the three regions of the world
            worldLeaderboard = new ArrayList<>();
            ArrayList<LoRPlayer> americasLeaderboard = getRegionLeaderboard("americas");
            ArrayList<LoRPlayer> europeLeaderboard = getRegionLeaderboard("europe");
            ArrayList<LoRPlayer> seaLeaderboard = getRegionLeaderboard("sea");

            for (LoRPlayer player : americasLeaderboard) {
                americasLeaderboardListView.getItems().add(player.getGameName());
            }
            for (LoRPlayer player : europeLeaderboard) {
                europeLeaderboardListView.getItems().add(player.getGameName());
            }
            for (LoRPlayer player : seaLeaderboard) {
                seaLeaderboardListView.getItems().add(player.getGameName());
            }
            for (LoRPlayer player : worldLeaderboard) {
                worldLeaderboardListView.getItems().add(player.getGameName());
            }
        });
        leaderboardThread.start();


    }

}
