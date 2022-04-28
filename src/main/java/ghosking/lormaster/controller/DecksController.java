package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRDeck;
import ghosking.lormaster.lor.LoRDeckEncoder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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
    List<Image> regionIcons;

    public static void overwriteSelectedDeck(LoRDeck deck) {
        decks.set(selectedDeckIndex, deck);
    }

    private void setupSceneButtons() {
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

    public void onNewDeckButtonClicked() {
        selectedDeckIndex = decksGridPane.getRowCount();
        decks.add(selectedDeckIndex, new LoRDeck());
        LoRMasterApplication.switchToDeckEditorScene(decks.get(selectedDeckIndex));
    }

    public void onImportDeckButtonClicked() {

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.showAndWait();

    }

    public void onExportDeckButtonClicked() {

    }

    public void onEditDeckButtonClicked() {
        LoRMasterApplication.switchToDeckEditorScene(decks.get(selectedDeckIndex));
    }

    public void onDeleteDeckButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.NONE, "Are you sure you want to delete this deck?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Delete Deck?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            decks.remove(selectedDeckIndex);
            selectedDeckIndex = -1;
            updateDecksGridPane();
        }
    }

    private void updateDecksGridPane() {
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
                        } else {
                            decksGridPane.getChildren().get(k).getStyleClass().clear();
                            decksGridPane.getChildren().get(k).getStyleClass().add("unselected-deck");
                        }
                    }
                });
            }
        }
    }

    private void setupDecksGridPane() {
        regionIcons = new ArrayList<>();
        String baseURL = "https://dd.b.pvp.net/3_4_0/core/en_us/img/regions/icon-";
        List<String> iconFilenames = Arrays.asList("demacia.png", "freljord.png", "ionia.png", "noxus.png", "piltoverzaun.png",
                "shadowisles.png", "bilgewater.png", "shurima.png", "all.png", "targon.png", "bandlecity.png");
        for (String iconFilename : iconFilenames) {
            regionIcons.add(new Image(baseURL + iconFilename, 80 / 1.5, 120 / 1.75, false, true, false));
        }

        readDecks();
        int rows = decks.size();
        double rowH = 120;
        decksGridPane.setPrefHeight(Math.max(decksGridPane.getPrefHeight(), rows * rowH));
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowH);
            rowConst.setValignment(VPos.CENTER);
            decksGridPane.getRowConstraints().add(rowConst);
        }

        updateDecksGridPane();
    }

    public static void readDecks() {
        // Try to read in any existing user decks.
        decks = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("USER_DECK_CODES.txt"));
            String deckName, deckCode;
            while ((deckName = br.readLine()) != null && (deckCode = br.readLine()) != null) {
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
        readDecks();

        setupDecksGridPane();



        System.out.println(decks);



        LoRMasterApplication.getStage().setOnCloseRequest(windowEvent -> writeDecks());
    }

}
