package ghosking.lormaster;

import ghosking.lormaster.lor.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class LoRMasterApplication extends Application {

    // The user that is currently logged in.
    private static LoRPlayer activePlayer;

    private static Stage primaryStage;
    private static Scene leaderboardScene;

    private static LocalDateTime timeLastUpdatedLeaderboard;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LoRMasterApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        primaryStage = stage;

        // @TODO: Load other scenes in the background in separate threads.
    }

    public static void main(String[] args) { launch(); }

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
