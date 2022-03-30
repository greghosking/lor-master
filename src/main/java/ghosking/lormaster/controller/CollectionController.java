package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.LoRCardDatabase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
    @FXML
    GridPane cardsGridPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Link the other navigation buttons to their respective scenes.
        profileNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToProfileScene());
        communityNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToCommunityScene());
        decksNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToDecksScene());
        leaderboardNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToLeaderboardScene());
        metaNavigationButton.setOnMouseClicked(mouseEvent -> LoRMasterApplication.switchToMetaScene());

        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();


//        ImageView imageView = new ImageView();
//        GridPane gridpane = new GridPane();
//        Image image = new Image("File:image/myfile.jpg")

        ImageView imageView = new ImageView(cardDatabase.getCard("01DE001").getGameAsset());
//        imageView.setFitWidth(cardsGridPane.getWidth()/cardsGridPane.getColumnCount());
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);
        ImageView imageView1 = new ImageView(cardDatabase.getCard("01DE002").getGameAsset());
//        imageView.setFitWidth(cardsGridPane.getWidth()/cardsGridPane.getColumnCount());
        imageView1.setFitWidth(300);
        imageView1.setPreserveRatio(true);
//        cardsGridPane.getChildren().add(imageView);
//        cardsGridPane.getChildren().add(imageView1);
        cardsGridPane.add(imageView, 0, 0);
        cardsGridPane.add(imageView1, 1, 0);
    }

}
