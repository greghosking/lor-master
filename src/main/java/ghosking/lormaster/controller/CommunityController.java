package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRCardDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class CommunityController implements Initializable {

    @FXML
    Button profileNavigationButton;
    @FXML
    Button collectionNavigationButton;
    @FXML
    Button decksNavigationButton;
    @FXML
    Button leaderboardNavigationButton;
    @FXML
    Button metaNavigationButton;
    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link the other navigation buttons to their respective scenes.
        profileNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        collectionNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCollectionScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        imageView.setImage(LoRCardDatabase.getInstance().getCard("05PZ006").getFullAsset());
    }
}
