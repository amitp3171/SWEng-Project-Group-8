package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;


public class CustomerComplaintSubmissionView implements DialogInterface {

    DataParser dataParser;

    Map<String,String> relatedPurchase;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField complaintContentField;

    @FXML
    private TextField complaintTitleField;

    @FXML
    private Button sendComplaintButton;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;


    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.relatedPurchase = (Map<String,String>)params[0];
    }

    @FXML
    void cancelLogin(ActionEvent event) {
        dialog.close();
    }

    @FXML
    void onSendComplaint(ActionEvent event) throws IOException {
        if (complaintTitleField.getText().isEmpty() || complaintContentField.getText().isEmpty()) {
            statusLabel.setText("נא למלא את כל השדות");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
        }
        else {

            CinemaClient.sendToServer("submit Customer Complaint", String.format("%s,[%s],[%s], %s", CinemaClient.getUserDataManager().getId(), complaintTitleField.getText(), complaintContentField.getText(), relatedPurchase.get("id")));
            statusLabel.setText("תלונה נשלחה בהצלחה");
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setVisible(true);
            sendComplaintButton.setDisable(true);
        }
    }

    @FXML
    void initialize() {
    }

}
