package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRDeck;
import ghosking.lormaster.lor.LoRDeckEncoder;
import ghosking.lormaster.lor.LoRMatch;
import ghosking.lormaster.lor.LoRPlayer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProfileController implements Initializable {

    @FXML
    Button liveMatchButton, collectionButton, decksButton, leaderboardButton, metaButton;
    @FXML
    Label usernameLabel;
    @FXML
    Button inspectPlayerDeckButton, inspectOpponentDeckButton;
    @FXML
    ScrollPane matchHistoryScrollPane;
    @FXML
    GridPane matchHistoryGridPane;

    private static List<LoRMatch> matchHistory;
    private static int selectedMatchIndex = -1;

    private void setupSceneButtons() {
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

    private void setupUsernameLabel() {
        usernameLabel.setText("WELCOME " + LoRMasterApplication.getUser().getGameName().toUpperCase() + "!");
    }

    public void onInspectPlayerDeckButtonClicked() {
        // Show the user an alert with the deck code of the player for the selected match.
        ButtonType copyButtonType = new ButtonType("COPY TO CLIPBOARD", ButtonBar.ButtonData.YES);
        Alert inspectDeckAlert = new Alert(Alert.AlertType.NONE, "", copyButtonType);
        inspectDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
        inspectDeckAlert.setTitle("Inspect Deck");
        inspectDeckAlert.setHeaderText("SHARE THIS DECK CODE WITH OTHERS:");

        List<LoRMatch.LoRMatchParticipant> participants = matchHistory.get(selectedMatchIndex).getParticipants();
        String playerDeckCode;
        if (participants.get(0).getPUUID().equalsIgnoreCase(LoRMasterApplication.getUser().getPUUID()))
            playerDeckCode = participants.get(0).getDeckCode().toUpperCase();
        else
            playerDeckCode = participants.get(1).getDeckCode().toUpperCase();
        Label content = new Label(playerDeckCode);
        content.setWrapText(true);
        inspectDeckAlert.getDialogPane().setContent(content);

        // If the user clicks the button, copy the deck code to the user's clipboard.
        inspectDeckAlert.showAndWait();
        if (inspectDeckAlert.getResult() == copyButtonType) {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(playerDeckCode);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        }
    }

    public void onInspectOpponentDeckButtonClicked() {
        // Show the user an alert with the deck code of the opponent for the selected match.
        ButtonType copyButtonType = new ButtonType("COPY TO CLIPBOARD", ButtonBar.ButtonData.YES);
        Alert inspectDeckAlert = new Alert(Alert.AlertType.NONE, "", copyButtonType);
        inspectDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
        inspectDeckAlert.setTitle("Inspect Deck");
        inspectDeckAlert.setHeaderText("SHARE THIS DECK CODE WITH OTHERS:");

        List<LoRMatch.LoRMatchParticipant> participants = matchHistory.get(selectedMatchIndex).getParticipants();
        String opponentDeckCode;
        if (!participants.get(0).getPUUID().equalsIgnoreCase(LoRMasterApplication.getUser().getPUUID()))
            opponentDeckCode = participants.get(0).getDeckCode().toUpperCase();
        else
            opponentDeckCode = participants.get(1).getDeckCode().toUpperCase();
        Label content = new Label(opponentDeckCode);
        content.setWrapText(true);
        inspectDeckAlert.getDialogPane().setContent(content);

        // If the user clicks the button, copy the deck code to the user's clipboard.
        inspectDeckAlert.showAndWait();
        if (inspectDeckAlert.getResult() == copyButtonType) {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(opponentDeckCode);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        }
    }

    private void setupMatchHistoryGridPane() {
        List<Image> regionIcons = LoRMasterApplication.getRegionIcons();
        LoRPlayer user = LoRMasterApplication.getUser();
        matchHistory = user.getMatchHistory();
        matchHistory.removeIf(match -> match.getParticipants().size() < 2);
        int rows = matchHistory.size();
        double rowH = 120;
        matchHistoryGridPane.setPrefHeight(Math.max(matchHistoryGridPane.getPrefHeight(), rows * rowH));
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowH);
            rowConst.setValignment(VPos.CENTER);
            matchHistoryGridPane.getRowConstraints().add(rowConst);
        }
        List<ColumnConstraints> columnConstraints = matchHistoryGridPane.getColumnConstraints();

        for (int i = 0; i < matchHistory.size(); i++) {
            LoRMatch match = matchHistory.get(i);
            List<LoRMatch.LoRMatchParticipant> participants = match.getParticipants();
            LoRMatch.LoRMatchParticipant player, opponent;
            LoRDeck playerDeck, opponentDeck;

            // The match participants are not guaranteed to be in order.
            if (participants.get(0).getPUUID().equalsIgnoreCase(user.getPUUID())) {
                player = participants.get(0);
                opponent = participants.get(1);
            }
            else {
                player = participants.get(1);
                opponent = participants.get(0);
            }
            playerDeck = LoRDeckEncoder.decode(player.getDeckCode());
            opponentDeck = LoRDeckEncoder.decode(opponent.getDeckCode());

            // First column indicates defeat or victory.
            Label outcomeLabel;
            if (player.wonMatch()) {
                outcomeLabel = new Label("   VICTORY");
                outcomeLabel.setStyle("-fx-text-fill: #C9A15C");
            }
            else {
                outcomeLabel = new Label("   DEFEAT");
                outcomeLabel.setStyle("-fx-text-fill: #FF4E50");
            }
            outcomeLabel.setPrefWidth(columnConstraints.get(0).getPrefWidth());
            outcomeLabel.setPrefHeight(rowH);
            outcomeLabel.getStyleClass().add("unselected-deck");
            matchHistoryGridPane.add(outcomeLabel, 0, i);

            // Second and third columns indicate the region(s) in the player's deck.
            StackPane playerRegion1IconPane = new StackPane();
            playerRegion1IconPane.setPrefWidth(columnConstraints.get(1).getPrefWidth());
            playerRegion1IconPane.setPrefHeight(rowH);
            StackPane playerRegion2IconPane = new StackPane();
            playerRegion2IconPane.setPrefWidth(columnConstraints.get(2).getPrefWidth());
            playerRegion2IconPane.setPrefHeight(rowH);

            if (playerDeck.getRegions().size() == 2) {
                ImageView icon1ImageView = new ImageView(regionIcons.get(playerDeck.getRegions().get(0).getID()));
                playerRegion1IconPane.getChildren().add(icon1ImageView);
                StackPane.setAlignment(icon1ImageView, Pos.CENTER);
                playerRegion1IconPane.getStyleClass().add("unselected-deck");
                ImageView icon2ImageView = new ImageView(regionIcons.get(playerDeck.getRegions().get(1).getID()));
                playerRegion2IconPane.getChildren().add(icon2ImageView);
                StackPane.setAlignment(icon2ImageView, Pos.CENTER);
                playerRegion2IconPane.getStyleClass().add("unselected-deck");
            }
            if (playerDeck.getRegions().size() == 1) {
                ImageView icon2ImageView = new ImageView(regionIcons.get(playerDeck.getRegions().get(0).getID()));
                playerRegion2IconPane.getChildren().add(icon2ImageView);
                StackPane.setAlignment(icon2ImageView, Pos.CENTER);
                playerRegion2IconPane.getStyleClass().add("unselected-deck");
            }
            matchHistoryGridPane.add(playerRegion1IconPane, 1, i);
            matchHistoryGridPane.add(playerRegion2IconPane, 2, i);

            // Fourth column indicates the player's name.
            Label playerLabel = new Label("  " + user.getGameName().toUpperCase());
            playerLabel.setPrefWidth(columnConstraints.get(3).getPrefWidth());
            playerLabel.setPrefHeight(rowH);
            playerLabel.getStyleClass().add("unselected-deck");
            matchHistoryGridPane.add(playerLabel, 3, i);

            // Fifth column simply says "VS."
            Label vsLabel = new Label("VS.");
            vsLabel.setPrefWidth(columnConstraints.get(4).getPrefWidth());
            vsLabel.setPrefHeight(rowH);
            vsLabel.setTextAlignment(TextAlignment.CENTER);
            vsLabel.getStyleClass().add("unselected-deck");
            matchHistoryGridPane.add(vsLabel, 4, i);

            // Sixth and seventh columns indicate the region(s) in the opponent's deck.
            StackPane opponentRegion1IconPane = new StackPane();
            opponentRegion1IconPane.setPrefWidth(columnConstraints.get(5).getPrefWidth());
            opponentRegion1IconPane.setPrefHeight(rowH);
            StackPane opponentRegion2IconPane = new StackPane();
            opponentRegion2IconPane.setPrefWidth(columnConstraints.get(6).getPrefWidth());
            opponentRegion2IconPane.setPrefHeight(rowH);

            if (opponentDeck.getRegions().size() == 2) {
                ImageView icon1ImageView = new ImageView(regionIcons.get(opponentDeck.getRegions().get(0).getID()));
                opponentRegion1IconPane.getChildren().add(icon1ImageView);
                StackPane.setAlignment(icon1ImageView, Pos.CENTER);
                opponentRegion1IconPane.getStyleClass().add("unselected-deck");
                ImageView icon2ImageView = new ImageView(regionIcons.get(opponentDeck.getRegions().get(1).getID()));
                opponentRegion2IconPane.getChildren().add(icon2ImageView);
                StackPane.setAlignment(icon2ImageView, Pos.CENTER);
                opponentRegion2IconPane.getStyleClass().add("unselected-deck");
            }
            if (opponentDeck.getRegions().size() == 1) {
                ImageView icon2ImageView = new ImageView(regionIcons.get(opponentDeck.getRegions().get(0).getID()));
                opponentRegion2IconPane.getChildren().add(icon2ImageView);
                StackPane.setAlignment(icon2ImageView, Pos.CENTER);
                opponentRegion2IconPane.getStyleClass().add("unselected-deck");
            }
            matchHistoryGridPane.add(opponentRegion1IconPane, 5, i);
            matchHistoryGridPane.add(opponentRegion2IconPane, 6, i);

            // Eighth column indicates the opponent's name.
            Label opponentLabel = new Label("  " + LoRPlayer.fromPUUID(opponent.getPUUID()).getGameName().toUpperCase());
            opponentLabel.setPrefWidth(columnConstraints.get(7).getPrefWidth());
            opponentLabel.setPrefHeight(rowH);
            opponentLabel.getStyleClass().add("unselected-deck");
            matchHistoryGridPane.add(opponentLabel, 7, i);

            for (int j = 0; j < matchHistoryGridPane.getChildren().size(); j++) {
                int nodeIndex = j;
                matchHistoryGridPane.getChildren().get(j).setOnMouseClicked(mouseEvent -> {
                    for (int k = 0; k < matchHistoryGridPane.getChildren().size(); k++) {
                        // Select the entire row if one of its elements is clicked.
                        selectedMatchIndex = GridPane.getRowIndex(matchHistoryGridPane.getChildren().get(nodeIndex));
                        inspectPlayerDeckButton.setDisable(selectedMatchIndex == -1);
                        inspectOpponentDeckButton.setDisable(selectedMatchIndex == -1);
                        if (GridPane.getRowIndex(matchHistoryGridPane.getChildren().get(k)) == selectedMatchIndex) {
                            matchHistoryGridPane.getChildren().get(k).getStyleClass().clear();
                            matchHistoryGridPane.getChildren().get(k).getStyleClass().add("selected-deck");
                        }
                        else {
                            matchHistoryGridPane.getChildren().get(k).getStyleClass().clear();
                            matchHistoryGridPane.getChildren().get(k).getStyleClass().add("unselected-deck");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSceneButtons();
        setupUsernameLabel();
        Platform.runLater(this::setupMatchHistoryGridPane);
    }
}
