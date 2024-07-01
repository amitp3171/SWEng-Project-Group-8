package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.DatabaseBridge;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.InTheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.ScreeningTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class ScreeningListController {

    @FXML
    private Label movieLabel;

    @FXML
    private ListView<String> screeningListView;

    private List<ScreeningTime> screeningTimes;

    private InTheaterMovie selectedMovie;

    public void setSelectedMovie(InTheaterMovie selectedMovie) {
        this.selectedMovie = selectedMovie;
        movieLabel.setText(selectedMovie.getMovieName());
        initializeList();
    }

    public void initializeList() {
        // get screenings of the selected movies
        screeningTimes = selectedMovie.getScreenings();
        // get screenings times as string
        String[] items = new String[screeningTimes.size()];
        for (int i = 0; i < screeningTimes.size(); i++) {
            items[i] = screeningTimes.get(i).getTime().toString();
        }
        // display in list
        screeningListView.getItems().addAll(items);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("inTheaterMovieList");
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // get screening time
        String selectedScreeningTime = screeningListView.getSelectionModel().getSelectedItem();

        if (selectedScreeningTime == null) return;

        // get screeningTime object
        int selectedIndex = screeningListView.getSelectionModel().getSelectedIndex();
        ScreeningTime screeningTime = screeningTimes.get(selectedIndex);

        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("screeningEditor");
        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        ScreeningEditorController screeningEditorController = dialogLoader.getController();
        // set current screening hour
        screeningEditorController.setScreeningHour(selectedScreeningTime);
        screeningEditorController.setSelectedScreeningTime(screeningTime);

        // create new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(screeningDialogPane);
        screeningEditorController.setDialog(dialog);

        // show dialog
        dialog.showAndWait();

        // if change was performed
        if (dialog.getResult() == ButtonType.OK) {
            screeningListView.getItems().set(selectedIndex, screeningTime.getTime().toString());
            DatabaseBridge db = DatabaseBridge.getInstance();
            // update DB entry
            db.updateEntity(selectedMovie);
        };
    }

    @FXML
    void initialize() {}

}
