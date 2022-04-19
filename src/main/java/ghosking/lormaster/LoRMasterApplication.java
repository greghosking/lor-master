package ghosking.lormaster;

import ghosking.lormaster.lor.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoRMasterApplication extends Application {

    private static Stage stage;
    private static Scene loginScene;
    private static Scene profileScene;
    private static Scene communityScene;
    private static Scene collectionScene;
    private static Scene decksScene;
    private static Scene leaderboardScene;
    private static Scene metaScene;

    private static LocalDateTime timeOfLastProfileUpdate;
    private static LocalDateTime timeOfLastLeaderboardUpdate;
    private static LocalDateTime timeOfLastMetaUpdate;

    // The user that is currently logged in.
    private static LoRPlayer activePlayer;


    private static LocalDateTime timeLastUpdatedLeaderboard;

    @Override
    public void start(Stage stage) {
        LoRMasterApplication.stage = stage;
        LoRMasterApplication.stage.setResizable(false);
        switchToLoginScene();

        // Start a separate thread to load all game assets in the background.
//        Thread gameAssetLoaderThread = new Thread(() -> {
//            LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
//            ArrayList<String> collectibleCardCodes = cardDatabase.getCardCodesByCollectible(cardDatabase.getCardCodes(), true, false);
//            System.out.println("Collectible cards size: " + collectibleCardCodes.size());
//            int cardsLoaded = 0;
//            for (String cardCode : collectibleCardCodes) {
//                cardsLoaded++;
//                System.out.println("Loading " + cardDatabase.getCard(cardCode).getName() + " (" + cardCode + ") (" + cardsLoaded + "/" + collectibleCardCodes.size() + ")");
//                cardDatabase.getCard(cardCode).getGameAsset();
//
//            }
//        });
//        gameAssetLoaderThread.start();

        // @TODO: Load other scenes in the background in separate threads.
        Thread collectionSceneLoaderThread = new Thread(() -> {
            loadCollectionScene();
        });
        collectionSceneLoaderThread.start();


        LoRMasterApplication.stage.show();
    }

    public static void main(String[] args) { launch(); }

    private static void loadLoginScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/login-view.fxml"));
            loginScene = new Scene(fxmlLoader.load());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToLoginScene() {
        if (loginScene == null) {
            loadLoginScene();
        }
        stage.setScene(loginScene);
    }

    private static void loadProfileScene() {

    }

    public static LoRPlayer getActivePlayer() {
        return activePlayer;
    }

    public static void setActivePlayer(LoRPlayer player) {
        activePlayer = player;
    }

    public static void switchToProfileScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/profile-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadCollectionScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/collection-view.fxml"));
            collectionScene = new Scene(fxmlLoader.load());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static void switchToCollectionScene() {
        if (collectionScene == null) {
            loadCollectionScene();
        }
        stage.setScene(collectionScene);
    }

    public static void switchToLiveMatchScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/live-match-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
//    public static void switchToCollectionScene() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/collection-view.fxml"));
//            stage.setScene(new Scene(fxmlLoader.load()));
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public static void switchToDecksScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/decks-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // @TODO: Clean up this method.
    public static void switchToLeaderboardScene() {
        if (timeLastUpdatedLeaderboard == null || timeLastUpdatedLeaderboard.plusMinutes(10).isBefore(LocalDateTime.now())) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/leaderboard-view.fxml"));
                leaderboardScene = new Scene(fxmlLoader.load());
                timeLastUpdatedLeaderboard = LocalDateTime.now();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        stage.setScene(leaderboardScene);

    }
    public static void switchToMetaScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/meta-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
