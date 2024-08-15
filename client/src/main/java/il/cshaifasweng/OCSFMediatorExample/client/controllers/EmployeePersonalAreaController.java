package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class EmployeePersonalAreaController {
    @FXML
    private Label welcomeUserLabel;

    @FXML
    private Button complaintListButton;

    @FXML
    private Button showEmployeeRequestsButton;

    @FXML
    private Button reportListButton;

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
    void onShowEmployeeRequests(ActionEvent event) throws IOException {
        CinemaClient.setContent("employeeRequestList");
    }

    @FXML
    void onShowComplaints(ActionEvent event) throws IOException {
        CinemaClient.setContent("serviceEmployeeComplaintList");
    }

    @FXML
    void onShowReports(ActionEvent event) throws IOException {
        CinemaClient.setContent("reportSelection");
    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
        welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));

        if (userDataManager.getEmployeeType().equals("BranchManager")) {
            reportListButton.setVisible(true);
        }

        else if (userDataManager.getEmployeeType().equals("CompanyManager")) {
            reportListButton.setVisible(true);
            showEmployeeRequestsButton.setVisible(true);
        }
    }
}
