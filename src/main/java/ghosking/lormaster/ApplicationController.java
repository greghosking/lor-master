package ghosking.lormaster;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationController implements Initializable {

    @FXML
    Button profileButton;
    @FXML
    Button cardsButton;
    @FXML
    Button decksButton;

    // When we click a button... we want to do the following:
        // Reset the background color of all other buttons...
        // Set the background color of the button that was clicked to a lighter tone
        // (using setStyle).
        // Switch the content of the pane to correspond to whichever button was clicked.

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        profileButton.setOnMouseClicked(mouseEvent -> profileButton.setStyle("-fx-background-color: #FF0000"));
    }
}
