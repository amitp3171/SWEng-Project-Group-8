package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedCustomerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedEmployeeCredentialsEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EmployeeLoginController implements DialogInterface {

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

    DataParser dataParser;

    private String employeeUserName;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeRole;

    private Dialog<ButtonType> dialog;

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

    // TODO: hash passwords!
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
        this.employeeUserName = userNameField.getText();
        // verify credentials
        CinemaClient.sendToServer("verify Employee credentials", String.join(",", this.employeeUserName, passwordField.getText()));
        // disable buttons
        employeeLoginButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    @Subscribe
    public void onVerifiedEmployeeCredentialsEvent(NewVerifiedEmployeeCredentialsEvent event) {
        Platform.runLater(() -> {
            String messageData = event.getMessage().getData();

            if (messageData.equals("user invalid")) {
                invalidUserLabel.setText("פרטי כניסה שגויים");
                invalidUserLabel.setVisible(true);
                userNameField.clear();
                passwordField.clear();
                // enable buttons
                employeeLoginButton.setDisable(false);
                cancelButton.setDisable(false);
            }
            else if (messageData.equals("user already logged in")) {
                invalidUserLabel.setText("אין אפשרות להתחבר למשתמש אשר מחובר במקום אחר");
                invalidUserLabel.setVisible(true);
                userNameField.clear();
                passwordField.clear();
                // enable buttons
                employeeLoginButton.setDisable(false);
                cancelButton.setDisable(false);
            }
            else {
                Map<String, String> employeeDictionary = dataParser.parseEmployee(messageData);
                try {
                    // set content
                    if (employeeDictionary.get("employeeType").equals("BranchManager")) {
                        CinemaClient.setUserDataManager(employeeDictionary.get("id"), employeeDictionary.get("firstName"), employeeDictionary.get("lastName"), this.employeeUserName, employeeDictionary.get("employeeType"), employeeDictionary.get("additionalFields"));
                    }
                    else {
                        CinemaClient.setUserDataManager(employeeDictionary.get("id"), employeeDictionary.get("firstName"), employeeDictionary.get("lastName"), this.employeeUserName, employeeDictionary.get("employeeType"));
                    }
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
        dataParser = CinemaClient.getDataParser();
        // register to EventBus
        EventBus.getDefault().register(this);
    }

}
