package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedCustomerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerLoginController {

    @FXML
    private TextField customerIdNumField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    private Dialog<ButtonType> dialog;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    @FXML
    void cancelLogin(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    private boolean inputIsValid() {
        String customerId = customerIdNumField.getText();
        return (
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
        if (!inputIsValid()) return;
        // verify credentials
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "verify Customer id");
        newMessage.setData(customerIdNumField.getText());
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("verify Customer id request sent");
        // disable buttons
        loginButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @Subscribe
    public void onVerifiedCustomerIdEvent(NewVerifiedCustomerIdEvent event) {
        Platform.runLater(() -> {
//            try {
//
//                availableBranches = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
            // if credentials are correct
            // TODO: implement
            // create controller
//            MovieTypeSelectionController movieTypeSelectionController = CinemaClient.setContent("movieTypeSelection").getController();
//            // TODO: set user
////        movieTypeSelectionController.setSelectedBranch(selectedBranch);
//            // close dialog
//            EventBus.getDefault().unregister(this);
//            dialog.setResult(ButtonType.OK);
//            dialog.close();
        });
    }

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
    }

}
