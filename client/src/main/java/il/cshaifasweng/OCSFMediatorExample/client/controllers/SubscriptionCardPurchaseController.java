package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductPriceEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SubscriptionCardPurchaseController implements DialogInterface {

    @FXML
    private Label paymentStatusLabel;

    @FXML
    private ComboBox<Integer> selectAmountListBox;

    private double productPrice;

    Dialog<ButtonType> dialog;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {

    }

    @FXML
    void cancelPurchase(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.close();
    }

    @FXML
    void onPayment(ActionEvent event) throws IOException {
        ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("cardPaymentPrompt", this.productPrice, selectAmountListBox.getSelectionModel().getSelectedItem());

        if (status == ButtonType.OK) {
            // TODO: if this were a real payment, we'd pass the card details here.
            CinemaClient.sendToServer("create SubscriptionCard Purchase", String.join(",", CinemaClient.getUserDataManager().getGovId(), String.valueOf(selectAmountListBox.getSelectionModel().getSelectedItem()), String.valueOf(this.productPrice)));
        }
        else {
            paymentStatusLabel.setText("תשלום בוטל");
            paymentStatusLabel.setTextFill(Color.RED);
            paymentStatusLabel.setVisible(true);
        }
    }

    @Subscribe
    public void onPurchaseStatusUpdate(NewPurchaseStatusEvent event) {
        // on event received
        Platform.runLater(() -> {
            String status = event.getMessage().getData();
            if (status.equals("payment successful")) {
                paymentStatusLabel.setText("תשלום בוצע בהצלחה, ניתן לראות את הרכישה באיזור האישי");
                paymentStatusLabel.setTextFill(Color.GREEN);
                paymentStatusLabel.setVisible(true);
            }
        });
    }

    @Subscribe
    public void onUpdateProductPrice(NewProductPriceEvent event) {
        // on event received
        Platform.runLater(() -> {
            this.productPrice = Double.parseDouble(event.getMessage().getData());
            System.out.println("update Price request received");
        });
    }

    @FXML
    void initialize() throws IOException {
        selectAmountListBox.getItems().addAll(new Integer[] {1,2,3,4,5,6,7,8,9,10});
        EventBus.getDefault().register(this);
        CinemaClient.sendToServer("get Product price", "SubscriptionCard");
    }

}
