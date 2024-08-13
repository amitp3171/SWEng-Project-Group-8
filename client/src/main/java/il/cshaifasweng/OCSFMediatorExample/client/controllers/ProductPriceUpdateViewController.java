package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductPriceUpdateViewController implements DialogInterface {

    @FXML
    private TextField newPriceField;

    @FXML
    private ComboBox<String> selectProductComboBox;

    @FXML
    private Button updateProductPriceButton;

    @FXML
    private Label statusLabel;

    Dialog<ButtonType> dialog;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        //
    }

    private boolean inputIsValid() {
        String customerId = newPriceField.getText();
        return !(
                // if blank
                customerId.isBlank() ||
                // should be of numeric type
                !customerId.matches("-?\\d+(\\.\\d+)?")
        );
    }

    private String parseProductSelection() {
        switch (selectProductComboBox.getSelectionModel().getSelectedItem()) {
            case "כרטיס":
                return "Ticket";
            case "כרטיסייה":
                return "SubscriptionCard";
            default:
                return "Link";
        }
    }

    @FXML
    void cancelSelectBranch(ActionEvent event) {
        dialog.close();
    }

    @FXML
    void onProductSelected(ActionEvent event) {
        updateProductPriceButton.setDisable(false);
    }

    @FXML
    void onUpdateProductPrice(ActionEvent event) throws IOException {
        if (!inputIsValid() || Double.parseDouble(newPriceField.getText()) <= 0) {
            statusLabel.setText("פורמט קלט אינו תקין");
            statusLabel.setVisible(true);
            return;
        }

        if (CinemaClient.getUserDataManager().getEmployeeType().equals("CompanyManager")) {
            CinemaClient.sendToServer("update Product price", String.join(",", parseProductSelection(), newPriceField.getText()));
            statusLabel.setText("מחיר שונה בהצלחה");
            statusLabel.setVisible(true);
        }
        else {
            // TODO: send change price request to company manager
        }
    }

    @FXML
    void initialize() {
        selectProductComboBox.getItems().setAll(new String[] {"כרטיס", "כרטיסייה", "חבילת צפייה ביתית"});
    }

}
