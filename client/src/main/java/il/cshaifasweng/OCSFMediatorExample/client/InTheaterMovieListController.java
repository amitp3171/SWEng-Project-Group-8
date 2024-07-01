package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class InTheaterMovieListController {

    // usage of ListView wasn't taught in class,
    // and we don't HAVE to use it, but in my opinion
    // it's more esthetically appealing.
    // keep in mind that a custom CellFactory has to be implemented
    // to display objects such as movies.
    @FXML
    private ListView<String> movieListView;

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        String selectedMovie = movieListView.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) return;

        FXMLLoader screeningLoader = CinemaClient.setContent("screeningList");

        ScreeningListController screeningController = screeningLoader.getController();
        screeningController.setMovieLabel(selectedMovie);

//        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML("movieEditor");
//
//        Dialog<ButtonType> dialog = new Dialog<>();
//        dialog.getDialogPane().setContent(screeningDialogPane);
//        dialog.setTitle("בחר הקרנה");
//
//        dialog.showAndWait();
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void initialize() {
        String[] items = {"הארי פוטר", "מלחמת הכוכבים", "מועדון קרב"};
        movieListView.getItems().addAll(items);
    }
}