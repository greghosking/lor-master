package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRDeck;
import ghosking.lormaster.lor.LoRDeckEncoder;
import ghosking.lormaster.lor.LoRMatch;
import ghosking.lormaster.lor.LoRPlayer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MetaController implements Initializable {

    private static class DeckStats {
        private final String deckCode;
        private int matchesPlayed;
        private int matchesWon;

        public DeckStats(String deckCode) {
            this.deckCode = deckCode;
            this.matchesPlayed = 0;
            this.matchesWon = 0;
        }

        public String getDeckCode() {
            return deckCode;
        }

        public void incrementMatchesPlayed() {
            matchesPlayed++;
        }

        public void incrementMatchesWon() {
            matchesWon++;
        }

        public double getWinRate() {
            try {
                double rate = (double) matchesWon / matchesPlayed;
                return (double) Math.round(rate * 1000) / 10;
            }
            catch (Exception ex) {
                return 0;
            }
        }

        public double getPlayRate() {
            double rate = (matchesPlayed / 40.0);
            return (double) Math.round(rate * 1000) / 10;
        }
    }

    @FXML
    Button profileButton, liveMatchButton, collectionButton, decksButton, leaderboardButton;
    @FXML
    Button inspectDeckButton;
    @FXML
    ScrollPane metaScrollPane;
    @FXML
    GridPane metaGridPane;

    private static List<DeckStats> decksStats;
    private static int selectedDeckIndex = -1;

    private void setupSceneButtons() {
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
    }

    public void onInspectDeckButtonClicked() {
        // Show the user an alert with the deck code of the player for the selected match.
        ButtonType copyButtonType = new ButtonType("COPY TO CLIPBOARD", ButtonBar.ButtonData.YES);
        Alert inspectDeckAlert = new Alert(Alert.AlertType.NONE, "", copyButtonType);
        inspectDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
        inspectDeckAlert.setTitle("Inspect Deck");
        inspectDeckAlert.setHeaderText("SHARE THIS DECK CODE WITH OTHERS:");

        Label content = new Label(decksStats.get(selectedDeckIndex).getDeckCode().toUpperCase());
        content.setWrapText(true);
        inspectDeckAlert.getDialogPane().setContent(content);

        // If the user clicks the button, copy the deck code to the user's clipboard.
        inspectDeckAlert.showAndWait();
        if (inspectDeckAlert.getResult() == copyButtonType) {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(decksStats.get(selectedDeckIndex).getDeckCode().toUpperCase());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        }
    }

    private void setupMetaGridPane() {
        // NOTE: Due to a limited API rate, this method looks at a specific
        // pro player's match history rather than recursively gathering data from
        // hundreds of matches over time. Since the sample size is much smaller
        // than was originally intended, the data does not look right (for example:
        // if a deck only appears once, it may have a 0% or 100% win rate).
        List<LoRMatch> matchHistory = LoRPlayer.fromRiotID("MajiinBae", "NA1").getMatchHistory();
        decksStats = new ArrayList<>();
        for (LoRMatch match : matchHistory) {
            LoRMatch.LoRMatchParticipant player1 = match.getParticipants().get(0);
            // See if the deck that player1 used is already in the list.
            boolean deckFound = false;
            for (DeckStats decksStat : decksStats) {
                if (decksStat.getDeckCode().equalsIgnoreCase(player1.getDeckCode())) {
                    decksStat.incrementMatchesPlayed();
                    if (player1.wonMatch())
                        decksStat.incrementMatchesWon();
                    deckFound = true;
                    break;
                }
            }
            if (!deckFound) {
                DeckStats deckStats = new DeckStats(player1.getDeckCode());
                deckStats.incrementMatchesPlayed();
                if (player1.wonMatch())
                    deckStats.incrementMatchesWon();
                decksStats.add(deckStats);
            }
            LoRMatch.LoRMatchParticipant player2 = match.getParticipants().get(1);
            // See if the deck that player2 used is already in the list.
            deckFound = false;
            for (DeckStats decksStat : decksStats) {
                if (decksStat.getDeckCode().equalsIgnoreCase(player2.getDeckCode())) {
                    decksStat.incrementMatchesPlayed();
                    if (player2.wonMatch())
                        decksStat.incrementMatchesWon();
                    deckFound = true;
                    break;
                }
            }
            if (!deckFound) {
                DeckStats deckStats = new DeckStats(player2.getDeckCode());
                deckStats.incrementMatchesPlayed();
                if (player2.wonMatch())
                    deckStats.incrementMatchesWon();
                decksStats.add(deckStats);
            }
        }

        // Sort by win rate.
        for (int i = 0; i < decksStats.size() - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < decksStats.size(); j++) {
                if (decksStats.get(j).getWinRate() >= decksStats.get(maxIndex).getWinRate())
                    maxIndex = j;
            }
            DeckStats placeholder = decksStats.get(i);
            decksStats.set(i, decksStats.get(maxIndex));
            decksStats.set(maxIndex, placeholder);
        }

        int rows = decksStats.size();
        double rowH = 120;
        metaGridPane.setPrefHeight(Math.max(metaGridPane.getPrefHeight(), rows * rowH));
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowH);
            rowConst.setValignment(VPos.CENTER);
            metaGridPane.getRowConstraints().add(rowConst);
        }
        List<ColumnConstraints> columnConstraints = metaGridPane.getColumnConstraints();

        for (int i = 0; i < decksStats.size(); i++) {
            // First column indicates the ranking of the deck.
            Label rankLabel = new Label(i + ".");
            rankLabel.setPrefWidth(columnConstraints.get(0).getPrefWidth());
            rankLabel.setPrefHeight(rowH);
            rankLabel.setAlignment(Pos.CENTER);
            rankLabel.setTextAlignment(TextAlignment.CENTER);
            rankLabel.setContentDisplay(ContentDisplay.CENTER);
            rankLabel.getStyleClass().add("unselected-deck");
            metaGridPane.add(rankLabel, 0, i);

            // Second and third columns indicate the region(s) in the deck.
            List<Image> regionIcons = LoRMasterApplication.getRegionIcons();
            StackPane region1IconPane = new StackPane();
            region1IconPane.setPrefWidth(columnConstraints.get(1).getPrefWidth());
            region1IconPane.setPrefHeight(rowH);
            StackPane region2IconPane = new StackPane();
            region2IconPane.setPrefWidth(columnConstraints.get(2).getPrefWidth());
            region2IconPane.setPrefHeight(rowH);

            LoRDeck deck = LoRDeckEncoder.decode(decksStats.get(i).getDeckCode());
            if (deck.getRegions().size() == 2) {
                ImageView region1IconImageView = new ImageView(regionIcons.get(deck.getRegions().get(0).getID()));
                region1IconPane.getChildren().add(region1IconImageView);
                StackPane.setAlignment(region1IconImageView, Pos.CENTER);
                region1IconPane.getStyleClass().add("unselected-deck");
                ImageView region2IconImageView = new ImageView(regionIcons.get(deck.getRegions().get(1).getID()));
                region2IconPane.getChildren().add(region2IconImageView);
                StackPane.setAlignment(region2IconImageView, Pos.CENTER);
                region2IconPane.getStyleClass().add("unselected-deck");
            }
            if (deck.getRegions().size() == 1) {
                ImageView region2IconImageView = new ImageView(regionIcons.get(deck.getRegions().get(0).getID()));
                region2IconPane.getChildren().add(region2IconImageView);
                StackPane.setAlignment(region2IconImageView, Pos.CENTER);
                region2IconPane.getStyleClass().add("unselected-deck");
            }
            metaGridPane.add(region1IconPane, 1, i);
            metaGridPane.add(region2IconPane, 2, i);

            // Fourth column indicates the deck's region(s).
            Label regionsLabel;
            if (deck.getRegions().size() == 2) {
                regionsLabel = new Label("  " + deck.getRegions().get(0).getName().toUpperCase() + " / " +
                        deck.getRegions().get(1).getName().toUpperCase());
            }
            else {
                regionsLabel = new Label("  " + deck.getRegions().get(0).getName().toUpperCase());
            }
            regionsLabel.setPrefWidth(columnConstraints.get(3).getPrefWidth());
            regionsLabel.setPrefHeight(rowH);
            regionsLabel.getStyleClass().add("unselected-deck");
            metaGridPane.add(regionsLabel, 3, i);

            // Fifth column indicates the deck's win rate.
            Label winRateLabel = new Label(String.valueOf(decksStats.get(i).getWinRate()));
            winRateLabel.setPrefWidth(columnConstraints.get(4).getPrefWidth());
            winRateLabel.setPrefHeight(rowH);
            winRateLabel.setAlignment(Pos.CENTER);
            winRateLabel.setTextAlignment(TextAlignment.CENTER);
            winRateLabel.setContentDisplay(ContentDisplay.CENTER);
            winRateLabel.getStyleClass().add("unselected-deck");
            metaGridPane.add(winRateLabel, 4, i);

            // Sixth column indicates the deck's play rate.
            Label playRateLabel = new Label(String.valueOf(decksStats.get(i).getPlayRate()));
            playRateLabel.setPrefWidth(columnConstraints.get(5).getPrefWidth());
            playRateLabel.setPrefHeight(rowH);
            playRateLabel.setAlignment(Pos.CENTER);
            playRateLabel.setTextAlignment(TextAlignment.CENTER);
            playRateLabel.setContentDisplay(ContentDisplay.CENTER);
            playRateLabel.getStyleClass().add("unselected-deck");
            metaGridPane.add(playRateLabel, 5, i);

            for (int j = 0; j < metaGridPane.getChildren().size(); j++) {
                int nodeIndex = j;
                metaGridPane.getChildren().get(j).setOnMouseClicked(mouseEvent -> {
                    for (int k = 0; k < metaGridPane.getChildren().size(); k++) {
                        // Select the entire row if one of its elements is clicked.
                        selectedDeckIndex = GridPane.getRowIndex(metaGridPane.getChildren().get(nodeIndex));
                        inspectDeckButton.setDisable(selectedDeckIndex == -1);
                        if (GridPane.getRowIndex(metaGridPane.getChildren().get(k)) == selectedDeckIndex) {
                            metaGridPane.getChildren().get(k).getStyleClass().clear();
                            metaGridPane.getChildren().get(k).getStyleClass().add("selected-deck");
                        }
                        else {
                            metaGridPane.getChildren().get(k).getStyleClass().clear();
                            metaGridPane.getChildren().get(k).getStyleClass().add("unselected-deck");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSceneButtons();
        Platform.runLater(this::setupMetaGridPane);
    }
}
