package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerComplaintInfoController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label responseLabel;

    DataParser dataParser;

    private Map<String, String> selectedComplaint;

    public void setSelectedComplaint(Map<String, String> selectedComplaint) throws IOException {
        this.selectedComplaint = selectedComplaint;
        String title = selectedComplaint.get("title").substring(1, selectedComplaint.get("title").length() - 1);
        String content = selectedComplaint.get("complaintContent").substring(1, selectedComplaint.get("complaintContent").length() - 1);
        String response = selectedComplaint.get("response").substring(1, selectedComplaint.get("response").length() - 1);
        titleLabel.setText(title);
        contentLabel.setText(String.format("תוכן התלונה: %s", content));
        responseLabel.setText(String.format("תגובת הסרטייה: %s", response));
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        // get controller
        CinemaClient.setContent("customerComplaintList").getController();
    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
    }

}