package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MovieTypeSelectionController {

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showComingSoonMovieList(ActionEvent event) {

    }

    @FXML
    void showHomeMovieList(ActionEvent event) {

    }

    @FXML
    void showInTheaterMovieList(ActionEvent event) throws IOException {
        CinemaClient.setContent("inTheaterMovieList");
    }

    @FXML
    void initialize() {

    }

}
