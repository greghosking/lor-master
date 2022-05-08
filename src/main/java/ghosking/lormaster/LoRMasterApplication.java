package ghosking.lormaster;

import ghosking.lormaster.controller.DeckEditorController;
import ghosking.lormaster.lor.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoRMasterApplication extends Application {

    private static Stage stage;
    private static Scene profileScene;
    private static Scene collectionScene;
    private static Scene leaderboardScene;
    private static Scene metaScene;

    private static LoRPlayer user;
    private static List<Image> regionIcons;

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
        if (profileScene == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/profile-view.fxml"));
                profileScene = new Scene(fxmlLoader.load());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        stage.setScene(profileScene);
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

    private static void loadLeaderboardScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/leaderboard-view.fxml"));
            leaderboardScene = new Scene(fxmlLoader.load());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToLeaderboardScene() {
        if (leaderboardScene == null)
            loadLeaderboardScene();
        stage.setScene(leaderboardScene);
    }

    private static void loadMetaScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/meta-view.fxml"));
            metaScene = new Scene(fxmlLoader.load());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToMetaScene() {
        if (metaScene == null)
            loadMetaScene();
        stage.setScene(metaScene);
    }

    public static LoRPlayer getUser() {
        return user;
    }

    public static void setUser(LoRPlayer player) {
        user = player;
    }

    public static List<Image> getRegionIcons() {
        return regionIcons;
    }

    @Override
    public void start(Stage stage) {
        LoRMasterApplication.stage = stage;
        LoRMasterApplication.stage.setResizable(false);
        LoRMasterApplication.stage.getIcons().add(new Image(LoRMasterApplication.class.getResourceAsStream("images/lor-icon.png")));
        LoRMasterApplication.stage.setTitle("Legends of Runeterra Master");

        regionIcons = new ArrayList<>();
        String baseURL = "https://dd.b.pvp.net/3_4_0/core/en_us/img/regions/icon-";
        List<String> iconFilenames = Arrays.asList("demacia.png", "freljord.png", "ionia.png", "noxus.png", "piltoverzaun.png",
                "shadowisles.png", "bilgewater.png", "shurima.png", "all.png", "targon.png", "bandlecity.png");
        for (String iconFilename : iconFilenames) {
            regionIcons.add(new Image(baseURL + iconFilename, 80 / 1.5, 120 / 1.75, false, true, false));
        }

        // Start the app in the login scene.
        switchToLoginScene();
//        switchToLiveMatchScene();
        LoRMasterApplication.stage.show();

        // Immediately start to load the collection scene in the background so that
        // the card images have more time to load.
        Thread collectionSceneLoaderThread = new Thread(LoRMasterApplication::loadCollectionScene);
        collectionSceneLoaderThread.start();
        Thread leaderboardSceneLoaderThread = new Thread(LoRMasterApplication::loadLeaderboardScene);
        leaderboardSceneLoaderThread.start();
    }

    public static void main(String[] args) { launch(); }
}
