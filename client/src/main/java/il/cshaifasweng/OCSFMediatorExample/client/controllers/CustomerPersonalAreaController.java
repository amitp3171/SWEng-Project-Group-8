package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerPersonalAreaController {

    @FXML
    private Label welcomeUserLabel;

    UserDataManager userDataManager;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void onShowPurchases(ActionEvent event) throws IOException {
        CinemaClient.setContent("customerPurchaseList");
    }

    @FXML
    void onShowMessages(ActionEvent event) throws IOException {
        CinemaClient.setContent("customerMessageList");
    }

    @FXML
    void onShowComplaints(ActionEvent event) throws IOException {
        CinemaClient.setContent("customerComplaintList");
    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
        if (userDataManager.isGuest())
            welcomeUserLabel.setText("ברוך הבא אורח!");
        else
            welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));
    }

}