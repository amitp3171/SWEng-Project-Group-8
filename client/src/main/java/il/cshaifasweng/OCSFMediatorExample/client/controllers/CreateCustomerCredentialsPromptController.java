package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCreateCustomerCredentialsEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewVerifiedCustomerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CreateCustomerCredentialsPromptController implements DialogInterface {

    @FXML
    private TextField customerIdNumField;

    @FXML
    private TextField customerFirstNameField;

    @FXML
    private TextField customerLastNameField;

    @FXML
    private Button createCustomerButton;

    @FXML
    private TextField customerLoginIdNumField;

    @FXML
    private Button customerExistsLogin;

    @FXML
    private Button cancelButton;

    @FXML
    private Label invalidUserLabel;

    DataParser dataParser;

    private Dialog<ButtonType> dialog;

    private String customerFirstName;
    private String customerLastName;
    private String customerGovId;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {}

    private boolean inputIsValid(boolean isLogin) {
        String customerId = isLogin ?  customerLoginIdNumField.getText() : customerIdNumField.getText();
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
    void cancelCreation(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    // TODO: handle
    @FXML
    void createCustomer(ActionEvent event) throws IOException {
        // avoid empty selection
        if (!inputIsValid(false) || customerFirstNameField.getText().isEmpty() || customerLastNameField.getText().isEmpty()) {
            customerIdNumField.clear();
            invalidUserLabel.setText("פורמט קלט אינו חוקי");
            invalidUserLabel.setVisible(true);
            return;
        }
        // hide label
        invalidUserLabel.setVisible(false);
        // get credentials
        this.customerGovId = customerIdNumField.getText();
        this.customerFirstName = customerFirstNameField.getText();
        this.customerLastName = customerLastNameField.getText();
        // verify credentials
        CinemaClient.sendToServer("create Customer credentials", String.join(",", this.customerGovId, this.customerFirstName, this.customerLastName));
        // disable buttons
        createCustomerButton.setDisable(true);
        cancelButton.setDisable(true);
        customerExistsLogin.setDisable(true);
    }

    @Subscribe
    public void onCreateCustomerEvent(NewCreateCustomerCredentialsEvent event) {
        Platform.runLater(() -> {
            String messageData = event.getMessage().getData();

            if (messageData.equals("user created")) {
                CinemaClient.setUserDataManager(this.customerFirstName, this.customerLastName, this.customerGovId);
                // close dialog
                EventBus.getDefault().unregister(this);
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
            else {
                invalidUserLabel.setText("משתמש קיים, נא להתחבר");
                invalidUserLabel.setVisible(true);
                // enable buttons
                createCustomerButton.setDisable(false);
                cancelButton.setDisable(false);
                customerExistsLogin.setDisable(false);
            }

        });
    }

    @FXML
    void loginCustomer(ActionEvent event) throws IOException {
        // avoid empty selection
        if (!inputIsValid(true)) {
            customerLoginIdNumField.clear();
            invalidUserLabel.setText("פורמט קלט אינו חוקי");
            invalidUserLabel.setVisible(true);
            return;
        }
        // hide label
        invalidUserLabel.setVisible(false);
        // get credentials
        this.customerGovId = customerLoginIdNumField.getText();
        // verify credentials
        CinemaClient.sendToServer("verify Customer id", this.customerGovId);
        // disable buttons
        createCustomerButton.setDisable(true);
        cancelButton.setDisable(true);
        customerExistsLogin.setDisable(true);
    }

    @Subscribe
    public void onVerifiedCustomerIdEvent(NewVerifiedCustomerIdEvent event) {
        Platform.runLater(() -> {
            String messageData = event.getMessage().getData();

            if (messageData.equals("user invalid")) {
                invalidUserLabel.setText("תעודת זהות שגויה");
                invalidUserLabel.setVisible(true);
                customerIdNumField.clear();
                // enable buttons
                createCustomerButton.setDisable(false);
                cancelButton.setDisable(false);
                customerExistsLogin.setDisable(false);
            }
            else {
                Map<String, String> customerMap = dataParser.parseCustomer(messageData);
                // set content
                CinemaClient.setUserDataManager(customerMap.get("firstName"), customerMap.get("lastName"), customerGovId);
                // close dialog
                EventBus.getDefault().unregister(this);
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
        });
    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
        EventBus.getDefault().register(this);
    }

}
