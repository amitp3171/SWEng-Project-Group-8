package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class movieListController {

    // usage of ListView wasn't taught in class,
    // and we don't HAVE to use it, but in my opinion
    // it's more esthetically appealing.
    // keep in mind that a custom CellFactory has to be implemented
    // to display objects such as movies.
    @FXML
    ListView movieListView;

    @FXML
    private void switchToPrimary() throws IOException {
        CinemaClient.setRoot("primary");
    }

    @FXML
    void onMovieClick(MouseEvent event) {

    }

    @FXML
    void initialize() {

    }
}