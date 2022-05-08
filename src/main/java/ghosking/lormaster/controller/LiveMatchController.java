package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRCardDatabase;
import ghosking.lormaster.lor.LoRDeck;
import ghosking.lormaster.lor.LoRDeckEncoder;
import ghosking.lormaster.lor.LoRRequest;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class LiveMatchController implements Initializable {

    @FXML
    Button profileButton, collectionButton, decksButton, leaderboardButton, metaButton;
    @FXML
    Label playerDeckLabel, opponentDeckLabel;
    @FXML
    ListView<String> playerDeckListView, opponentDeckListView;
    @FXML
    Label matchNotFoundLabel1, matchNotFoundLabel2;

    LoRDeck playerDeck, opponentDeck;
    List<Integer> playerCardIDs, opponentCardIDs;

    private void setupSceneButtons() {
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

    private void updateUI() {
        Service<Void> updateUIService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws InterruptedException {
                        String url = "http://127.0.0.1:21337/positional-rectangles";
                        // Returns a string containing the live match data in JSON format, or null
                        // if the game is not currently open. If the game is open, "PlayerName" and
                        // others will be null if there is no active match.
                        String liveMatchJSON = LoRRequest.get(url);

                        // If there is no active match, swap the UI to alert the user.
                        boolean isMatchActive = false;
                        if (liveMatchJSON != null) {
                            try {
                                String gameState = new JSONObject(liveMatchJSON).get("GameState").toString();
                                if (gameState != null && gameState.equalsIgnoreCase("InProgress")) isMatchActive = true;
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                                System.exit(1);
                            }
                        }

                        // If there is no active match, deck data should be reset to allow
                        // the player to use this between multiple matches.
                        if (!isMatchActive) {
                            playerDeck = null;
                            opponentDeck.clear();
                            playerCardIDs.clear();
                            opponentCardIDs.clear();
                        }

                        // Get the player deck if it does not yet exist and a match is active.
                        if (isMatchActive && playerDeck == null) {
                            url = "http://127.0.0.1:21337/static-decklist";
                            // Returns a string containing the player's active deck data in JSON format,
                            // or null if the game is not currently open. If the game is open, "DeckCode"
                            // and others will be null if there is no active match.
                            String staticDeckJSON = LoRRequest.get(url);
                            if (!(staticDeckJSON == null || new JSONObject(staticDeckJSON).get("DeckCode") == null)) {
                                String deckCode = new JSONObject(staticDeckJSON).getString("DeckCode");
                                playerDeck = LoRDeckEncoder.decode(deckCode);
                            }
                        }

                        if (isMatchActive) {
                            JSONArray rectJSONArray = new JSONObject(liveMatchJSON).getJSONArray("Rectangles");
                            for (Object rectObj : rectJSONArray) {
                                JSONObject rectJSONObj = (JSONObject) rectObj;
                                int cardID = rectJSONObj.getInt("CardID");
                                String cardCode = rectJSONObj.getString("CardCode");
                                boolean localPlayer = rectJSONObj.getBoolean("LocalPlayer");
                                // If this card has not yet shown up and belongs to the player,
                                // this means the player has drawn the card and so it should be
                                // removed from the deck.
                                if (localPlayer && !playerCardIDs.contains(cardID)) {
                                    if (!cardCode.equalsIgnoreCase("face")) {
                                        playerDeck.remove(cardCode);
                                        playerCardIDs.add(cardID);
                                    }
                                }
                                // If this card has not yet shown up and belongs to the opponent,
                                // this means that the opponent has played the card and so it should
                                // be added to the deck.
                                if (!localPlayer && !opponentCardIDs.contains(cardID)) {
                                    if (!cardCode.equalsIgnoreCase("face")) {
                                        opponentDeck.add(cardCode, 1);
                                        opponentCardIDs.add(cardID);
                                    }
                                }
                            }
                        }

                        final CountDownLatch latch = new CountDownLatch(1);
                        boolean finalIsMatchActive = isMatchActive;
                        Platform.runLater(() -> {
                            try {
                                playerDeckLabel.setVisible(finalIsMatchActive);
                                playerDeckListView.setVisible(finalIsMatchActive);
                                opponentDeckLabel.setVisible(finalIsMatchActive);
                                opponentDeckListView.setVisible(finalIsMatchActive);
                                matchNotFoundLabel1.setVisible(!finalIsMatchActive);
                                matchNotFoundLabel2.setVisible(!finalIsMatchActive);

                                if (finalIsMatchActive) {
                                    LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
                                    playerDeck.sort();
                                    playerDeckListView.getItems().clear();
                                    for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : playerDeck.getCards()) {
                                        LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
                                        playerDeckListView.getItems().add("×" + cardCodeAndCount.getCount() + " " + card.getName().toUpperCase());
                                    }
                                    opponentDeck.sort();
                                    opponentDeckListView.getItems().clear();
                                    for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : opponentDeck.getCards()) {
                                        LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
                                        opponentDeckListView.getItems().add("×" + cardCodeAndCount.getCount() + " " + card.getName().toUpperCase());
                                    }
                                }
                            }
                            finally {
                                latch.countDown();
                            }
                        });
                        latch.await();
                        return null;
                    }
                };
            }
        };
        updateUIService.start();
        updateUIService.setOnSucceeded(workerStateEvent -> updateUIService.restart());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerCardIDs = new ArrayList<>();
        opponentCardIDs = new ArrayList<>();
        opponentDeck = new LoRDeck();

        setupSceneButtons();
        // Constantly update the UI to keep up with changes to the live match endpoint.
        updateUI();
    }
}
