package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCreatedScreeningTimeEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewTheaterIdListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewTheaterListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ScreeningCreatorController implements DialogInterface {

    @FXML
    private ChoiceBox<String> inTheaterMovieChoiceBox;

    @FXML
    private DatePicker screeningDatePicker;

    @FXML
    private TextField screeningTimePromptTF;

    @FXML
    private ChoiceBox<String> theaterChoiceBox;

    @FXML
    private Label creationStatusLabel;

    private Dialog<ButtonType> dialog;

    private ArrayList<Map<String, String>> availableMovies;
    private String branchLocation;
    private ArrayList<String> theaters;

    public void setData(Object... items) /*ArrayList<Map<String, String>> availableMovies, String branchLocation*/ {
        this.availableMovies = (ArrayList<Map<String, String>>) items[0];
        this.branchLocation = (String) items[1];

        String[] movieNames = new String[availableMovies.size()];

        for (int i = 0; i < availableMovies.size(); i++)
            movieNames[i] = availableMovies.get(i).get("movieName");

        inTheaterMovieChoiceBox.getItems().addAll(movieNames);

        // request theater list from server
        try {
            CinemaClient.sendToServer("get Theater ID list", branchLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onUpdateBranchEvent(NewTheaterIdListEvent event) {
        Platform.runLater(() -> {
            try {
                theaters = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //TODO
    @Subscribe
    public void onCreateScreeningTimeEvent(NewCreatedScreeningTimeEvent event) {
        Platform.runLater(() -> {
            String status = event.getMessage().getData();
            if (status.equals("request successful"))
                creationStatusLabel.setText("הקרנה נוצרה בהצלחה");
            else
                creationStatusLabel.setText("שגיאה (הקרנה קיימת)");
            creationStatusLabel.setVisible(true);
        });
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    @FXML
    void cancelScreeningCreation(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void createScreeningHour(ActionEvent event) throws IOException {
        // get text-field input
        String selectedDate = screeningDatePicker.getValue().toString();
        String selectedTime = screeningTimePromptTF.getText();

        //check if a movie has been selected
        if(inTheaterMovieChoiceBox.getSelectionModel().isEmpty()) {
            creationStatusLabel.setText("יש לבחור סרט");
            creationStatusLabel.setVisible(true);
            return;
        }

        // Check if a theater has been selected
        if (theaterChoiceBox.getSelectionModel().isEmpty()) {
            creationStatusLabel.setText("יש לבחור אולם");
            creationStatusLabel.setVisible(true);
            return;
        }

        String selectedTheaterId = theaters.get(theaterChoiceBox.getSelectionModel().getSelectedIndex());

        // Validate the input time format
        if (!isValidTime(selectedTime)) {
            creationStatusLabel.setText("שעה לא תקינה");
            creationStatusLabel.setVisible(true);
            return;
        }

        // Check if the selected date is today
        if (screeningDatePicker.getValue().isEqual(LocalDate.now())) {
            LocalTime currentTime = LocalTime.now();
            LocalTime selectedTimeParsed = LocalTime.parse(selectedTime, DateTimeFormatter.ofPattern("HH:mm"));

            // Ensure the selected time is not earlier than the current time
            if (selectedTimeParsed.isBefore(currentTime)) {
                creationStatusLabel.setText("לא ניתן ליצור הקרנה למועד שכבר עבר");
                creationStatusLabel.setVisible(true);
                return;
            }
        }

        // send request to server
        CinemaClient.sendToServer("create ScreeningTime",
                String.join(",",
                        branchLocation,
                        selectedDate,
                        selectedTime,
                        selectedTheaterId,
                        availableMovies.get(inTheaterMovieChoiceBox.getSelectionModel().getSelectedIndex()).get("id"))
                );
    }

    private boolean isValidTime(String time) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime.parse(time, timeFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @FXML
    void onDateSelected(ActionEvent event) {}

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
        theaterChoiceBox.getItems().addAll(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});

        // Set DatePicker to start from the current day
        screeningDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Optionally, color past dates
                }
            }
        });
        screeningDatePicker.setValue(LocalDate.now()); // Set default value to current date
    }

}
