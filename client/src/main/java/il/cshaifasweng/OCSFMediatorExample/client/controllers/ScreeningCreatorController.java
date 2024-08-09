package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

    private ArrayList<String> availableMovies;
    private String branchLocation;
    private ArrayList<String> theaters;

    public void setData(Object... items) /*ArrayList<String> availableMovies, String branchLocation*/ {
        this.availableMovies = (ArrayList<String>) items[0];
        this.branchLocation = (String) items[1];

        String[] movieNames = new String[availableMovies.size()];

        for (int i = 0; i < availableMovies.size(); i++)
            movieNames[i] = availableMovies.get(i).split(",")[1];

        inTheaterMovieChoiceBox.getItems().addAll(movieNames);

        // request theater list from server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get Theater ID list");
        newMessage.setData(branchLocation);
        try {
            CinemaClient.getClient().sendToServer(newMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Theater ID request sent");
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

    // TODO: handle case when screeningtime already exists

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
        System.out.println(selectedDate);
        String selectedTime = screeningTimePromptTF.getText();
        String selectedTheaterId = theaters.get(theaterChoiceBox.getSelectionModel().getSelectedIndex());
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "create ScreeningTime");
        // branch, date, time, theater, movie
        newMessage.setData(String.format("%s,%s,%s,%s,%s",
                branchLocation,
                selectedDate,
                selectedTime,
                selectedTheaterId,
                availableMovies.get(inTheaterMovieChoiceBox.getSelectionModel().getSelectedIndex()).split(",")[0]));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("create ScreeningTime request sent");
    }

    @FXML
    void onDateSelected(ActionEvent event) {}

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
        theaterChoiceBox.getItems().addAll(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
    }

}
