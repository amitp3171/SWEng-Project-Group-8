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

public class CustomerComplaintListController {

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
        CinemaClient.setContent("customerPersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
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
            complaintTitles[i] = complaintTitles[i].substring(1, complaintTitles[i].length() - 1);
        }
        // display movies
        complaintListView.getItems().addAll(complaintTitles);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (complaintListView.getSelectionModel().getSelectedItem() == null) return;

        // get complaint object
        int selectedIndex = complaintListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedComplaint = complaints.get(selectedIndex);

        // set selected complaint
        CustomerComplaintInfoController complaintInfoController = CinemaClient.setContent("customerComplaintInfo").getController();
        complaintInfoController.setSelectedComplaint(selectedComplaint);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        userDataManager = CinemaClient.getUserDataManager();
        getComplaintList();
    }
}

