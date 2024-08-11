package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MovieTypeSelectionController {

    @FXML
    private Label welcomeUserLabel;

    @FXML
    private MenuItem personalAreaMenuItem;

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
    void showPersonalArea(ActionEvent event) throws IOException {
        CinemaClient.setContent("customerPersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    @FXML
    void showComingSoonMovieList(ActionEvent event) throws IOException {
        CinemaClient.setContent("comingSoonMovieList");
    }

    @FXML
    void showHomeMovieList(ActionEvent event) throws IOException {
        CinemaClient.setContent("homeMovieList");
    }

    @FXML
    void showInTheaterMovieList(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("branchSelector");
    }

    @FXML
    void onBuySubscriptionCard(ActionEvent event) throws IOException {
        if (userDataManager.isGuest()) {
            CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");

            if (userDataManager.isGuest())
                welcomeUserLabel.setText("ברוך הבא אורח!");
            else
                welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));

            return;
        }

        CinemaClient.getDialogCreationManager().loadDialog("subscriptionCardPurchase");
    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
        if (userDataManager.isGuest())
            welcomeUserLabel.setText("ברוך הבא אורח!");
        else {
            welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));
            personalAreaMenuItem.setVisible(true);
        }
    }

}
