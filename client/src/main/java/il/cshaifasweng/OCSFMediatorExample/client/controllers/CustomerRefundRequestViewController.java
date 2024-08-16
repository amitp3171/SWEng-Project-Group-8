package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComplaintListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerRefundRequestViewController implements DialogInterface {

    @FXML
    private Label refundStatusLabel;

    @FXML
    private Label refundAmountField;

    @FXML
    private Button refundRequestButton;

    private Dialog<ButtonType> dialog;

    private String productClass;

    private Map<String, String> relatedProduct;

    private ButtonType refundStatus = ButtonType.CANCEL;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
        this.dialog.setOnCloseRequest(event -> {
            // X button acts as cancel button
            dialog.setResult(refundStatus);
        });
    }

    public void setData(Object... params) {
        productClass = (String) params[0];
        relatedProduct = (Map<String, String>) params[1];
        setLabelText();
    }

    private void setLabelText() {
        switch (productClass) {
            case "Ticket":
                LocalTime screeningTime = LocalTime.parse(this.relatedProduct.get("screeningTime"), DateTimeFormatter.ofPattern("HH:mm"));
                LocalDate screeningDate = LocalDate.parse(this.relatedProduct.get("screeningDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                double refundAmount = Double.parseDouble(this.relatedProduct.get("price"));

                if (LocalDate.now().equals(screeningDate)) {
                    if (LocalTime.now().plusHours(1).isBefore(screeningTime) && LocalTime.now().plusHours(3).isAfter(screeningTime))
                        refundAmount /= 2;
                    else if (LocalTime.now().plusHours(1).isAfter(screeningTime))
                        refundAmount = 0;
                }

                refundAmountField.setText(String.format("גובה ההחזר: %s", refundAmount));
                break;
            case "Link":
                refundAmountField.setText(String.format("גובה ההחזר: %s", Double.parseDouble(this.relatedProduct.get("price"))/2));
        }
    }

    @FXML
    void cancelRefundRequest(ActionEvent event) {
//        EventBus.getDefault().unregister(this);
        dialog.setResult(this.refundStatus);
        dialog.close();
    }

    @FXML
    void confirmRefundRequest(ActionEvent event) throws IOException {
        switch (this.productClass) {
            case "Ticket":
                CinemaClient.sendToServer("Customer Ticket Refund", this.relatedProduct.get("id"));
                break;
            case "Link":
                CinemaClient.sendToServer("Customer Link Refund", this.relatedProduct.get("id"));
                break;
        }

        refundStatusLabel.setText("החזר כספי בוצע בהצלחה");
        refundStatusLabel.setVisible(true);
        refundRequestButton.setDisable(true);
        this.refundStatus = ButtonType.OK;
    }

//    @Subscribe
//    public void onGetCustomerPurchaseListEvent(NewComplaintListEvent event) {
//        // on event received
//        Platform.runLater(() -> {
////            try {
////                customerPurchases.clear();
////                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
////                for (String purchase : receivedData) {
////                    customerPurchases.add(dataParser.parsePurchase(purchase));
////                }
////            }
////            catch (IOException e) {
////                e.printStackTrace();
////            }
////            // update list
////            initializeList();
////            System.out.println("Customer Purchase list request received");
//        });
//    }

    @FXML
    void initialize() {
//        EventBus.getDefault().register(this);
    }

}
