package ghosking.lormaster.controller;

import ghosking.lormaster.LoRMasterApplication;
import ghosking.lormaster.lor.*;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    ImageView backImageView, frontImageView;
    @FXML
    TextField gameNameTextField, tagLineTextField;
    @FXML
    Button loginButton;
    @FXML
    Label loginMessageLabel;

    private final double slideDuration = 10;
    private final double fadeDuration = 1.5;

    private LocalDateTime timeOfLastLoginAttempt;
    private final long timeBetweenLoginAttempts = 3;

    private int index;
    private LoRPlayer activePlayer;

    private void startSlideshow() {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        List<String> championCardCodes = new LoRCardDatabase.LoRCardFilter()
                .bySupertype(List.of("champion"))
                .byType(List.of(LoRType.UNIT))
                .getCardCodes();

        ArrayList<Image> images = new ArrayList<>();
        // To make sure that the slideshow has an image to start with, load a random
        // image into the images ArrayList before starting a thread to load the rest.
        index = (int) (Math.random() * championCardCodes.size());
        images.add(cardDatabase.getCard(championCardCodes.get(index)).getFullAsset());

        Thread imageLoadingThread = new Thread(() -> {
            for (String cardCode : championCardCodes) {
                images.add(cardDatabase.getCard(cardCode).getFullAsset());
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
                    index = (int) (Math.random() * images.size());
                    frontImageView.setImage(images.get(index));

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
                    index = (int) (Math.random() * images.size());
                    backImageView.setImage(images.get(index));

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

    public void attemptLogin() {
        // Hide the login message label when the user attempts to log in.
        loginMessageLabel.setVisible(false);

        // Start a separate thread to perform the login attempt in the background.
        Thread loginThread = new Thread(() -> {
            try {
                activePlayer = LoRPlayer.fromRiotID(gameNameTextField.getText(), tagLineTextField.getText());
            }
            catch (Exception ex) {
                showLoginError();
            }
        });
        loginThread.start();

        // Continually check if the login attempt has been processed without blocking.
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0), event -> {}),
                new KeyFrame(Duration.seconds(1), event -> {
                    // If the login attempt was processed and successful by now,
                    // activePlayer will not be null.
                    if (activePlayer != null) {
                        LoRMasterApplication.setActivePlayer(activePlayer);
                        LoRMasterApplication.switchToProfileScene();
                        timeline.stop();
                    }
                })
        );
        timeline.setCycleCount(3);
        timeline.play();
    }

    private void showLoginError() {
        // Show a red message at the bottom of the screen for a few seconds.
        // (The message warns the user that the login attempt failed.)
        double loginMessageDuration = 7.5;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> loginMessageLabel.setVisible(true)),
                new KeyFrame(Duration.seconds(loginMessageDuration), event -> loginMessageLabel.setVisible(false))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Prevent the user from spamming the login button and sending too many
        // login attempt requests.
        loginButton.setOnMouseClicked(mouseEvent -> {
            if (timeOfLastLoginAttempt == null ||
                    timeOfLastLoginAttempt.plusSeconds(timeBetweenLoginAttempts).isBefore(LocalDateTime.now())) {
                timeOfLastLoginAttempt = LocalDateTime.now();
                attemptLogin();
            }
        });

        startSlideshow();
    }
}
