package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
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
                // TODO: sorting the listbox might be necessary, however we are about to replace it with a datepicker..
                String oldDate = selectDayListBox.getSelectionModel().getSelectedItem();
                String newDate = newScreeningTime.split(",")[1];
                // if the new date is not present in the list
                if (!selectDayListBox.getItems().contains(newDate))
                    selectDayListBox.getItems().add(newDate);
                // if edited screening was the only one for the date, and the date was changed.
                if(!newDate.equals(oldDate) && screeningListView.getItems().size() == 1) {
                    this.availableDates.remove(oldDate);
                    this.availableDates.add(newDate);
                    selectDayListBox.getItems().remove(oldDate);
                    this.selectedDate = newDate;
                    selectDayListBox.getSelectionModel().select(newDate);
                    initializeList();
                }
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
        // register to EventBus
        EventBus.getDefault().register(this);
        userDataManager = CinemaClient.getUserDataManager();
        forceRefresh = false;
    }

}
