package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedCustomerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerLoginController implements DialogInterface {

    @FXML
    private TextField customerIdNumField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label invalidUserLabel;

    private Dialog<ButtonType> dialog;

    private String customerFirstName;
    private String customerLastName;
    private String customerGovId;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {}

    @FXML
    void cancelLogin(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    private boolean inputIsValid() {
        String customerId = customerIdNumField.getText();
        return !(
                // if blank
                customerId.isBlank() ||
                // id should be of 9 digits
                customerId.length() != 9 ||
                // id should be of numeric type
                !customerId.matches("-?\\d+")
        );
    }

    @FXML
    void loginCustomer(ActionEvent event) throws IOException {
        // avoid empty selection
        if (!inputIsValid()) {
            customerIdNumField.clear();
            invalidUserLabel.setVisible(true);
            return;
        }
        // hide label
        invalidUserLabel.setVisible(false);
        // get id
        customerGovId = customerIdNumField.getText();
        // verify credentials
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "verify Customer id");
        newMessage.setData(customerGovId);
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("verify Customer id request sent");
        // disable buttons
        loginButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @Subscribe
    public void onVerifiedCustomerIdEvent(NewVerifiedCustomerIdEvent event) {
        Platform.runLater(() -> {
            String messageData = event.getMessage().getData();

            if (messageData.equals("user invalid")) {
                invalidUserLabel.setVisible(true);
                customerIdNumField.clear();
                // enable buttons
                loginButton.setDisable(false);
                cancelButton.setDisable(false);
            }
            else {
                String[] splitData = messageData.split(",");
                customerFirstName = splitData[0];
                customerLastName = splitData[1];
                try {
                    // set content
                    CinemaClient.setUserDataManager(customerFirstName, customerLastName, customerGovId);
                    CinemaClient.setContent("movieTypeSelection");
                    // close dialog
                    EventBus.getDefault().unregister(this);
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
    }

}
