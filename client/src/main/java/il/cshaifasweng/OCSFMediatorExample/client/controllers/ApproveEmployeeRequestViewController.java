package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ApproveEmployeeRequestViewController implements DialogInterface {

    @FXML
    private Label newPriceLabel;

    @FXML
    private ComboBox<String> selectActionComboBox;

    @FXML
    private Button updatePriceRequestButton;

    @FXML
    private Label statusLabel;

    Dialog<ButtonType> dialog;

    ButtonType status = ButtonType.CANCEL;

    Map<String, String> selectedRequest;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.selectedRequest = (Map<String, String>) params[0];
        newPriceLabel.setText(
                "מוצר: " +
                        parseProductSelection() +
                        ", מחיר: " +
                        selectedRequest.get("newPrice")
        );
    }

    private String parseProductSelection() {
        switch (selectedRequest.get("selectedProduct")) {
            case "Ticket":
                return "כרטיס";
            case "SubscriptionCard":
                return "כרטיסייה";
            default:
                return "חבילת צפייה ביתית";
        }
    }

    private String parseActionSelection() {
        switch (selectActionComboBox.getSelectionModel().getSelectedItem()) {
            case "אישור":
                return "accept";
            default:
                return "reject";
        }
    }

    @FXML
    void chooseAction(ActionEvent event) {
        if (!selectedRequest.get("status").equals("pending")) return;
        updatePriceRequestButton.setDisable(false);
    }

    @FXML
    void onCancel(ActionEvent event) {
        dialog.setResult(this.status);
        dialog.close();
    }

    @FXML
    void onUpdatePriceRequest(ActionEvent event) throws IOException {
        CinemaClient.sendToServer("update PriceChangeRequest", String.join(",", selectedRequest.get("id"), parseActionSelection()));
        selectedRequest.replace("status", parseActionSelection() + "ed");
        statusLabel.setText("סטטוס " + selectActionComboBox.getSelectionModel().getSelectedItem() + " נשלח בהצלחה");
        statusLabel.setVisible(true);
        status = ButtonType.OK;
        updatePriceRequestButton.setDisable(true);
    }

    @FXML
    void initialize() {
        selectActionComboBox.getItems().addAll("אישור", "דחייה");
    }

}
