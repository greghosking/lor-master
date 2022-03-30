package ghosking.lormaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class LoRMasterApplication extends Application {

    private static Stage primaryStage;
    private static Scene leaderboardScene;

    private static LocalDateTime timeLastUpdatedLeaderboard;

//    private static DateTime timeSinceLastUpdatedLeaderboard;

    @Override
    public void start(Stage stage) throws Exception {

        // At the start of the program...
        // 1. new thread: load champion images for the login screen.

        // then, we start the login screen...

        // 2. new thread: load collectible cards to be displayed in the collection screen.
        // 3. either at the same time as 2 or after 2, load in the rest of the assets...
        //    (non collectibles, full art for the cards...)

        FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        primaryStage = stage;
    }

    public static void main(String[] args) { launch(); }

    public static void switchToProfileScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/profile-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setScene(scene);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToCommunityScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/community-view.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void switchToCollectionScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/collection-view.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void switchToDecksScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/decks-view.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // @TODO: clean this up.
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
        primaryStage.setScene(leaderboardScene);

    }
    public static void switchToMetaScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/meta-view.fxml"));
            primaryStage.setScene(new Scene(fxmlLoader.load()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
