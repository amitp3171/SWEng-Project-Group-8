package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalDate;
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
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
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
    private DatePicker screeningDatePicker;

    @FXML
    private ListView<String> screeningListView;

    private String selectedBranch;

//    private ArrayList<DateCell> highlightedDateCells = new ArrayList<>();
    private ArrayList<LocalDate> availableDates = new ArrayList<>();
    private String selectedMovie;
    // yyyy-mm-dd
    private List<String> screeningTimes;
    private ArrayList<String> availableDatesStrings = new ArrayList<>();
    private LocalDate selectedDate;

    UserDataManager userDataManager;

    private boolean forceRefresh;

    public void setSelectedBranch(String branch) {
        this.selectedBranch = branch;
    }

    private String concatTimeTheater(String screeningTime) {
        // id, date, time, theater.getTheaterID()
        String[] parsedScreeningTime = screeningTime.split(",");
        // concat screening time with screening theater
        return String.format("%s, אולם %s", parsedScreeningTime[2], (1 + (Integer.parseInt(parsedScreeningTime[3])-1) % 10));
    }

    private void requestServerData() throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        // message_text,branch_location,movie_id
        Message newMessage = new Message(messageId, "get ScreeningTime list");
        newMessage.setData(String.join(",", selectedBranch, selectedMovie.split(",")[0], String.valueOf(forceRefresh)));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ScreeningTime request sent");
    }

    private void requestUpdateScreening(String updatedScreeningTime) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        // message_text,branch_location,movie_id
        Message newMessage = new Message(messageId, "set ScreeningTime");
        newMessage.setData(updatedScreeningTime);
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ScreeningTime update sent");
    }

    public void setSelectedMovie(String selectedMovie, boolean forceRefresh) throws IOException {
        this.forceRefresh = forceRefresh;

        this.selectedMovie = selectedMovie;

        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
        String[] parsedMovie = selectedMovie.split(",(?![^\\[]*\\])");

        String title = parsedMovie[1];
        String description = parsedMovie[2].substring(1, parsedMovie[2].length() - 1);
        String mainActors = parsedMovie[3].substring(1, parsedMovie[3].length() - 1);
        String producerName = parsedMovie[4];

        movieLabel.setText(title);
        movieSummaryLabel.setText(String.format("תקציר: %s", description));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", mainActors));
        producerNameLabel.setText(String.format("מפיק: %s", producerName));

        movieLabel.setTooltip(new Tooltip(title));
        movieSummaryLabel.setTooltip(new Tooltip(description));
        primaryActorsLabel.setTooltip(new Tooltip(mainActors));
        producerNameLabel.setTooltip(new Tooltip(producerName));

        requestServerData();
    }

    @FXML
    void onDateSelected(ActionEvent event) {
        // get selected day
        LocalDate newSelectedDate = screeningDatePicker.getValue();
        // if no changes should be made
        if (selectedDate.equals(newSelectedDate)) return;
        // update selected day
        selectedDate = newSelectedDate;
        // init list
        initializeList();
    }

//    public void initializeDayPicker() {
//        for (String screeningTime : screeningTimes) {
//            // id, day, time, theater.getTheaterID()
//            String dateAsString = screeningTime.split(",")[1];
//            if (!this.availableDatesStrings.contains(dateAsString)){
//                this.availableDatesStrings.add(dateAsString);
//                this.availableDates.add(LocalDate.parse(screeningTime.split(",")[1]));
//            }
//        }
//        Collections.sort(this.availableDatesStrings);
//        selectDayListBox.getItems().addAll(this.availableDatesStrings);
//        this.selectedDate = this.availableDatesStrings.get(0);
//        selectDayListBox.getSelectionModel().selectFirst();
//    }

    public void setAvailableDates() {
        for (String screeningTime : screeningTimes){
            LocalDate screeningDate = LocalDate.parse(screeningTime.split(",")[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!this.availableDates.contains(screeningDate))
                this.availableDates.add(screeningDate);
        }
        Collections.sort(this.availableDates);
        this.selectedDate = this.availableDates.get(0);
    }

    public void initializeDatePicker() {
        screeningDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && item != null) {
                            if (availableDates.contains(item))
                                this.setStyle("-fx-background-color: lightgreen");
                            else
                                this.setDisable(true);
                        }
                    }
                };
            }
        });
        screeningDatePicker.setValue(this.selectedDate);
    }

    public void initializeList() {
        // reset listView
        screeningListView.getItems().clear();
        ArrayList<String> items = new ArrayList<>();
        // filters screenings to only include the selected day
        for (String screeningTime : screeningTimes) {
            // id, day, time, theater.getTheaterID()
            String[] parsedScreening = screeningTime.split(",");
            if (parsedScreening[1].equals(this.selectedDate.toString()))
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
        // get screening time
        String selectedItem = screeningListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) return;

        // get screeningTime object
        int selectedIndex = screeningListView.getSelectionModel().getSelectedIndex();
        String screeningTime = screeningTimes.get(selectedIndex);

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager"))
            onItemSelectedContentManager(selectedItem, selectedIndex, screeningTime);
        // TODO: might need to add a dialog for price changes
        else
            onItemSelectedCustomer(selectedItem, selectedIndex, screeningTime);
    }

    void onItemSelectedCustomer(String selectedItem, int selectedIndex, String screeningTime) throws IOException {
        // TODO: redirect to purchase screen
        InTheaterMoviePurchaseScreenController inTheaterMoviePurchaseScreenController = CinemaClient.setContent("inTheaterMoviePurchaseScreen").getController();
        inTheaterMoviePurchaseScreenController.setSelectedMovie(this.selectedMovie);
        inTheaterMoviePurchaseScreenController.setSelectedBranch(this.selectedBranch);
        inTheaterMoviePurchaseScreenController.setSelectedScreening(screeningTime);

        EventBus.getDefault().unregister(this);
    }

    void onItemSelectedContentManager(String selectedItem, int selectedIndex, String screeningTime) throws IOException {
        StringBuilder mutableScreeningTime = new StringBuilder(screeningTime);

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("screeningEditor", selectedItem, mutableScreeningTime);

        // if change was performed
        if (result == ButtonType.OK) {
            String newScreeningTime = mutableScreeningTime.toString();
            // update ScreeningTime
            if (!screeningTimes.get(selectedIndex).equals(newScreeningTime)) {
                screeningTimes.set(selectedIndex, newScreeningTime);

                LocalDate oldDate = screeningDatePicker.getValue();
                LocalDate newDate = LocalDate.parse(newScreeningTime.split(",")[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // if the new date is not present in the list
                if (!this.availableDates.contains(newDate)) {
                    this.availableDates.add(newDate);
                    initializeDatePicker();
                }

                // if edited screening was the only one for the date, and the date was changed.
                if(!newDate.equals(oldDate) && screeningListView.getItems().size() == 1) {
                    this.availableDates.remove(oldDate);
                    this.selectedDate = newDate;
                    initializeList();
                    initializeDatePicker();
                }
                else
                    this.screeningListView.getItems().remove(selectedItem);

                requestUpdateScreening(newScreeningTime);
            }
        }

        screeningListView.getSelectionModel().clearSelection();
    }

    @Subscribe
    public void onUpdateScreeningTimeEvent(NewScreeningTimeListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                screeningTimes = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                setAvailableDates();
                initializeDatePicker();
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
        // register to EventBus
        EventBus.getDefault().register(this);
        userDataManager = CinemaClient.getUserDataManager();
        forceRefresh = false;
    }

}
