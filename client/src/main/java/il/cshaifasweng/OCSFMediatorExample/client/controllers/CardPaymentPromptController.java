package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductPriceEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CardPaymentPromptController implements DialogInterface {

    @FXML
    private Label paymentSumLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField creditCardCVCField;

    @FXML
    private TextField creditCardDateField;

    @FXML
    private TextField creditCardNumberField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button payButton;

    @FXML
    private Button closeDialogButton;

    Dialog<ButtonType> dialog;

    double productPrice;
    int amountOfProducts;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
        dialog.setResult(ButtonType.CANCEL);
    }

    public void setData(Object... items) { // double productPrice, int amountOfProducts
        this.productPrice = (Double) items[0];
        this.amountOfProducts = (int) items[1];
        paymentSumLabel.setText(String.format("סכום לתשלום: %.2f", this.productPrice * this.amountOfProducts));
    }

    @FXML
    void onPayPressed(ActionEvent event) throws IOException {
        if (creditCardNumberField.getText().isEmpty() || creditCardDateField.getText().isEmpty() || creditCardCVCField.getText().isEmpty()) {
            statusLabel.setVisible(true);
            return;
        }
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void cancelPayment(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CANCEL);
        dialog.close();
    }

    @FXML
    void initialize() {

    }

}
