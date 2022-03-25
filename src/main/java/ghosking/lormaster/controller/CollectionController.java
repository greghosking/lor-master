package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class CollectionController implements Initializable {

    @FXML
    Button profileNavigationButton;
    @FXML
    Button communityNavigationButton;
    @FXML
    Button decksNavigationButton;
    @FXML
    Button leaderboardNavigationButton;
    @FXML
    Button metaNavigationButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Link the other navigation buttons to their respective scenes.
        profileNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        communityNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCommunityScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());
    }

}
