package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ServiceEmployeeComplaintInfoController {
    @FXML
    private Label titleLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label complaintStatus;

    @FXML
    private Button handleComplaintButton;

    UserDataManager userDataManager;

    DataParser dataParser;

    private Map<String, String> selectedComplaint;



    public void setSelectedComplaint(Map<String, String> selectedComplaint) throws IOException {
        this.selectedComplaint = selectedComplaint;
        String title = selectedComplaint.get("title").substring(1, selectedComplaint.get("title").length() - 1);
        String content = selectedComplaint.get("complaintContent").substring(1, selectedComplaint.get("complaintContent").length() - 1);
        titleLabel.setText(title);
        contentLabel.setText(String.format("תוכן התלונה: %s", content));

    }

    public void setComplaintStatus(String selectedComplaintStatus) {
        if(selectedComplaint.get("response").equals("[טרם התקבלה תגובה מהצוות]")) {
            if (selectedComplaintStatus.equals("0")) {
                complaintStatus.setText("חריגה מזמן הטיפול בתלונה!");
            }
            else {
                complaintStatus.setText("הזמן הנותר לטיפול בתלונה: " + selectedComplaintStatus);
            }
        }
        else {
            complaintStatus.setText("הפנייה טופלה");
            handleComplaintButton.setDisable(true);
        }

    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        // get controller
        CinemaClient.setContent("serviceEmployeeComplaintList").getController();
    }

    @FXML
    void onHandleComplaint(ActionEvent event) throws IOException {
        //creat dialog
        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("handleComplaint", selectedComplaint);

        if(result == ButtonType.OK) {
            complaintStatus.setText("הפנייה טופלה");
            handleComplaintButton.setDisable(true);
        }

    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
        userDataManager = CinemaClient.getUserDataManager();
        if(userDataManager.getEmployeeType().equals("ServiceEmployee"))
            handleComplaintButton.setVisible(true);
    }

}

