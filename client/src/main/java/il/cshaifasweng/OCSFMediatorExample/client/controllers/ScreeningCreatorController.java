package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewTheaterIdListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewTheaterListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ScreeningCreatorController {

    @FXML
    private Label movieNameLabel;

    @FXML
    private TextField screeningDatePromptTF;

    @FXML
    private TextField screeningTimePromptTF;

    @FXML
    private ChoiceBox<String> theaterChoiceBox;

    private Dialog<ButtonType> dialog;

    private String movie;
    private String branchLocation;
    private ArrayList<String> theaters;

    public void setData(String movie, String branchLocation) throws IOException {
        this.movie = movie;
        this.branchLocation = branchLocation;

        movieNameLabel.setText(movie.split(",")[1]);

        // request theater list from server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get Theater ID list");
        newMessage.setData(branchLocation);
        CinemaClient.getClient().sendToServer(newMessage);
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
        String selectedDate = screeningDatePromptTF.getText();
        String selectedTime = screeningTimePromptTF.getText();
        // TODO: Modify
        String selectedTheaterId = theaters.get(theaterChoiceBox.getSelectionModel().getSelectedIndex());
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "create ScreeningTime");
        // branch, date, time, theater, movie
        newMessage.setData(String.format("%s,%s,%s,%s,%s", branchLocation, selectedDate, selectedTime, selectedTheaterId, movie.split(",")[0]));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("create ScreeningTime request sent");
    }

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
        theaterChoiceBox.getItems().addAll(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
    }

}
