package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Map;

public class HandleComplaintController implements DialogInterface{

    private Dialog<ButtonType> dialog;

    private Map<String, String> selectedComplaint;

    @FXML
    private TextField refundAmount;

    @FXML
    private TextField response;

    @FXML
    private Label statusLabel;

    public void setData(Object... items) {
        this.selectedComplaint = (Map<String, String>) items[0];
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    @FXML
    public void onSendResponse(ActionEvent event) throws IOException {

        String refundAmount = this.refundAmount.getText();
        String response = this.response.getText();
        // send request to server
        if(isPositiveInteger(refundAmount) && !(response.isEmpty())) {
            CinemaClient.sendToServer("handle complaint", String.join(",", selectedComplaint.get("id"), refundAmount, response));
            dialog.setResult(ButtonType.OK);
            dialog.close();
        }
        else if(!isPositiveInteger(refundAmount)) {
            statusLabel.setText("סכום הזיכוי חייב להיות מספר שלם!");
            statusLabel.setVisible(true);
        }
        else {
            statusLabel.setText("נא למלא את שדה התשובה ללקוח");
            statusLabel.setVisible(true);
        }
    }

    public boolean isPositiveInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        dialog.setResult(ButtonType.CANCEL);
        dialog.close();
    }



}
