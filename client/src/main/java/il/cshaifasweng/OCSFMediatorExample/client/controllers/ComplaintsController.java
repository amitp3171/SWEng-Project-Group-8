package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComplaintListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewScreeningTimeListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ComplaintsController {


    private ArrayList<Map<String, String>> complaints = new ArrayList<>();

    private DataParser dataParser;

    @FXML
    private ListView<String> complaintListView;

    @FXML
    private Label welcomeUserLabel;

    UserDataManager userDataManager;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }
    @FXML
    void goToAccount(ActionEvent event) throws IOException {
        CinemaClient.setContent("account");
    }

    @FXML
    void purchases(ActionEvent event) throws IOException {
        CinemaClient.setContent("purchasesList");
    }

    @FXML
    void messages(ActionEvent event) throws IOException {
        CinemaClient.setContent("messagesList");
    }

    @FXML
    void complaints(ActionEvent event) throws IOException {
        CinemaClient.setContent("complaintsList");
    }

    public void getComplaintList() throws IOException {
        // Assuming customer ID is valid and other user data is set
        CinemaClient.sendToServer("get active complaints", userDataManager.getGovId());
    }


    @Subscribe
    public void onGetComplaintListEvent(NewComplaintListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String complaintContent : receivedData) {
                    System.out.println(complaintContent);
                    complaints.add(dataParser.parseComplaint(complaintContent));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("complaintList request received");
        });
    }

    void initializeList() {
        complaintListView.getItems().clear();
        // get movie names
        String[] complaintTitles = new String[complaints.size()];
        for (int i = 0; i < complaintTitles.length; i++) {
            complaintTitles[i] = complaints.get(i).get("title");
        }
        // display movies
        complaintListView.getItems().addAll(complaintTitles);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        userDataManager = CinemaClient.getUserDataManager();
        getComplaintList();
       /* if (userDataManager.isGuest())
            welcomeUserLabel.setText("ברוך הבא אורח!");
        else
            welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));
    */
        }
}

