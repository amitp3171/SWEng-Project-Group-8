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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class ScreeningListController {

    @FXML
    private Label movieLabel;

    @FXML
    private Label movieInfoLabel;

    @FXML
    private ComboBox<String> selectDayListBox;

    @FXML
    private ListView<String> screeningListView;

    private List<ScreeningTime> screeningTimes;

    private InTheaterMovie selectedMovie;

    // default value is sunday
    private ScreeningTime.Day selectedDay = ScreeningTime.Day.SUNDAY;

    private String concatTimeTheater(int index) {
        // concat screening time with screening theater
        return String.format("%s, אולם %s", screeningTimes.get(index).getTime().toString(), screeningTimes.get(index).getTheater().getTheaterNumber());
    }

    public void setSelectedMovie(InTheaterMovie selectedMovie) {
        this.selectedMovie = selectedMovie;

        movieLabel.setText(selectedMovie.getMovieName());
        movieInfoLabel.setText("תקציר: " + selectedMovie.getDescription() + '\n'
                + "שחקנים ראשיים: " + selectedMovie.getMainActors() + '\n'
                + "מפיק: " + selectedMovie.getProducerName() + '\n'
                + selectedMovie.getPicture());

        initializeList();
    }

    @FXML
    void chooseDay(ActionEvent event) {
        // get selected day
        ScreeningTime.Day newSelectedDay = ScreeningTime.Day.values()[selectDayListBox.getSelectionModel().getSelectedIndex()];
        // if no changes should be made
        if (selectedDay == newSelectedDay) return;
        // update selected day
        selectedDay = newSelectedDay;
        // init list
        initializeList();
    }

    public void initializeList() {
        // reset listView
        screeningTimes = new ArrayList<>();
        screeningListView.getItems().clear();
        // filters screenings to only include the selected day
        for (ScreeningTime screeningTime : selectedMovie.getScreenings()) {
            if (screeningTime.getDay() == selectedDay)
                screeningTimes.add(screeningTime);
        }
        // get screenings times as string
        String[] items = new String[screeningTimes.size()];
        for (int i = 0; i < screeningTimes.size(); i++) {
            items[i] = concatTimeTheater(i);
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

        // create hidden close button to support the close button (X)
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        // show dialog
        dialog.showAndWait();

        // if change was performed
        if (dialog.getResult() == ButtonType.OK) {
            // concat string
            String newLine = concatTimeTheater(selectedIndex);
            // update listView
            screeningListView.getItems().set(selectedIndex, newLine);
            DatabaseBridge db = DatabaseBridge.getInstance();
            // update DB entry
            db.updateEntity(selectedMovie);
        };

        // clear selection
        screeningListView.getSelectionModel().clearSelection();
    }

    @FXML
    void initialize() {
        String[] items = {"יום ראשון", "יום שני", "יום שלישי", "יום רביעי", "יום חמישי", "יום שישי", "יום שבת"};
        selectDayListBox.getItems().addAll(items);
    }

}
