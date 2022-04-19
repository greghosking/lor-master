package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRCardDatabase;
import ghosking.lormaster.lor.LoRRarity;
import ghosking.lormaster.lor.LoRRegion;
import ghosking.lormaster.lor.LoRType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class CollectionController implements Initializable {

    @FXML
    Button profileButton, liveMatchButton, decksButton, leaderboardButton, metaButton;
    @FXML
    ToggleButton DEToggleButton, FRToggleButton, IOToggleButton, NXToggleButton, PZToggleButton, SIToggleButton;
    @FXML
    ToggleButton BWToggleButton, MTToggleButton, SHToggleButton, BCToggleButton, allToggleButton;
    @FXML
    TextField searchTextField;
    @FXML
    ToggleButton unitToggleButton, spellToggleButton, landmarkToggleButton;
    @FXML
    ToggleButton commonToggleButton, rareToggleButton, epicToggleButton, championToggleButton;
    @FXML
    ToggleButton zeroCostToggleButton, oneCostToggleButton, twoCostToggleButton, threeCostToggleButton, fourCostToggleButton;
    @FXML
    ToggleButton fiveCostToggleButton, sixCostToggleButton, sevenCostToggleButton, eightPlusCostToggleButton;
    @FXML
    Button resetButton;

    @FXML
    ScrollPane cardsScrollPane;
    @FXML
    GridPane cardsGridPane;

    List<ToggleButton> regionToggleButtons;
    List<ToggleButton> typeToggleButtons;
    List<ToggleButton> rarityToggleButtons;
    List<ToggleButton> costToggleButtons;

    private void updateCardsGridPane() {
        // Clear the grid contents.
        for (Node node : cardsGridPane.getChildren())
            ((ImageView) node).setImage(null);

        List<LoRRegion> regionsToInclude = new ArrayList<>();
        if (DEToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("DE"));
        if (FRToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("FR"));
        if (IOToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("IO"));
        if (NXToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("NX"));
        if (PZToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("PZ"));
        if (SIToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("SI"));
        if (BWToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("BW"));
        if (MTToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("MT"));
        if (SHToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("SH"));
        if (BCToggleButton.isSelected()) regionsToInclude.add(LoRRegion.fromCode("BC"));

        List<LoRType> typesToInclude = new ArrayList<>();
        if (unitToggleButton.isSelected()) typesToInclude.add(LoRType.UNIT);
        if (spellToggleButton.isSelected()) typesToInclude.add(LoRType.SPELL);
        if (landmarkToggleButton.isSelected()) typesToInclude.add(LoRType.LANDMARK);

        List<LoRRarity> raritiesToInclude = new ArrayList<>();
        if (commonToggleButton.isSelected()) raritiesToInclude.add(LoRRarity.COMMON);
        if (rareToggleButton.isSelected()) raritiesToInclude.add(LoRRarity.RARE);
        if (epicToggleButton.isSelected()) raritiesToInclude.add(LoRRarity.EPIC);
        if (championToggleButton.isSelected()) raritiesToInclude.add(LoRRarity.CHAMPION);

        List<Integer> costsToInclude = new ArrayList<>();
        if (zeroCostToggleButton.isSelected()) costsToInclude.add(0);
        if (oneCostToggleButton.isSelected()) costsToInclude.add(1);
        if (twoCostToggleButton.isSelected()) costsToInclude.add(2);
        if (threeCostToggleButton.isSelected()) costsToInclude.add(3);
        if (fourCostToggleButton.isSelected()) costsToInclude.add(4);
        if (fiveCostToggleButton.isSelected()) costsToInclude.add(5);
        if (sixCostToggleButton.isSelected()) costsToInclude.add(6);
        if (sevenCostToggleButton.isSelected()) costsToInclude.add(7);
        if (sevenCostToggleButton.isSelected()) costsToInclude.addAll(Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15));

        // Start to filter the cards by the selected options.
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        LoRCardDatabase.LoRCardFilter filter = new LoRCardDatabase.LoRCardFilter()
                .byRegion(regionsToInclude)
                .byType(typesToInclude)
                .byRarity(raritiesToInclude)
                .byCost(costsToInclude)
                .byCollectible(true);
        // If the search bar is not empty, search for the user query.
        if (!searchTextField.getText().equalsIgnoreCase("")) filter.search(searchTextField.getText());
        // Sort and store the contents of the filter in a new list.
        List<String> cardCodes = filter.sort().getCardCodes();

        for (int i = 0; i < cardCodes.size(); i++) {
            int cardIndex = i;
            ((ImageView) cardsGridPane.getChildren().get(i)).setImage(cardDatabase.getCard(cardCodes.get(i)).getGameAsset());
            cardsGridPane.getChildren().get(i).setOnMouseClicked(mouseEvent -> showCardFullAsset(cardCodes.get(cardIndex)));
        }

        // Scroll back to the top of the grid.
        cardsScrollPane.setVvalue(0);
    }

    private void showCardFullAsset(String cardCode) {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        // Create a new stage with a black background.
        StackPane stack = new StackPane();
        stack.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), Insets.EMPTY)));
        Scene scene = new Scene(stack, 1280, 720);

        double imageW = 1280;
        double imageH = 720;
        // The expanded art for spells is circular and should have equal dimensions.
        if (cardDatabase.getCard(cardCode).getType() == LoRType.SPELL) {
            imageW = 720 / 1.25;
            imageH = 720 / 1.25;
        }
        ImageView fullImageView = new ImageView(cardDatabase.getCard(cardCode).getFullAsset());
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link the other navigation buttons to their respective scenes.
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        decksButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        regionToggleButtons = Arrays.asList(DEToggleButton, FRToggleButton, IOToggleButton, NXToggleButton, PZToggleButton,
                SIToggleButton, BWToggleButton, MTToggleButton, SHToggleButton, BCToggleButton, allToggleButton);
        typeToggleButtons = Arrays.asList(unitToggleButton, spellToggleButton, landmarkToggleButton);
        rarityToggleButtons = Arrays.asList(commonToggleButton, rareToggleButton, epicToggleButton, championToggleButton);
        costToggleButtons = Arrays.asList(zeroCostToggleButton, oneCostToggleButton, twoCostToggleButton, threeCostToggleButton,
                fourCostToggleButton, fiveCostToggleButton, sixCostToggleButton, sevenCostToggleButton, eightPlusCostToggleButton);

        // Load the icons for each region toggle button and set the click handlers.
        String baseURL = "https://dd.b.pvp.net/3_4_0/core/en_us/img/regions/icon-";
        List<String> iconURLS = Arrays.asList("demacia.png", "freljord.png", "ionia.png", "noxus.png", "piltoverzaun.png",
                "shadowisles.png", "bilgewater.png", "targon.png", "shurima.png", "bandlecity.png", "all.png");
        for (int i = 0; i < regionToggleButtons.size(); i++) {
            double w = regionToggleButtons.get(i).getPrefWidth() / 1.5;
            double h = regionToggleButtons.get(i).getPrefHeight() / 1.75;
            regionToggleButtons.get(i).setGraphic(new ImageView(new Image(baseURL + iconURLS.get(i), w, h, false, true, true)));

            // Each region toggle button should call updateCardsGrid() when clicked,
            // and should also toggle the allToggleButton as needed.
            if (i < regionToggleButtons.size() - 1) {
                regionToggleButtons.get(i).setOnMouseClicked(mouseEvent -> {
                    updateCardsGridPane();

                    allToggleButton.setSelected(true);
                    for (int j = 0; j < regionToggleButtons.size() - 1; j++)
                        if (!regionToggleButtons.get(j).isSelected()) allToggleButton.setSelected(false);
                });
            }
            // The allToggleButton should toggle all other region toggle buttons
            // and then call updateCardsGrid().
            else {
                regionToggleButtons.get(i).setOnMouseClicked(mouseEvent -> {
                    for (int j = 0; j < regionToggleButtons.size() - 1; j++)
                        regionToggleButtons.get(j).setSelected(allToggleButton.isSelected());
                    updateCardsGridPane();
                });
            }
        }

        searchTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) updateCardsGridPane();
        });

        // Set up the click handlers for type, rarity, and cost toggle buttons.
        for (ToggleButton button : typeToggleButtons)
            button.setOnMouseClicked(mouseEvent -> updateCardsGridPane());
        for (ToggleButton button : rarityToggleButtons)
            button.setOnMouseClicked(mouseEvent -> updateCardsGridPane());
        for (ToggleButton button : costToggleButtons)
            button.setOnMouseClicked(mouseEvent -> updateCardsGridPane());

        // Also make the reset button clear the search bar, re-select all type,
        // rarity, and cost toggle buttons, and update the cardsGridPane.
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

        // Get all collectible cards.
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        List<String> cardCodes = new LoRCardDatabase.LoRCardFilter().byCollectible(true).sort().getCardCodes();

        // Calculate the number of rows necessary to display all cards and resize
        // the cardsGridPane accordingly.
        int rows = (int) Math.ceil(cardCodes.size() / 4.0);
        int cols = cardsGridPane.getColumnCount();
        cardsGridPane.setPrefHeight(rows * 1024 / 3.5);
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(1024 / 3.5);
            rowConst.setValignment(VPos.CENTER);
            cardsGridPane.getRowConstraints().add(rowConst);
        }

        // Add the cards to the grid.
        int cardIndex = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cardIndex >= cardCodes.size())
                    break;
                int _cardIndex = cardIndex;

                // Create the ImageView for the current card and set up a mouse
                // event handler to show the full art when the card is clicked.
                ImageView cardImageView = new ImageView(cardDatabase.getCard(cardCodes.get(cardIndex)).getGameAsset());
                cardImageView.setOnMouseClicked(mouseEvent -> showCardFullAsset(cardCodes.get(_cardIndex)));

                cardsGridPane.add(cardImageView, j, i);
                cardIndex++;
            }
        }
    }
}
