package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewScreeningTimeListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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

    DataParser dataParser;

    private String selectedBranch;
    private Map<String, String> selectedMovie;

    private ArrayList<LocalDate> availableDates = new ArrayList<>();
    private LocalDate selectedDate;

    private ArrayList<Map<String, String>> screeningTimes = new ArrayList<>();
    private ArrayList<Map<String, String>> visibleScreeningTimes = new ArrayList<>();

    private boolean forceRefresh;

    public void setSelectedBranch(String branch) {
        this.selectedBranch = branch;
    }

    private String concatTimeTheater(Map<String, String> screeningTime) {
        // concat screening time with screening theater
        return String.format("%s, אולם %s", screeningTime.get("time"), (1 + (Integer.parseInt(screeningTime.get("theaterId"))-1) % 10));
    }

    private void requestServerData() throws IOException {
        CinemaClient.sendToServer("get ScreeningTime list", String.join(",", selectedBranch, selectedMovie.get("id"), String.valueOf(forceRefresh)));
    }

    private void requestUpdateScreening(Map<String, String> updatedScreeningTime) throws IOException {
        CinemaClient.sendToServer("set ScreeningTime", String.join(",", updatedScreeningTime.get("id"), updatedScreeningTime.get("date"), updatedScreeningTime.get("time"), updatedScreeningTime.get("theaterId")));
    }

    public void setSelectedMovie(Map<String, String> selectedMovie, boolean forceRefresh) throws IOException {
        this.forceRefresh = forceRefresh;

        this.selectedMovie = selectedMovie;

        String title = selectedMovie.get("movieName");
        String description = selectedMovie.get("description").substring(1, selectedMovie.get("description").length() - 1);
        String mainActors = selectedMovie.get("mainActors").substring(1, selectedMovie.get("mainActors").length() - 1);
        String producerName = selectedMovie.get("producerName");

        String encodedImage = selectedMovie.get("picture");

        byte[] decodedBytes = java.util.Base64.getDecoder().decode(encodedImage);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        Image movieImage = new Image(bis);

        movieImageView.setImage(movieImage);
        movieImageView.setScaleX(-1);


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
        LocalDate newSelectedDate = screeningDatePicker.getValue();

        if (newSelectedDate == null) return;
        // if no changes should be made
        if (selectedDate.equals(newSelectedDate)) return;
        // update selected day
        selectedDate = newSelectedDate;
        // init list
        initializeList();
    }

    public void setAvailableDates() {
        for (Map<String, String> screeningTime : screeningTimes){
            LocalDate screeningDate = LocalDate.parse(screeningTime.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (!this.availableDates.contains(screeningDate))
                this.availableDates.add(screeningDate);
        }
        Collections.sort(this.availableDates);
        this.selectedDate = this.availableDates.isEmpty() ? null : this.availableDates.get(0);
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
        visibleScreeningTimes.clear();

        ArrayList<String> items = new ArrayList<>();
        // filters screenings to only include the selected day
        for (Map<String, String> screeningTime : screeningTimes) {
            if (screeningTime.get("date").equals(this.selectedDate.toString())) {
                items.add(concatTimeTheater(screeningTime));
                visibleScreeningTimes.add(screeningTime);
            }
        }

        if (items.isEmpty())
            items.add("אין הקרנות");
        else
            Collections.sort(items);
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
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
        EventBus.getDefault().unregister(this);
    }
    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // get screening time
        String selectedItem = screeningListView.getSelectionModel().getSelectedItem();

        if (selectedItem == null || this.selectedDate == null) return;

        // get screeningTime object
        int selectedIndex = screeningListView.getSelectionModel().getSelectedIndex();
        Map<String, String> screeningTime = visibleScreeningTimes.get(selectedIndex);

        if (CinemaClient.getUserDataManager().isEmployee() && CinemaClient.getUserDataManager().getEmployeeType().equals("ContentManager"))
            onItemSelectedContentManager(selectedItem, selectedIndex, screeningTime);
        else
            onItemSelectedCustomer(screeningTime);
    }

    void onItemSelectedCustomer(Map<String, String> screeningTime) throws IOException {
        InTheaterMoviePurchaseScreenController inTheaterMoviePurchaseScreenController = CinemaClient.setContent("inTheaterMoviePurchaseScreen").getController();
        inTheaterMoviePurchaseScreenController.setSelectedMovie(this.selectedMovie);
        inTheaterMoviePurchaseScreenController.setSelectedBranch(this.selectedBranch);
        inTheaterMoviePurchaseScreenController.setSelectedScreening(screeningTime);

        EventBus.getDefault().unregister(this);
    }

    void onItemSelectedContentManager(String selectedItem, int selectedIndex, Map<String, String> selectedScreeningTime) throws IOException {

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("screeningEditor", selectedItem, selectedScreeningTime, this.selectedBranch, this.screeningTimes);

        LocalDate oldDate = screeningDatePicker.getValue();

        // if change was performed
        if (result == ButtonType.OK) {
            LocalDate newDate = LocalDate.parse(selectedScreeningTime.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // if edited screening was the only one for the date, and the date was changed.
            if(!newDate.equals(oldDate)) {
                // if the new date is not present in the list
                if (!this.availableDates.contains(newDate))
                    this.availableDates.add(newDate);

                if (screeningListView.getItems().size() == 1) {
                    this.availableDates.remove(oldDate);
                    this.selectedDate = newDate;
                    initializeList();
                }
                else {
                    this.screeningListView.getItems().remove(selectedItem);
                    visibleScreeningTimes.remove(selectedIndex);
                }

                initializeDatePicker();
            }
            else {
                screeningListView.getItems().set(selectedIndex, concatTimeTheater(selectedScreeningTime));
                Collections.sort(screeningListView.getItems());
            }

            requestUpdateScreening(selectedScreeningTime);
        }
        // if screeningtime is to be deleted
        else if (result == ButtonType.FINISH) {
            CinemaClient.sendToServer("remove ScreeningTime", this.visibleScreeningTimes.get(selectedIndex).get("id"));

            screeningTimes.remove(selectedScreeningTime);

            if (screeningListView.getItems().size() == 1) {
                this.availableDates.remove(oldDate);
                this.selectedDate = this.availableDates.isEmpty() ? null : this.availableDates.get(0);
                initializeList();
                initializeDatePicker();
            }
            else {
                this.screeningListView.getItems().remove(selectedItem);
                visibleScreeningTimes.remove(selectedIndex);
            }
        }

        screeningListView.getSelectionModel().clearSelection();
    }

    @Subscribe
    public void onUpdateScreeningTimeEvent(NewScreeningTimeListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String screeningTime : receivedData)
                    screeningTimes.add(dataParser.parseScreeningTime(screeningTime));

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
        dataParser = CinemaClient.getDataParser();
        // register to EventBus
        EventBus.getDefault().register(this);
        forceRefresh = false;
    }

}
