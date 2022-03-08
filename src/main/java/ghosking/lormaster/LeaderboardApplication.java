package ghosking.lormaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LeaderboardApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(LeaderboardApplication.class.getResource("leaderboard-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//
//        stage.setTitle("Leaderboard");
//        stage.setScene(scene);
//        stage.show();

//        FXMLLoader fxmlLoader = new FXMLLoader(LeaderboardApplication.class.getResource("application-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//
//        stage.setTitle("App");
//        stage.setScene(scene);
//        stage.show();

        StackPane root = new StackPane();
        root.setId("background-pane");
//        Scene scene = new Scene(root, 650, 650);
        FXMLLoader fxmlLoader = new FXMLLoader(LeaderboardApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(String.valueOf(this.getClass().getResource("login-view.css")));
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) { launch(); }
}
