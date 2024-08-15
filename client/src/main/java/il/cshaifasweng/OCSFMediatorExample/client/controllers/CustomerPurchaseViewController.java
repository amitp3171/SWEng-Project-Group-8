package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @FXML
    private Button complaintButton;

    Dialog<ButtonType> dialog;

    DataParser dataParser;

    Map<String, String> selectedPurchase;

    String productClass;

    Map<String, String> relatedProduct;

    ButtonType refundStatus = ButtonType.CANCEL;


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
                    if (isBeforeScreeningTime(this.relatedProduct.get("screeningTime"), this.relatedProduct.get("screeningDate"))) {
                        productDescLabel.setText(
                                String.format("כרטיס: %s, אולם %s, מושב %s, %s, %n %s, סניף %s",
                                        this.relatedProduct.get("movieName"),
                                        (1 + ((Integer.parseInt(this.relatedProduct.get("theaterId")) - 1) % 10)),
                                        this.relatedProduct.get("seatNumber"),
                                        this.relatedProduct.get("screeningTime"),
                                        this.relatedProduct.get("screeningDate"),
                                        this.relatedProduct.get("branchLocation")));
                    }
                    else if (this.selectedPurchase.get("paymentMethod").equals("Subscription Card")) {
                        refundButton.setDisable(true);
                        refundButton.setText("אין אפשרות לקבל החזר כספי על מימוש כרטיסיות");
                    }
                    else {
                        refundButton.setDisable(true);
                        refundButton.setText("אין אפשרות לקבל החזר כספי לאחר הקרנת הסרט");
                    }
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
                    LocalTime availableTime = LocalTime.parse(this.relatedProduct.get("availableTime"), DateTimeFormatter.ofPattern("HH:mm"));

                    LocalDateTime availableAt = LocalDateTime.of(availableDate, availableTime);
                    LocalDateTime expiresAt = LocalDateTime.parse(this.relatedProduct.get("expiresAt"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

                    System.out.println(relatedProduct.toString());

                    if (LocalDateTime.now().plusHours(1).isAfter(availableAt.plusHours(1))) {
                        refundButton.setText("ניתן לבטל עד שעה לפני מועד הפעלת הקישור");
                        refundButton.setDisable(true);
                    }
                    if (LocalDateTime.now().isAfter(availableAt) && LocalDateTime.now().isBefore(expiresAt)) {
                        productDescLabel.setText(
                                String.format("חבילת צפייה: %s, זמינות עד %s, %s", this.relatedProduct.get("link"), expiresAt.getHour() + ":" + expiresAt.getMinute(), expiresAt.getDayOfMonth() + "/" + expiresAt.getMonthValue() + "/" + expiresAt.getYear())
                        );
                    }
                    else {
                        productDescLabel.setText(String.format("קישור: נבחרה זמינות ב-%s, %s", availableDate, availableTime));
                    }
                    break;
            }
            if (refundButton.isDisable())
                complaintButton.setDisable(false);

            System.out.println("Product details request received");
        });
    }

    //return true if the time in the parameters is before the current time
    public boolean isBeforeScreeningTime(String time, String date) {
        // Time and date strings
        String timeString = time;
        String dateString = date;



        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalTime parsedTime = LocalTime.parse(timeString, timeFormatter);
        LocalDate parsedDate = LocalDate.parse(dateString, dateFormatter);
        LocalDateTime parsedDateTime = LocalDateTime.of(parsedDate, parsedTime);
        LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
        System.out.println("Parsed LocalDateTime: " + parsedDateTime);
        System.out.println("Current LocalDateTime: " + currentTime);
        return currentTime.isBefore(parsedDateTime);
    }

    @FXML
    void onCancelPressed(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(refundStatus);
        dialog.close();
    }

    @FXML
    void onSubmitComplaint(ActionEvent event) throws IOException{
        CinemaClient.getDialogCreationManager().loadDialog("customerComplaintSubmissionView", selectedPurchase);
    }

    @FXML
    void onRefundRequested(ActionEvent event) throws IOException {
        this.refundStatus = CinemaClient.getDialogCreationManager().loadDialog("customerRefundRequestView", this.productClass, this.relatedProduct);
        if (this.refundStatus.equals(ButtonType.OK)) {
            refundButton.setDisable(true);
            complaintButton.setDisable(true);
        }
    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
        EventBus.getDefault().register(this);
    }

}
