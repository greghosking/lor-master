package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRPlayer;
import ghosking.lormaster.lor.LoRRegion;
import ghosking.lormaster.lor.LoRRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {

    /**
     * Create a custom cell renderer for the leaderboard ListViews.
     */
    private class LeaderboardListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            // Clear the background and set the text color.
            setBackground(Background.EMPTY);
            setFont(Font.font("Lucida Console", 11));
            setTextFill(Color.valueOf("#FFF1DA"));
        }
    }

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
        System.out.println("Initializing the leaderboard...");

        // Link the other navigation buttons to their respective scenes.
        profileNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        communityNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCommunityScene());
        collectionNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        // Set each leaderboard ListView to use the custom cell renderer defined above.
        americasLeaderboardListView.setCellFactory(list -> new LeaderboardListCell());
        europeLeaderboardListView.setCellFactory(list -> new LeaderboardListCell());
        seaLeaderboardListView.setCellFactory(list -> new LeaderboardListCell());
        worldLeaderboardListView.setCellFactory(list -> new LeaderboardListCell());

        // Start a separate thread to get and sort the leaderboard data in the background.
        Thread leaderboardThread = new Thread(() -> {
            worldLeaderboard = new ArrayList<>();
            ArrayList<LoRPlayer> americasLeaderboard = getRegionLeaderboard("americas");
            ArrayList<LoRPlayer> europeLeaderboard = getRegionLeaderboard("europe");
            ArrayList<LoRPlayer> seaLeaderboard = getRegionLeaderboard("sea");

            // Sort the world leaderboard by LP descending and reassign rank as necessary.
            for (int i = 0; i < worldLeaderboard.size() - 1; i++) {
                int indexOfMostLP = i;
                for (int j = i + 1; j < worldLeaderboard.size(); j++) {
                    if (worldLeaderboard.get(j).getLP() > worldLeaderboard.get(indexOfMostLP).getLP()) {
                        indexOfMostLP = j;
                    }
                }
                LoRPlayer placeholder = worldLeaderboard.get(i);
                worldLeaderboard.set(i, worldLeaderboard.get(indexOfMostLP));
                worldLeaderboard.set(indexOfMostLP, placeholder);
            }
            for (int i = 0; i < worldLeaderboard.size(); i++) {
                LoRPlayer player = worldLeaderboard.get(i);
                // Create a new instance of LoRPlayer in place of the original
                // to not modify the player's rank in their respective region.
                worldLeaderboard.set(i, new LoRPlayer(player.getGameName(), i + 1, player.getLP()));
            }

            // Add all players from each leaderboard ArrayList to the corresponding
            // leaderboard ListView to be rendered.
            for (LoRPlayer player : americasLeaderboard) {
                americasLeaderboardListView.getItems().add(player.toString());
            }
            for (LoRPlayer player : europeLeaderboard) {
                europeLeaderboardListView.getItems().add(player.toString());
            }
            for (LoRPlayer player : seaLeaderboard) {
                seaLeaderboardListView.getItems().add(player.toString());
            }
            for (LoRPlayer player : worldLeaderboard) {
                worldLeaderboardListView.getItems().add(player.toString());
            }
        });
        leaderboardThread.start();
    }
}
