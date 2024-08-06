package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedCustomerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedEmployeeCredentialsEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EmployeeLoginController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button employeeLoginButton;

    @FXML
    private Label invalidUserLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField userNameField;

    private String employeeUserName;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeRole;

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

    @FXML
    void loginEmployeeCustomer(ActionEvent event) throws IOException {
        // avoid empty selection
        if (userNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            invalidUserLabel.setVisible(true);
            return;
        }
        // hide label
        invalidUserLabel.setVisible(false);
        // get id
        employeeUserName = userNameField.getText();
        // verify credentials
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "verify Employee credentials");
        // TODO: hash passwords!
        newMessage.setData(String.format("%s,%s", employeeUserName, passwordField.getText()));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("verify Employee credentials request sent");
        // disable buttons
        employeeLoginButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @Subscribe
    public void onVerifiedEmployeeCredentialsEvent(NewVerifiedEmployeeCredentialsEvent event) {
        Platform.runLater(() -> {
            String messageData = event.getMessage().getData();

            if (messageData.equals("user invalid")) {
                invalidUserLabel.setVisible(true);
                userNameField.clear();
                passwordField.clear();
                // enable buttons
                employeeLoginButton.setDisable(false);
                cancelButton.setDisable(false);
            }
            else {
                String[] splitData = messageData.split(",");
                employeeFirstName = splitData[0];
                employeeLastName = splitData[1];
                employeeRole = splitData[2];
                try {
                    // set content
                    MovieTypeSelectionController movieTypeSelectionController = CinemaClient.setContent("movieTypeSelection").getController();
                    movieTypeSelectionController.setEmployeeData(employeeFirstName, employeeLastName, employeeUserName, employeeRole);
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
