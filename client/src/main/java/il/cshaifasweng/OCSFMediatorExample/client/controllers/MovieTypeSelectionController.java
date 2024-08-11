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
import java.util.List;

public class MovieTypeSelectionController {

    @FXML
    private Label welcomeUserLabel;

    @FXML
    private MenuItem addInTheaterMovieMenuItem;

    @FXML
    private MenuItem addOrRemoveMovies;

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

    //TODO: add inTheaterMovie here?
    // Add movie
    @FXML
    void onAddInTheaterMovie(ActionEvent event) throws IOException {

    }

    @FXML
    void initialize() {
        userDataManager = CinemaClient.getUserDataManager();
        if (userDataManager.isGuest())
            welcomeUserLabel.setText("ברוך הבא אורח!");
        else
            welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", userDataManager.getFirstName(), userDataManager.getLastName()));

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager")) {
            addOrRemoveMovies.setVisible(true);
            addInTheaterMovieMenuItem.setVisible(true);
        }
    }

}
