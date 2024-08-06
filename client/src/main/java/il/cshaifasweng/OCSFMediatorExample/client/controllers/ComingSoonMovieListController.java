package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComingSoonMovieListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ComingSoonMovieListController {
    @FXML
    private ListView<String> movieListView;

    @FXML
    private Label branchNameLabel;

    private String firstName;
    private String lastName;
    private String govId = null;

    private boolean isGuest = false;

    private String employeeUserName = null;
    private String employeeType = null;

    private ArrayList<String> allComingSoonMovies;

    private ArrayList<String> comingSoonMovies;

    private boolean forceRefresh;

    void setCustomerData() {
        this.isGuest = true;
    }

    void setCustomerData(String firstName, String lastName, String govId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.govId = govId;
    }

    void setEmployeeData(String firstName, String lastName, String userName, String employeeType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeUserName = userName;
        this.employeeType = employeeType;
        this.isGuest = false;
    }


    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (movieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        String selectedMovie = comingSoonMovies.get(selectedIndex);

        // load screening list selector
        FXMLLoader screeningLoader = CinemaClient.setContent("screeningList");

        // set selected movie
        ScreeningListController screeningController = screeningLoader.getController();
        if (this.isGuest)
            screeningController.setCustomerData();
        else if (this.employeeType == null)
            screeningController.setCustomerData(this.firstName, this.lastName, this.govId);
        else
            screeningController.setEmployeeData(this.firstName, this.lastName, this.employeeUserName, this.employeeType);
        screeningController.setSelectedMovie(selectedMovie, forceRefresh);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        MovieTypeSelectionController movieTypeSelectionController = CinemaClient.setContent("movieTypeSelection").getController();
        if (isGuest)
            movieTypeSelectionController.setCustomerData();
        else if (this.employeeType == null)
            movieTypeSelectionController.setCustomerData(this.firstName, this.lastName, this.govId);
        else
            movieTypeSelectionController.setEmployeeData(this.firstName, this.lastName, this.employeeUserName, this.employeeType);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    private void requestComingSoonMovieList(boolean forceRefresh) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get ComingSoonMovie list");
        newMessage.setData(String.valueOf(forceRefresh));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ComingSoonMovie request sent");
    }


    void initializeList() throws IOException {
        movieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[comingSoonMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = comingSoonMovies.get(i).split(",")[1];
        }
        // display movies
        movieListView.getItems().addAll(movieNames);


    }

    @Subscribe
    public void onUpdateComingSoonMovieEvent(NewComingSoonMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                comingSoonMovies = new ArrayList<>();
                allComingSoonMovies = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String movie : allComingSoonMovies) {
                        comingSoonMovies.add(movie);
                }

                // update list
                if (!comingSoonMovies.isEmpty())
                    initializeList();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("ComingSoonMovie request received");
        });
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        this.forceRefresh = true;
        requestComingSoonMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        forceRefresh = false;
        // register to EventBus
        EventBus.getDefault().register(this);
        requestComingSoonMovieList(false);

    }


}