
package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
        import org.greenrobot.eventbus.EventBus;

public class PurchasesController {

    @FXML
    private Label welcomeUserLabel;

    UserDataManager userDataManager;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }
    @FXML
    void goToAccount(ActionEvent event) throws IOException {
        CinemaClient.setContent("account");
    }

    @FXML
    void purchases(ActionEvent event) throws IOException {
        CinemaClient.setContent("purchasesList");
    }

    @FXML
    void messages(ActionEvent event) throws IOException {
        CinemaClient.setContent("messagesList");
    }

    @FXML
    void complaints(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("complaintsList");
    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
       /* if (userDataManager.isGuest())
            welcomeUserLabel.setText("ברוך הבא אורח!");
        else
            welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));
    */}

}

