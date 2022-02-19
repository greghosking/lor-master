package ghosking.lormaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LeaderboardApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LeaderboardApplication.class.getResource("leaderboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Leaderboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
