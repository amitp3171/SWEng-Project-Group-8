package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductPriceEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
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

    @FXML
    private TextField selectedTime;

    @FXML
    private DatePicker linkActivationDatePicker;

    private LocalDate selectedDate;

    private Dialog<ButtonType> dialog;

    private Map<String, String> selectedMovie;

    private double productPrice;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.selectedMovie = (Map<String, String>) params[0];

        String title = selectedMovie.get("title");
        String description = selectedMovie.get("description").substring(1, selectedMovie.get("mainActors").length() - 1);
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
        if(isValidTimeFormat(selectedTime.getText()) && selectedDate != null) {
            if (CinemaClient.getUserDataManager().isGuest()) {
                CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");
            }

            if (CinemaClient.getUserDataManager().isCustomer()) {
                ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("cardPaymentPrompt", this.productPrice, 1);
                if (status == ButtonType.OK) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String formattedDate = selectedDate.format(formatter);
                        CinemaClient.sendToServer("create Link Purchase",
                                String.join(",",
                                        CinemaClient.getUserDataManager().getGovId(),
                                        this.selectedMovie.get("id"),
                                        String.valueOf(this.productPrice),
                                        formattedDate,
                                        selectedTime.getText()));
                    }

            }
            else {
                    statusLabel.setText("תשלום בוטל");
                    statusLabel.setTextFill(Color.RED);
                    statusLabel.setVisible(true);
            }
        }
        else {
            statusLabel.setText("יום או שעה לא תקינים. יש לבחור תאריך ושעה בפורמט: hh:mm");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
        }
    }

    public boolean isValidTimeFormat(String time) {
        // Check if the time format is valid (HH:mm)
        if (!time.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            return false;
        }

        // Parse the time string to LocalTime
        LocalTime parsedTime = LocalTime.parse(time);

        // Check if the selected date is today
        if (selectedDate.equals(LocalDate.now())) {
            // Check if the selected time is before the current time
            if (parsedTime.isBefore(LocalTime.now())) {
                return false;
            }
        }

        return true;
    }


    @FXML
    void onDateSelected(ActionEvent event) {
        // get selected day
        selectedDate = linkActivationDatePicker.getValue();
    }

    public void initializeDatePicker() {
        linkActivationDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && item != null) {
                            if (!item.isBefore(LocalDate.now()))
                                this.setStyle("-fx-background-color: lightgreen");
                            else
                                this.setDisable(true);
                        }
                    }
                };
            }
        });
        linkActivationDatePicker.setValue(this.selectedDate);
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
        initializeDatePicker();
        CinemaClient.sendToServer("get Product price", "Link");
    }
}


