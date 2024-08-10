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

    boolean resultOK;

    Map<String, String> selectedScreeningTime;
    ArrayList<String> selectedSeatIds;
    double ticketPrice;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
        dialog.setResult(ButtonType.CANCEL);
    }

    public void setData(Object... items) { // Map<String,String> selectedScreeningTime, ArrayList<String> selectedSeatIds
        this.selectedScreeningTime = (Map<String, String>) items[0];
        this.selectedSeatIds = (ArrayList<String>) items[1];
        try {
            CinemaClient.sendToServer("get Ticket price");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cancelPayment(ActionEvent event) {
        EventBus.getDefault().unregister(this);

        if (resultOK)
            dialog.setResult(ButtonType.OK);
        else
            dialog.setResult(ButtonType.CANCEL);

        dialog.close();
    }

    @FXML
    void onPayPressed(ActionEvent event) throws IOException {
        if (creditCardNumberField.getText().isEmpty() || creditCardDateField.getText().isEmpty() || creditCardCVCField.getText().isEmpty()) {
            statusLabel.setVisible(true);
            return;
        }
        statusLabel.setVisible(false);

        // TODO: if this were a real payment, we'd pass the card details here.
        CinemaClient.sendToServer("create Ticket Purchase", String.join(",", CinemaClient.getUserDataManager().getGovId(), selectedScreeningTime.get("id"), selectedSeatIds.toString(), String.valueOf(this.ticketPrice)));
    }

    @Subscribe
    public void onUpdateProductPrice(NewProductPriceEvent event) {
        // on event received
        Platform.runLater(() -> {
            this.ticketPrice = Double.parseDouble(event.getMessage().getData());
            paymentSumLabel.setText(String.format("סכום לתשלום: %.2f", this.ticketPrice*this.selectedSeatIds.size()));
            System.out.println("update Seats list request received");
        });
    }

    @Subscribe
    public void onUpdateProductPrice(NewPurchaseStatusEvent event) {
        // on event received
        Platform.runLater(() -> {
            String status = event.getMessage().getData();

            if (status.equals("payment successful")) {
                statusLabel.setText("תשלום בוצע בהצלחה, ניתן לראות את הרכישה באיזור האישי");
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setVisible(true);

                resultOK = true;

                // disable all fields except exit
                creditCardNumberField.setDisable(true);
                creditCardCVCField.setDisable(true);
                creditCardDateField.setDisable(true);
                payButton.setDisable(true);
                cancelButton.setDisable(true);
                closeDialogButton.setVisible(true);
            }
        });
    }

    @FXML
    void initialize() {
        resultOK = false;
        EventBus.getDefault().register(this);
    }

}
