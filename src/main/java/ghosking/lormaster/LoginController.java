package ghosking.lormaster;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

//    @FXML
//    private AnchorPane backgroundPane;
    @FXML
    private ImageView backImageView;
    @FXML
    private ImageView frontImageView;
//    @FXML
//    private Rectangle overlayRectangle;
//    @FXML
//    private Label titleLabel1;
//    @FXML
//    private Label titleLabel2;
    @FXML
    private TextField gameNameTextField;
    @FXML
    private TextField tagLineTextField;
//    @FXML
//    private Button loginButton;

    private int randomIndex;
    private final double slideDuration = 10;
    private final double fadeDuration = 1.5;

    public void slideshow() {

        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        ArrayList<Image> images = new ArrayList<>();
        ;
        // Before the start of the slideshow, load a random image into the images ArrayList.
        ArrayList<String> cardCodes = cardDatabase.getCardCodes();
        while (images.size() < 1) {
            randomIndex = (int) (Math.random() * cardCodes.size());
            LoRCard card = cardDatabase.getCard(cardCodes.get(randomIndex));
            if (card.getType().compareToIgnoreCase("Unit") == 0 && card.getSupertype().compareTo("Champion") == 0) {
                images.add(new Image(card.getAssets().get(1)));
            }
        }

        // Then, start a separate thread to load the rest of the images in the background.
        Thread imageLoadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (String code : cardCodes) {
                    LoRCard card = cardDatabase.getCard(code);
                    if (card.getType().compareToIgnoreCase("Unit") == 0 && card.getSupertype().compareTo("Champion") == 0) {
                        images.add(new Image(card.getAssets().get(1)));
                    }
                }
            }
        });
        imageLoadingThread.start();

        // Before starting the animation, set the image of the backImageView to the first loaded image
        // and make the frontImageView completely transparent.
        backImageView.setImage(images.get(0));
        frontImageView.setOpacity(0);

        Timeline timeline = new Timeline(
                // Show the backImageView for slideDuration seconds while slowly zooming in.
                new KeyFrame(Duration.seconds(0), event -> {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(slideDuration), backImageView);
                    scaleTransition.setToX(1.125);
                    scaleTransition.setToY(1.125);
                    scaleTransition.setCycleCount(1);
                    scaleTransition.play();
                }),
                // Set the image of the frontImageView to a random loaded image and fade it in
                // over fadeDuration seconds.
                new KeyFrame(Duration.seconds(slideDuration), event -> {
                    randomIndex = (int) (Math.random() * images.size());
                    frontImageView.setImage(images.get(randomIndex));

                    // Before making the frontImageView visible, reset its scale.
                    frontImageView.setScaleX(1);
                    frontImageView.setScaleY(1);

                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(fadeDuration), frontImageView);
                    fadeTransition.setFromValue(0);
                    fadeTransition.setToValue(1);
                    fadeTransition.setCycleCount(1);
                    fadeTransition.play();
                }),
                // Show the frontImageView for slideDuration seconds while slowly zooming in.
                new KeyFrame(Duration.seconds(slideDuration + fadeDuration), event -> {
                    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(slideDuration), frontImageView);
                    scaleTransition.setToX(1.125);
                    scaleTransition.setToY(1.125);
                    scaleTransition.setCycleCount(1);
                    scaleTransition.play();
                }),
                // Set the image of the backImageView to a random loaded image and fade the
                // frontImageView out over fadeDuration seconds.
                new KeyFrame(Duration.seconds((slideDuration * 2) + fadeDuration), event -> {
                    randomIndex = (int) (Math.random() * images.size());
                    backImageView.setImage(images.get(randomIndex));

                    // Before making the backImageView visible, reset its scale.
                    backImageView.setScaleX(1);
                    backImageView.setScaleY(1);

                    FadeTransition ft = new FadeTransition(Duration.seconds(fadeDuration), frontImageView);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setCycleCount(1);
                    ft.play();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void login() {

        // Start a separate thread to perform the login attempt in the background.
        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the text from the first TextField, replacing whitespace with %20
                // to be compatible with the login request URL.
                String gameName = gameNameTextField.getText().replace(" ", "%20");
                // Get the text from the second text field, removing any # characters or whitespace
                // and getting up to the first five characters (since tag lines are 3-5 characters).
                String tagLine = tagLineTextField.getText().replace("#", "")
                        .replace(" ", "");
                tagLine = tagLine.substring(0, Math.min(tagLine.length(), 5));

                String url = "https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/" +
                        gameName + "/" + tagLine + "?api_key=" + LoRAPIRequest.apiKey;

                String accountJSON = LoRAPIRequest.get(url);

                // @TODO: LoRPlayerDatabase... or LoRAccountDatabase... contains active player field
                // @TODO: and all other players, maintaining a match history and such for each.

                System.out.println(url);
//                System.out.println(LoRAPIRequest.get(url));
                if (accountJSON == null) {

                }
            }
        });
        loginThread.start();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slideshow();
    }
}
