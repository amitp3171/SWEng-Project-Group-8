package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductPriceEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.*;

public class HomeMovieInfoController implements DialogInterface {

    @FXML
    private ImageView movieImageView;

    @FXML
    private Label headerLabel;

    @FXML
    private Label movieLabel;

    @FXML
    private Label movieSummaryLabel;

    @FXML
    private Label primaryActorsLabel;

    @FXML
    private Label producerNameLabel;

    @FXML
    private Label movieLengthLabel;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;

    private Map<String, String> selectedMovie;

    private double productPrice;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.selectedMovie = (Map<String, String>) params[0];

        String title = selectedMovie.get("title");
        String description = selectedMovie.get("description");
        String mainActors = selectedMovie.get("mainActors").substring(1, selectedMovie.get("mainActors").length() - 1);
        String producerName = selectedMovie.get("producerName");
        String movieLength = selectedMovie.get("additionalFields");

        movieLabel.setText(title);
        movieSummaryLabel.setText(String.format("תקציר: %s", description));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", mainActors));
        producerNameLabel.setText(String.format("מפיק: %s", producerName));
        movieLengthLabel.setText(String.format("משך הסרט: %s שעות", movieLength));

        movieLabel.setTooltip(new Tooltip(title));
        movieSummaryLabel.setTooltip(new Tooltip(description));
        primaryActorsLabel.setTooltip(new Tooltip(mainActors));
        producerNameLabel.setTooltip(new Tooltip(producerName));
        movieLengthLabel.setTooltip(new Tooltip(movieLength));
    }

    @FXML
    void onCloseDialog(ActionEvent event) {
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }
    @FXML
    void onPurchaseButton(ActionEvent event) throws IOException {
        if (CinemaClient.getUserDataManager().isGuest()) {
            CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");
        }

        if (CinemaClient.getUserDataManager().isCustomer()) {
            ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("cardPaymentPrompt", this.productPrice, 1);
            if (status == ButtonType.OK) {
                CinemaClient.sendToServer("create Link Purchase",
                        String.join(",",
                                CinemaClient.getUserDataManager().getGovId(),
                                this.selectedMovie.get("id"),
                                String.valueOf(this.productPrice)));
            } else {
                statusLabel.setText("תשלום בוטל");
                statusLabel.setTextFill(Color.RED);
                statusLabel.setVisible(true);
            }
        }
    }

    @Subscribe
    public void onPurchaseStatusUpdate(NewPurchaseStatusEvent event) {
        // on event received
        Platform.runLater(() -> {
                statusLabel.setText("תשלום בוצע בהצלחה, ניתן לראות את הרכישה באיזור האישי");
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setVisible(true);
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
        EventBus.getDefault().register(this);
        CinemaClient.sendToServer("get Product price", "Link");
    }
}


