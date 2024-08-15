package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReportSelectionController {

    @FXML
    private Button showSubscriptionCardLinkReportButton;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("employeePersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void onShowTicketReport(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("ticketReportView");
    }

    @FXML
    void onShowSubscriptionCardLinkReport(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("subscriptionCardLinkReportView");
    }

    @FXML
    void onShowComplaintReport(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("complaintReportView");
    }

    @FXML
    void initialize() {
        if (CinemaClient.getUserDataManager().getEmployeeType().equals("CompanyManager"))
            showSubscriptionCardLinkReportButton.setVisible(true);
    }
}
