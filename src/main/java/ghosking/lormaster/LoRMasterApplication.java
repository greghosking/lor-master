package ghosking.lormaster;

import ghosking.lormaster.controller.DeckEditorController;
import ghosking.lormaster.lor.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoRMasterApplication extends Application {

    private static Stage stage;
    private static Scene loginScene;
    private static Scene profileScene;
    private static Scene communityScene;
    private static Scene collectionScene;
    private static Scene decksScene;
    private static Scene leaderboardScene;
    private static Scene metaScene;

    private static LoRPlayer user;

    public static Stage getStage() {
        return stage;
    }

    public static void switchToLoginScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/login-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
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
        if (collectionScene == null)
            loadCollectionScene();
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

    public static void switchToDecksScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/decks-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToDeckEditorScene(LoRDeck deck) {
        try {
            DeckEditorController.setUneditedDeck(deck);
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/deck-editor-view.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToLeaderboardScene() {
        if (leaderboardScene == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/leaderboard-view.fxml"));
                leaderboardScene = new Scene(fxmlLoader.load());
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

    public static LoRPlayer getUser() {
        return user;
    }

    public static void setUser(LoRPlayer player) {
        user = player;
    }

    @Override
    public void start(Stage stage) {
        LoRMasterApplication.stage = stage;
        LoRMasterApplication.stage.setResizable(false);
        LoRMasterApplication.stage.getIcons().add(new Image(LoRMasterApplication.class.getResourceAsStream("images/lor-icon.png")));
        LoRMasterApplication.stage.setTitle("Legends of Runeterra Master");

        // Start the app in the login scene.
        switchToLoginScene();
        LoRMasterApplication.stage.show();

        // Immediately start to load the collection scene in the background so that
        // the card images have more time to load.
        Thread collectionSceneLoaderThread = new Thread(LoRMasterApplication::loadCollectionScene);
        collectionSceneLoaderThread.start();
    }

    public static void main(String[] args) { launch(); }
}
