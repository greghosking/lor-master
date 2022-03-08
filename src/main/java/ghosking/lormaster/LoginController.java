package ghosking.lormaster;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML ImageView backImageView;
    @FXML ImageView frontImageView;


    int count = 0;

    int index;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

//        ArrayList<Image> images = new ArrayList<>();



        ArrayList<String> imageUrls = new ArrayList<>();

        for (String code : cardDatabase.getCardCodes()) {
            LoRCard card = cardDatabase.getCard(code);
            if (card.getSupertype().compareToIgnoreCase("Champion") == 0 && card.getType().compareTo("Unit") == 0) {
                imageUrls.add(card.getAssets().get(1));
            }
        }

        index = (int) (Math.random() * imageUrls.size());
        backImageView.setImage(new Image(imageUrls.get(index)));
        frontImageView.setOpacity(0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(5));
                    pause.play();
                }),
                new KeyFrame(Duration.seconds(5), event -> {
                    index = (int) (Math.random() * imageUrls.size());
                    frontImageView.setImage(new Image(imageUrls.get(index)));

                    FadeTransition ft = new FadeTransition(Duration.seconds(1), frontImageView);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.setCycleCount(1);
                    ft.play();
                }),
                new KeyFrame(Duration.seconds(6), event -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(5));
                    pause.play();
                }),
                new KeyFrame(Duration.seconds(11), event -> {
                    index = (int) (Math.random() * imageUrls.size());
                    backImageView.setImage(new Image(imageUrls.get(index)));
//                    imageView2.setOpacity(0);
                    FadeTransition ft = new FadeTransition(Duration.seconds(1), frontImageView);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setCycleCount(1);
                    ft.play();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();

    }



}
