package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    public void setMovieLabel(String movieName) {
        movieLabel.setText(movieName);
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
        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader); //CinemaClient.loadFXML("screeningEditor");

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
        };
    }

    @FXML
    void initialize() {
        // modify to fetch from DB later
        screeningTimes = new ArrayList<>();
        ScreeningTime firstTime = new ScreeningTime();
        firstTime.setTime(LocalTime.of(17, 30));
        screeningTimes.add(firstTime);
        String[] items = {firstTime.getTime().toString()};
        screeningListView.getItems().addAll(items);
    }

}
