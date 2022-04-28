package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class DeckEditorController implements Initializable {

    @FXML
    TextField deckNameTextField;
    @FXML
    ListView<String> deckListView;
    @FXML
    Label cardCountLabel, championCountLabel;
    @FXML
    Button doneButton;
    @FXML
    ToggleButton DEToggleButton, FRToggleButton, IOToggleButton, NXToggleButton, PZToggleButton, SIToggleButton, BWToggleButton, MTToggleButton, SHToggleButton, BCToggleButton, allToggleButton;
    @FXML
    ScrollPane cardsScrollPane;
    @FXML
    GridPane cardsGridPane;
    @FXML
    TextField searchTextField;
    @FXML
    ToggleButton unitToggleButton, spellToggleButton, landmarkToggleButton;
    @FXML
    ToggleButton commonToggleButton, rareToggleButton, epicToggleButton, championToggleButton;
    @FXML
    ToggleButton zeroCostToggleButton, oneCostToggleButton, twoCostToggleButton, threeCostToggleButton, fourCostToggleButton, fiveCostToggleButton, sixCostToggleButton, sevenCostToggleButton, eightPlusCostToggleButton;
    @FXML
    Button resetButton;

    private static List<ToggleButton> regionToggleButtons;
    private static List<ToggleButton> typeToggleButtons;
    private static List<ToggleButton> rarityToggleButtons;
    private static List<ToggleButton> costToggleButtons;
    Image placeholderCardImage;

    private static LoRDeck uneditedDeck;
    private static LoRDeck editedDeck;

    public static void setUneditedDeck(LoRDeck deck) {
        uneditedDeck = deck;
        editedDeck = new LoRDeck(uneditedDeck);
    }

    private void setupDeckNameTextField() {
        if (uneditedDeck.getName() == null) deckNameTextField.setText("NEW DECK");
        else deckNameTextField.setText(uneditedDeck.getName());
    }

    private void updateDeckListView() {
        editedDeck.sort();
        deckListView.getItems().clear();
        for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : editedDeck.getCards()) {
            LoRCardDatabase.LoRCard card = LoRCardDatabase.getInstance().getCard(cardCodeAndCount.getCardCode());
            deckListView.getItems().add("×" + cardCodeAndCount.getCount() + " " + card.getName().toUpperCase());
        }
    }

    private void setupDeckListView() {
        deckListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                int selectedIndex = deckListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex > -1) {
                    editedDeck.remove(editedDeck.getCards().get(selectedIndex).getCardCode());
                    updateDeckListView();
                    updateCountLabels();
                    updateCardsGridPane(false);
                }
            }
        });
        updateDeckListView();
    }

    private void updateCountLabels() {
        cardCountLabel.setText("CARDS: " + editedDeck.getSize() + "/40");
        championCountLabel.setText("CHAMPS: " + editedDeck.getNumChampions() + "/6");
    }

    public void onDoneButtonClicked() {
        // If the user attempts to exit the editor with an invalid deck, prompt
        // the user to save or discard changes or return to the editor.
        if (editedDeck.getSize() < 40) {
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.YES);
            ButtonType discardButtonType = new ButtonType("Discard", ButtonBar.ButtonData.NO);
            Alert invalidDeckAlert = new Alert(Alert.AlertType.NONE, "Do you want to save your changes anyway?",
                    saveButtonType, discardButtonType, ButtonType.CANCEL);
            invalidDeckAlert.setTitle("Invalid Deck");
            invalidDeckAlert.setHeaderText("Your deck does not have enough cards!");
            invalidDeckAlert.getDialogPane().getStylesheets().add(LoRMasterApplication.class.getResource("css/main.css").toExternalForm());

            invalidDeckAlert.showAndWait();
            ButtonType result = invalidDeckAlert.getResult();
            if (result == saveButtonType) {
                editedDeck.setName(deckNameTextField.getText());
                DecksController.overwriteSelectedDeck(editedDeck);
                DecksController.writeDecks();
                LoRMasterApplication.switchToDecksScene();
            } else if (result == discardButtonType)
                LoRMasterApplication.switchToDecksScene();
        }
        // If the user attempts to exit the editor with a valid deck, save the
        // changes and return to the decks scene.
        else {
            editedDeck.setName(deckNameTextField.getText());
            DecksController.overwriteSelectedDeck(editedDeck);
            DecksController.writeDecks();
            LoRMasterApplication.switchToDecksScene();
        }
    }

    public void onRegionToggleButtonClicked() {
        allToggleButton.setSelected(true);
        for (int i = 0; i < regionToggleButtons.size() - 1; i++)
            if (!regionToggleButtons.get(i).isSelected()) allToggleButton.setSelected(false);
        updateCardsGridPane();
    }

    public void onAllRegionToggleButtonClicked() {
        for (int i = 0; i < regionToggleButtons.size() - 1; i++)
            regionToggleButtons.get(i).setSelected(allToggleButton.isSelected());
        updateCardsGridPane();
    }

    private void setupRegionToggleButtons() {
        String baseURL = "https://dd.b.pvp.net/3_4_0/core/en_us/img/regions/icon-";
        List<String> iconFilenames = Arrays.asList("demacia.png", "freljord.png", "ionia.png", "noxus.png", "piltoverzaun.png",
                "shadowisles.png", "bilgewater.png", "targon.png", "shurima.png", "bandlecity.png", "all.png");
        for (int i = 0; i < regionToggleButtons.size(); i++) {
            regionToggleButtons.get(i).setGraphic(new ImageView(new Image(baseURL + iconFilenames.get(i), regionToggleButtons.get(i).getPrefWidth() / 1.5,
                    regionToggleButtons.get(i).getPrefHeight() / 1.75, false, true, false)));
        }
    }

    private void showCardFullAsset(String cardCode) {
        // Create a new stage with a black background.
        StackPane stack = new StackPane();
        stack.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), Insets.EMPTY)));
        Scene scene = new Scene(stack, 1280, 720);

        // The expanded art for spells is circular and should have equal dimensions.
        boolean isSpell = LoRCardDatabase.getInstance().getCard(cardCode).getType() == LoRType.SPELL;
        double imageW = isSpell ? (720 / 1.25) : 1280;
        double imageH = isSpell ? (720 / 1.25) : 720;

        ImageView fullImageView = new ImageView(LoRCardDatabase.getInstance().getCard(cardCode).getFullAsset());
        fullImageView.setPreserveRatio(false);
        fullImageView.setFitWidth(imageW);
        fullImageView.setFitHeight(imageH);

        // Add the image to the scene and show the stage.
        stack.getChildren().add(fullImageView);
        StackPane.setAlignment(fullImageView, Pos.CENTER);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private List<LoRRegion> getSelectedRegions() {
        List<LoRRegion> regions = new ArrayList<>();
        if (DEToggleButton.isSelected()) regions.add(LoRRegion.fromCode("DE"));
        if (FRToggleButton.isSelected()) regions.add(LoRRegion.fromCode("FR"));
        if (IOToggleButton.isSelected()) regions.add(LoRRegion.fromCode("IO"));
        if (NXToggleButton.isSelected()) regions.add(LoRRegion.fromCode("NX"));
        if (PZToggleButton.isSelected()) regions.add(LoRRegion.fromCode("PZ"));
        if (SIToggleButton.isSelected()) regions.add(LoRRegion.fromCode("SI"));
        if (BWToggleButton.isSelected()) regions.add(LoRRegion.fromCode("BW"));
        if (MTToggleButton.isSelected()) regions.add(LoRRegion.fromCode("MT"));
        if (SHToggleButton.isSelected()) regions.add(LoRRegion.fromCode("SH"));
        if (BCToggleButton.isSelected()) regions.add(LoRRegion.fromCode("BC"));
        return regions;
    }

    private List<LoRType> getSelectedTypes() {
        List<LoRType> types = new ArrayList<>();
        if (unitToggleButton.isSelected()) types.add(LoRType.UNIT);
        if (spellToggleButton.isSelected()) types.add(LoRType.SPELL);
        if (landmarkToggleButton.isSelected()) types.add(LoRType.LANDMARK);
        return types;
    }

    private List<LoRRarity> getSelectedRarities() {
        List<LoRRarity> rarities = new ArrayList<>();
        if (commonToggleButton.isSelected()) rarities.add(LoRRarity.COMMON);
        if (rareToggleButton.isSelected()) rarities.add(LoRRarity.RARE);
        if (epicToggleButton.isSelected()) rarities.add(LoRRarity.EPIC);
        if (championToggleButton.isSelected()) rarities.add(LoRRarity.CHAMPION);
        return rarities;
    }

    private List<Integer> getSelectedCosts() {
        List<Integer> costs = new ArrayList<>();
        if (zeroCostToggleButton.isSelected()) costs.add(0);
        if (oneCostToggleButton.isSelected()) costs.add(1);
        if (twoCostToggleButton.isSelected()) costs.add(2);
        if (threeCostToggleButton.isSelected()) costs.add(3);
        if (fourCostToggleButton.isSelected()) costs.add(4);
        if (fiveCostToggleButton.isSelected()) costs.add(5);
        if (sixCostToggleButton.isSelected()) costs.add(6);
        if (sevenCostToggleButton.isSelected()) costs.add(7);
        if (eightPlusCostToggleButton.isSelected()) costs.addAll(Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15));
        return costs;
    }

    public void updateCardsGridPane() {
        updateCardsGridPane(true);
    }

    public void updateCardsGridPane(boolean doScrollToTop) {
        // Filter the cards by the user selections.
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        LoRCardDatabase.LoRCardFilter filter = new LoRCardDatabase.LoRCardFilter()
                .byRegion(getSelectedRegions())
                .byType(getSelectedTypes())
                .byRarity(getSelectedRarities())
                .byCost(getSelectedCosts())
                .byCollectible(true);
        // If the search bar is not empty, search for the user query, then sort
        // and store the results of the filter in a new list.
        if (!searchTextField.getText().equalsIgnoreCase("")) filter.search(searchTextField.getText());
        List<String> cardCodes = filter.sort().getCardCodes();

        cardsGridPane.getChildren().removeAll(cardsGridPane.getChildren());
        while (cardsGridPane.getRowConstraints().size() > 0)
            cardsGridPane.getRowConstraints().remove(0);

        // Calculate the number of rows necessary to display all cards and resize
        // the cardsGridPane accordingly.
        int rows = (int) Math.ceil(cardCodes.size() / 4.0);
        double cardH = 1024 / 3.5;
        cardsGridPane.setPrefHeight(Math.max(cardsScrollPane.getPrefHeight(), rows * cardH));
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(cardH);
            rowConst.setValignment(VPos.CENTER);
            cardsGridPane.getRowConstraints().add(rowConst);
        }

        // Add the cards to the grid.
        int cardIndex = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cardsGridPane.getColumnCount(); j++) {
                if (cardIndex >= cardCodes.size())
                    break;
                final int finalCardIndex = cardIndex;
                final LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodes.get(cardIndex));
                ImageView cardImageView = new ImageView();
                // If the game asset has not finished loading, a placeholder asset
                // should be shown instead.
                Image cardImage = card.getGameAsset();
                if (cardImage.getProgress() == 1.0)
                    cardImageView.setImage(cardImage);
                else
                    cardImageView.setImage(placeholderCardImage);
                cardImage.progressProperty().addListener((observableValue, oldValue, progress) -> {
                    if ((double) progress == 1.0 && !cardImage.isError())
                        cardImageView.setImage(cardImage);
                    else
                        cardImageView.setImage(placeholderCardImage);
                });

                // Try adding then removing the card to trigger any exceptions.
                try {
                    editedDeck.add(card.getCode());
                    editedDeck.remove(card.getCode());
                } catch (LoRDeck.DeckSizeLimitExceededException | LoRDeck.RegionCountLimitExceededException |
                        LoRDeck.CardCopiesCountLimitExceededException | LoRDeck.ChampionCountLimitExceededException e) {
                    cardImageView.setOpacity(0.5);
                    cardImageView.setDisable(true);
                }
                // Also, multi-region cards cannot be added to a deck if there is no
                // shared region already in the deck.
                if (card.getRegions().size() >= 2) {
                    if (editedDeck.getRegions().size() == 0) {
                        cardImageView.setOpacity(0.5);
                        cardImageView.setDisable(true);
                    } else {
                        boolean sharedRegionFound = false;
                        for (LoRRegion cardRegion : card.getRegions())
                            for (LoRRegion deckRegion : editedDeck.getRegions())
                                if (cardRegion.getID() == deckRegion.getID()) {
                                    sharedRegionFound = true;
                                    break;
                                }
                        if (!sharedRegionFound) {
                            cardImageView.setOpacity(0.5);
                            cardImageView.setDisable(true);
                        }
                    }
                }

                cardImageView.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY)
                        showCardFullAsset(cardCodes.get(finalCardIndex));
                        // If the user right-clicks the card, try to add it to the currentDeck.
                        // (Since cards are only enabled if they can be added to the deck,
                        // there should not be any exceptions raised here.)
                    else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        try {
                            editedDeck.add(cardCodes.get(finalCardIndex));
                            updateDeckListView();
                            updateCountLabels();
                            updateCardsGridPane(false);
                        } catch (LoRDeck.DeckSizeLimitExceededException | LoRDeck.ChampionCountLimitExceededException |
                                LoRDeck.RegionCountLimitExceededException | LoRDeck.CardCopiesCountLimitExceededException ignored) {
                        }
                    }
                });

                cardsGridPane.add(cardImageView, j, i);
                cardIndex++;
            }
        }

        // Scroll back to the top of the grid.
        if (doScrollToTop) cardsScrollPane.setVvalue(0);
    }

    private void setupSearchTextField() {
        searchTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) updateCardsGridPane();
        });
    }

    private void setupResetButton() {
        resetButton.setOnMouseClicked(mouseEvent -> {
            searchTextField.setText("");
            for (ToggleButton button : typeToggleButtons)
                button.setSelected(true);
            for (ToggleButton button : rarityToggleButtons)
                button.setSelected(true);
            for (ToggleButton button : costToggleButtons)
                button.setSelected(true);
            updateCardsGridPane();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        regionToggleButtons = Arrays.asList(DEToggleButton, FRToggleButton, IOToggleButton, NXToggleButton, PZToggleButton, SIToggleButton, BWToggleButton, MTToggleButton, SHToggleButton, BCToggleButton, allToggleButton);
        typeToggleButtons = Arrays.asList(unitToggleButton, spellToggleButton, landmarkToggleButton);
        rarityToggleButtons = Arrays.asList(commonToggleButton, rareToggleButton, epicToggleButton, championToggleButton);
        costToggleButtons = Arrays.asList(zeroCostToggleButton, oneCostToggleButton, twoCostToggleButton, threeCostToggleButton, fourCostToggleButton, fiveCostToggleButton, sixCostToggleButton, sevenCostToggleButton, eightPlusCostToggleButton);
        placeholderCardImage = new Image(LoRMasterApplication.class.getResourceAsStream("images/placeholder.png"), 680 / 3.5, 1024 / 3.5, false, false);

        setupDeckNameTextField();
        setupDeckListView();
        updateCountLabels();
        setupRegionToggleButtons();
        updateCardsGridPane();
        setupSearchTextField();
        setupResetButton();

        LoRMasterApplication.getStage().setOnCloseRequest(windowEvent -> {
            onDoneButtonClicked();
            windowEvent.consume();
        });
    }
}
