package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private ImageView backImageView;
    @FXML
    private ImageView frontImageView;
    @FXML
    private TextField gameNameTextField;
    @FXML
    private TextField tagLineTextField;

    private int randomIndex;
    private final double slideDuration = 10;
    private final double fadeDuration = 1.5;

    public void slideshow() {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        ArrayList<Image> images = new ArrayList<>();

        // Before the start of the slideshow, load a random image into the images ArrayList.
        ArrayList<String> cardCodes = cardDatabase.getCardCodes();
        while (images.size() < 1) {
            randomIndex = (int) (Math.random() * cardCodes.size());
            LoRCard card = cardDatabase.getCard(cardCodes.get(randomIndex));
            if (card.getType().compareToIgnoreCase("Unit") == 0 && card.getSupertype().compareTo("Champion") == 0) {

                images.add(card.getFullAsset());
            }
        }

        // Then, start a separate thread to load the rest of the images in the background.
        Thread imageLoadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (String code : cardCodes) {
                    LoRCard card = cardDatabase.getCard(code);
                    if (card.getType().compareToIgnoreCase("Unit") == 0 && card.getSupertype().compareTo("Champion") == 0) {
                        images.add(card.getFullAsset());
                    }
                }
            }
        });
        imageLoadingThread.start();

        // Before starting the animation, set the image of the backImageView to the first loaded image and make the
        // frontImageView completely transparent.
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

            }
        });
        loginThread.start();

        LoRMasterApplication.switchToProfileScene();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slideshow();
    }
}
