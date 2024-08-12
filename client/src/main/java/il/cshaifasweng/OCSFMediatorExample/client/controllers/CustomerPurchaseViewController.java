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
import javafx.scene.control.*;
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

    String productClass;

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
            this.productClass = event.getMessage().getData().split(",")[0];
            String productToString = event.getMessage().getData().substring(this.productClass.length() + 1);
            switch (this.productClass) {
                case "Ticket":
                    this.relatedProduct = dataParser.parseTicket(productToString);
                    productDescLabel.setText(
                            String.format("כרטיס: %s, אולם %s, מושב %s, %s, %n %s, סניף %s",
                                    this.relatedProduct.get("movieName"),
                                    (1 + (Integer.parseInt(this.relatedProduct.get("theaterId"))-1 % 10)),
                                    this.relatedProduct.get("seatNumber"),
                                    this.relatedProduct.get("screeningTime"),
                                    this.relatedProduct.get("screeningDate"),
                                    this.relatedProduct.get("branchLocation")));
                    break;
                case "SubscriptionCard":
                    this.relatedProduct = dataParser.parseSubscriptionCard(productToString);
                    productDescLabel.setText(
                            String.format("כרטיסייה: נותרו %s כרטיסים",
                            this.relatedProduct.get("remainingTickets")));
                    refundButton.setDisable(true);
                    refundButton.setText("אין אפשרות לקבל החזר כספי על כרטיסיות");
                    break;
                case "Link":
                    // TODO: adjust based on expiry time
                    this.relatedProduct = dataParser.parseLink(productToString);
                    LocalDate availableDate = LocalDate.parse(this.relatedProduct.get("availableDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalTime availableTime = LocalTime.parse(this.relatedProduct.get("availableTime"), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    LocalTime expiresAt = LocalTime.parse(this.relatedProduct.get("expiresAt"), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    if (LocalTime.now().plusHours(1).isAfter(availableTime)) {
                        refundButton.setText("ניתן לבטל עד שעה לפני מועד הפעלת הקישור");
                        refundButton.setDisable(true);
                    }
                    if (availableDate.isEqual(LocalDate.now()) && LocalTime.now().isBefore(expiresAt) && LocalTime.now().isAfter(availableTime)) {
                        productDescLabel.setText(
                                String.format("חבילת צפייה: %s, זמינות עד השעה %s", this.relatedProduct.get("link"), this.relatedProduct.get("expiresAt"))
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
    void onRefundRequested(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("customerRefundRequestView", this.productClass, this.relatedProduct);
    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
        EventBus.getDefault().register(this);
    }

}
