package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link the other navigation buttons to their respective scenes.
        profileButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        liveMatchButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLiveMatchScene());
        collectionButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        leaderboardButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

}
