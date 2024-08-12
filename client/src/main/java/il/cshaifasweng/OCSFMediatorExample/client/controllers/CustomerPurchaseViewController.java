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
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComplaintListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductDetailsEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerPurchaseViewController implements DialogInterface {

    @FXML
    private Label paymentMethodLabel;

    @FXML
    private Label purchaseDateLabel;

    @FXML
    private Label productDescLabel;

    @FXML
    private Button refundButton;

    Dialog<ButtonType> dialog;

    DataParser dataParser;

    Map<String, String> selectedPurchase;

    Map<String, String> relatedProduct;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.selectedPurchase = (Map<String, String>) params[0];
        System.out.println(selectedPurchase.toString());
        purchaseDateLabel.setText(String.join(" ,", selectedPurchase.get("paymentTime"), selectedPurchase.get("paymentDate")));
        if (selectedPurchase.get("paymentMethod").equals("Credit Card"))
            paymentMethodLabel.setText("כרטיס אשראי");
        else
            paymentMethodLabel.setText("כרטיסייה");
        try {
            CinemaClient.sendToServer("request Product details", selectedPurchase.get("relatedProductId"));
        } catch (IOException e) {System.out.println(e.getMessage());};
    }

    @Subscribe
    public void onGetProductDetailsEvent(NewProductDetailsEvent event) {
        // on event received
        Platform.runLater(() -> {
            String productClass = event.getMessage().getData().split(",")[0];
            String productToString = event.getMessage().getData().substring(productClass.length() + 1);
            Map<String, String> productMap;
            switch (productClass) {
                case "Ticket":
                    productMap = dataParser.parseTicket(productToString);
                    productDescLabel.setText(
                            String.format("כרטיס: %s, אולם %s, מושב %s, %s, %n %s, סניף %s",
                                    productMap.get("movieName"),
                                    (1 + (Integer.parseInt(productMap.get("theaterId"))-1 % 10)),
                                    productMap.get("seatNumber"),
                                    productMap.get("screeningTime"),
                                    productMap.get("screeningDate"),
                                    productMap.get("branchLocation")));
                    System.out.println(productDescLabel.getText());
                    break;
                case "SubscriptionCard":
                    productMap = dataParser.parseSubscriptionCard(productToString);
                    productDescLabel.setText(
                            String.format("כרטיסייה: נותרו %s כרטיסים",
                            productMap.get("remainingTickets")));
                    break;
                case "Link":
                    // TODO: adjust based on expiry time
                    productMap = dataParser.parseLink(productToString);
                    LocalDate availableDate = LocalDate.parse(productMap.get("availableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime availableTime = LocalTime.parse(productMap.get("availableTime"), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    LocalTime expiresAt = LocalTime.parse(productMap.get("expiresAt"), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    if (availableDate.isEqual(LocalDate.now()) && LocalTime.now().isBefore(expiresAt) && LocalTime.now().isAfter(availableTime)) {
                        productDescLabel.setText(
                                String.format("קישור: %s, זמינות עד השעה %s", productMap.get("link"), productMap.get("expiresAt"))
                        );
                    }
                    else {
                        productDescLabel.setText(String.format("קישור: זמין ב-%s, %s", availableDate, availableTime));
                    }
                    break;
            }

            System.out.println("Product details request received");
        });
    }

    @FXML
    void onCancelPressed(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.close();
    }

    @FXML
    void onOkPressed(ActionEvent event) {
        this.onCancelPressed(event);
    }

    @FXML
    void onRefundRequested(ActionEvent event) {

    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
        EventBus.getDefault().register(this);
    }

}
