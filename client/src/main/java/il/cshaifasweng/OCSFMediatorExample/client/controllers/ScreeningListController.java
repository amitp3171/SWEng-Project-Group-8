package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewScreeningTimeListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ScreeningListController {

    @FXML
    private Label movieLabel;

    @FXML
    private Label movieSummaryLabel;

    @FXML
    private Label primaryActorsLabel;

    @FXML
    private Label producerNameLabel;

    @FXML
    private ImageView movieImageView;

    @FXML
    private ComboBox<String> selectDayListBox;

    @FXML
    private ListView<String> screeningListView;

    private String selectedBranch;

    private List<String> screeningTimes;

    private String selectedMovie;
    // yyyy-mm-dd
    private ArrayList<String> availableDates = new ArrayList<>();
    private String selectedDate;

    UserDataManager userDataManager;

    private boolean forceRefresh;

    public void setSelectedBranch(String branch) {
        selectedBranch = branch;
    }

    private String concatTimeTheater(String screeningTime) {
        // id, date, time, theater.getTheaterID()
        String[] parsedScreeningTime = screeningTime.split(",");
        // concat screening time with screening theater
        return String.format("%s, אולם %s", parsedScreeningTime[2], parsedScreeningTime[3]);
    }

    private void requestServerData() throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        // message_text,branch_location,movie_id
        Message newMessage = new Message(messageId, String.format("get ScreeningTime list,%s,%s,%s", selectedBranch, selectedMovie.split(",")[0], forceRefresh));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ScreeningTime request sent");
    }

    private void requestUpdateScreeningHour(String updatedScreeningTime) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        // message_text,branch_location,movie_id
        Message newMessage = new Message(messageId, "set ScreeningTime");
        newMessage.setData(updatedScreeningTime);
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ScreeningTime update sent");
    }

    public void setSelectedMovie(String selectedMovie, boolean forceRefresh) throws IOException {
        this.selectedMovie = selectedMovie;

        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
//        int startIdx = selectedMovie.indexOf('[') + 1;
//        int endIdx = selectedMovie.indexOf(']');
//
//        String mainActors = selectedMovie.substring(startIdx, endIdx);
//
//        String restOfMovieData = String.join("", selectedMovie.substring(0, startIdx - 2), selectedMovie.substring(endIdx + 1));

        String[] parsedMovie = selectedMovie.split(",(?![^\\[]*\\])");

        movieLabel.setText(parsedMovie[1]);
        movieSummaryLabel.setText(String.format("תקציר: %s", parsedMovie[2]));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", parsedMovie[3].substring(1, parsedMovie[3].length() - 1)));
        producerNameLabel.setText(String.format("מפיק: %s",parsedMovie[4]));

        requestServerData();
    }

    @FXML
    void chooseDay(ActionEvent event) {
        // get selected day
        String newSelectedDate = selectDayListBox.getSelectionModel().getSelectedItem();
        // if no changes should be made
        if (selectedDate.equals(newSelectedDate)) return;
        // update selected day
        selectedDate = newSelectedDate;
        // init list
        initializeList();
    }

    public void initializeDayPicker() {
        for (String screeningTime : screeningTimes) {
            // id, day, time, theater.getTheaterID()
            String dateAsString = screeningTime.split(",")[1];
            if (!this.availableDates.contains(dateAsString))
                this.availableDates.add(dateAsString);
        }
        Collections.sort(this.availableDates);
        selectDayListBox.getItems().addAll(this.availableDates);
        this.selectedDate = this.availableDates.get(0);
        selectDayListBox.getSelectionModel().selectFirst();
    }

    public void initializeList() {
        // reset listView
        screeningListView.getItems().clear();
        ArrayList<String> items = new ArrayList<>();
        // filters screenings to only include the selected day
        for (String screeningTime : screeningTimes) {
            // id, day, time, theater.getTheaterID()
            String[] parsedScreening = screeningTime.split(",");
            if (parsedScreening[1].equals(selectedDate))
                items.add(concatTimeTheater(screeningTime));
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
        EventBus.getDefault().unregister(this);
        // get controller
        InTheaterMovieListController controller = CinemaClient.setContent("inTheaterMovieList").getController();
        controller.setSelectedBranch(selectedBranch);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        if (!userDataManager.isEmployee()) onItemSelectedCustomer();
        // TODO: make sure this works!
        else /*(this.employeeType.equals("ContentManager"))*/ onItemSelectedContentManager();
    }

    void onItemSelectedCustomer() {}

    void onItemSelectedContentManager() throws IOException {
        // get screening time
        String selectedScreeningTime = screeningListView.getSelectionModel().getSelectedItem();

        if (selectedScreeningTime == null) return;

        // get screeningTime object
        int selectedIndex = screeningListView.getSelectionModel().getSelectedIndex();
        String screeningTime = screeningTimes.get(selectedIndex);

        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("screeningEditor");
        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        ScreeningEditorController screeningEditorController = dialogLoader.getController();
        // set current screening hour
        screeningEditorController.setScreeningHour(selectedScreeningTime);
        StringBuilder mutableScreeningTime = new StringBuilder(screeningTime);
        screeningEditorController.setSelectedScreeningTime(mutableScreeningTime);

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
            // get new time
            String newTime = mutableScreeningTime.toString();
            // update ScreeningTime
            String[] parsedSelectedScreeningTime =  screeningTimes.get(selectedIndex).split(",");
            parsedSelectedScreeningTime[2] = newTime;
            String reconstructedScreeningTime = String.join(",", parsedSelectedScreeningTime);
            // update listView
            screeningTimes.set(selectedIndex, reconstructedScreeningTime);
            screeningListView.getItems().set(selectedIndex, concatTimeTheater(reconstructedScreeningTime));

            requestUpdateScreeningHour(reconstructedScreeningTime);
        };

        // unregister dialog in case X button was pressed
        if (EventBus.getDefault().isRegistered(screeningEditorController)) EventBus.getDefault().unregister(screeningEditorController);

        // clear selection
        screeningListView.getSelectionModel().clearSelection();
    }

    @Subscribe
    public void onUpdateScreeningTimeEvent(NewScreeningTimeListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                screeningTimes = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                initializeDayPicker();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("ScreeningTime request received");
        });
    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
        forceRefresh = false;
        // register to EventBus
        EventBus.getDefault().register(this);
    }

}
