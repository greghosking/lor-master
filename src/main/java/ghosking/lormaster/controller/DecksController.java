package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRDeck;
import ghosking.lormaster.lor.LoRDeckEncoder;
import io.github.pixee.security.BoundedLineReader;
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
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.util.*;

public class DecksController implements Initializable {

    @FXML
    Button profileButton, liveMatchButton, collectionButton, leaderboardButton, metaButton;
    @FXML
    Button newDeckButton, importDeckButton, exportDeckButton, editDeckButton, deleteDeckButton;
    @FXML
    ScrollPane decksScrollPane;
    @FXML
    GridPane decksGridPane;

    private static List<LoRDeck> decks;
    private static int selectedDeckIndex = -1;

    public static void overwriteSelectedDeck(LoRDeck deck) {
        decks.set(selectedDeckIndex, deck);
    }

    private void setupSceneButtons() {
        profileButton.setOnMouseClicked(mouseEvent -> {
            LoRMasterApplication.switchToProfileScene();
            writeDecks();
        });
        liveMatchButton.setOnMouseClicked(mouseEvent -> {
            LoRMasterApplication.switchToLiveMatchScene();
            writeDecks();
        });
        collectionButton.setOnMouseClicked(mouseEvent -> {
            LoRMasterApplication.switchToCollectionScene();
            writeDecks();
        });
        leaderboardButton.setOnMouseClicked(mouseEvent -> {
            LoRMasterApplication.switchToLeaderboardScene();
            writeDecks();
        });
        metaButton.setOnMouseClicked(mouseEvent -> {
            LoRMasterApplication.switchToMetaScene();
            writeDecks();
        });
    }

    public void onNewDeckButtonClicked() {
        selectedDeckIndex = decksGridPane.getRowCount();
        decks.add(selectedDeckIndex, new LoRDeck());
        LoRMasterApplication.switchToDeckEditorScene(decks.get(selectedDeckIndex));
    }

    public void onImportDeckButtonClicked() {
        // Try to read a deck code from the user's clipboard.
        try {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String deckCode = clipboard.getString().toUpperCase();
            LoRDeck deck = LoRDeckEncoder.decode(deckCode);
            selectedDeckIndex = decksGridPane.getRowCount();
            decks.add(selectedDeckIndex, deck);
            LoRMasterApplication.switchToDeckEditorScene(decks.get(selectedDeckIndex));
        }
        // If there is no valid deck code on the user's clipboard, alert the user.
        catch (Exception ex) {
            Alert importDeckAlert = new Alert(Alert.AlertType.NONE, "", ButtonType.OK);
            importDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
            importDeckAlert.setTitle("Import Deck");
            importDeckAlert.setHeaderText("COULD NOT IMPORT DECK!");
            Label content = new Label("Please make sure you have copied a valid deck code\n" +
                    "on your clipboard and try again.\n");
            content.setWrapText(true);
            importDeckAlert.getDialogPane().setContent(content);

            importDeckAlert.showAndWait();
        }
    }

    public void onExportDeckButtonClicked() {
        // Show the user an alert with the code for the selected deck.
        ButtonType copyButtonType = new ButtonType("COPY TO CLIPBOARD", ButtonBar.ButtonData.YES);
        Alert exportDeckAlert = new Alert(Alert.AlertType.NONE, "", copyButtonType);
        exportDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
        exportDeckAlert.setTitle("Export Deck");
        exportDeckAlert.setHeaderText("SHARE THIS DECK CODE WITH OTHERS:");
        Label content = new Label(LoRDeckEncoder.encode(decks.get(selectedDeckIndex)));
        content.setWrapText(true);
        exportDeckAlert.getDialogPane().setContent(content);

        // If the user clicks the button, copy the deck code to the user's clipboard.
        exportDeckAlert.showAndWait();
        if (exportDeckAlert.getResult() == copyButtonType) {
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(LoRDeckEncoder.encode(decks.get(selectedDeckIndex)));
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(clipboardContent);
        }
    }

    public void onEditDeckButtonClicked() {
        LoRMasterApplication.switchToDeckEditorScene(decks.get(selectedDeckIndex));
    }

    public void onDeleteDeckButtonClicked() {
        // Show the user an alert to confirm whether they want to delete the selected deck.
        ButtonType yesButtonType = new ButtonType("YES", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("NO", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.NONE, "", yesButtonType, noButtonType);
        alert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());
        alert.setTitle("Delete Deck?");
        alert.setHeaderText("DELETE DECK?");
        Label content = new Label("Are you sure you want to delete this deck?\n");
        content.setWrapText(true);
        alert.getDialogPane().setContent(content);

        // If the user chooses to delete the deck, remove it from the list and
        // reset the selected deck index.
        alert.showAndWait();
        if (alert.getResult() == yesButtonType) {
            decks.remove(selectedDeckIndex);
            selectedDeckIndex = -1;
            writeDecks();
            updateDecksGridPane();
        }
    }

    private void updateDecksGridPane() {
        readDecks();
        decksGridPane.getChildren().removeAll(decksGridPane.getChildren());
        while (decksGridPane.getRowConstraints().size() > 0)
            decksGridPane.getRowConstraints().remove(0);
        int rows = decks.size();
        double rowH = 120;
        decksGridPane.setPrefHeight(Math.max(decksGridPane.getPrefHeight(), rows * rowH));
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowH);
            rowConst.setValignment(VPos.CENTER);
            decksGridPane.getRowConstraints().add(rowConst);
        }

        List<Image> regionIcons = LoRMasterApplication.getRegionIcons();
        decksGridPane.getChildren().clear();
        for (int i = 0; i < decks.size(); i++) {
            StackPane region1IconPane = new StackPane();
            region1IconPane.setPrefWidth(decksGridPane.getColumnConstraints().get(0).getPrefWidth());
            region1IconPane.setPrefHeight(decksGridPane.getRowConstraints().get(0).getPrefHeight());
            StackPane region2IconPane = new StackPane();
            region2IconPane.setPrefWidth(decksGridPane.getColumnConstraints().get(0).getPrefWidth());
            region2IconPane.setPrefHeight(decksGridPane.getRowConstraints().get(0).getPrefHeight());

            if (decks.get(i).getRegions().size() == 2) {
                ImageView region1IconImageView = new ImageView(regionIcons.get(decks.get(i).getRegions().get(0).getID()));
                region1IconPane.getChildren().add(region1IconImageView);
                StackPane.setAlignment(region1IconImageView, Pos.CENTER);
                region1IconPane.getStyleClass().add("unselected-deck");
                ImageView region2IconImageView = new ImageView(regionIcons.get(decks.get(i).getRegions().get(1).getID()));
                region2IconPane.getChildren().add(region2IconImageView);
                StackPane.setAlignment(region2IconImageView, Pos.CENTER);
                region2IconPane.getStyleClass().add("unselected-deck");
            }
            if (decks.get(i).getRegions().size() == 1) {
                ImageView region2IconImageView = new ImageView(regionIcons.get(decks.get(i).getRegions().get(0).getID()));
                region2IconPane.getChildren().add(region2IconImageView);
                StackPane.setAlignment(region2IconImageView, Pos.CENTER);
                region2IconPane.getStyleClass().add("unselected-deck");
            }
            decksGridPane.add(region1IconPane, 0, i);
            decksGridPane.add(region2IconPane, 1, i);

            Label label = new Label("  " + decks.get(i).getName().toUpperCase());
            label.setPrefWidth(decksGridPane.getColumnConstraints().get(2).getPrefWidth());
            label.setPrefHeight(decksGridPane.getRowConstraints().get(0).getPrefHeight());
            label.getStyleClass().add("unselected-deck");
            decksGridPane.add(label, 2, i);

            for (int j = 0; j < decksGridPane.getChildren().size(); j++) {
                int nodeIndex = j;
                decksGridPane.getChildren().get(j).setOnMouseClicked(mouseEvent -> {
                    for (int k = 0; k < decksGridPane.getChildren().size(); k++) {
                        // Select the entire row if one of its elements is clicked.
                        selectedDeckIndex = GridPane.getRowIndex(decksGridPane.getChildren().get(nodeIndex));
                        exportDeckButton.setDisable(selectedDeckIndex == -1);
                        editDeckButton.setDisable(selectedDeckIndex == -1);
                        deleteDeckButton.setDisable(selectedDeckIndex == -1);
                        if (GridPane.getRowIndex(decksGridPane.getChildren().get(k)) == selectedDeckIndex) {
                            decksGridPane.getChildren().get(k).getStyleClass().clear();
                            decksGridPane.getChildren().get(k).getStyleClass().add("selected-deck");
                        }
                        else {
                            decksGridPane.getChildren().get(k).getStyleClass().clear();
                            decksGridPane.getChildren().get(k).getStyleClass().add("unselected-deck");
                        }
                    }
                });
            }
        }
    }

    public static void readDecks() {
        // Try to read in any existing user decks.
        decks = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("USER_DECK_CODES.txt"));
            String deckName, deckCode;
            while ((deckName = BoundedLineReader.readLine(br, 5_000_000)) != null && (deckCode = BoundedLineReader.readLine(br, 5_000_000)) != null) {
                LoRDeck deck = LoRDeckEncoder.decode(deckCode);
                deck.setName(deckName);
                decks.add(deck);
            }
            br.close();
        }
        // If the file could not be found, create a few starter decks for the user.
        catch (IOException ex) {
            LoRDeck starterDeck1 = LoRDeckEncoder.decode("CEAAEBYBAEDRMGREFYZDKCABAABQMCYSCQNB2JYCAQAQABYMFIWAMAIBBEKCAIRHFE");
            starterDeck1.setName("BUFF AND TUFF");
            decks.add(starterDeck1);
            LoRDeck starterDeck2 = LoRDeckEncoder.decode("CEAAECABAIDASDAUDIOC2OIJAECBEEY3DQTSQNBXHMBAEAIEBUWAIAICC4MB4KY");
            starterDeck2.setName("SPELLS AND STEALTH");
            decks.add(starterDeck2);
            LoRDeck starterDeck3 = LoRDeckEncoder.decode("CEAAECABAMGA6EYXEYVS4NYIAECQCGY5FAVTCMRVAICACAYCBELDGBABAURCMKJW");
            starterDeck3.setName("DEATH AND SPIDERS");
            decks.add(starterDeck3);
        }
    }

    public static void writeDecks() {
        // Save user decks by writing deck codes to a file.
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("USER_DECK_CODES.txt"));

            for (LoRDeck deck : decks) {
                bw.write(deck.getName());
                bw.newLine();
                bw.write(LoRDeckEncoder.encode(deck));
                bw.newLine();
            }

            bw.close();
        }
        // If the file could not be opened, something went seriously wrong...
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSceneButtons();
        Platform.runLater(this::updateDecksGridPane);

        // If the user attempts to exit the application, make sure to save their decks.
        LoRMasterApplication.getStage().setOnCloseRequest(windowEvent -> writeDecks());
    }
}
