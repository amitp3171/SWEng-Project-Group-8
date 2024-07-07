package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.*;
import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewInTheaterMovieListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.DatabaseBridge;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class InTheaterMovieListController {
    @FXML
    private ListView<String> movieListView;

    @FXML
    private Label branchNameLabel;

    private ArrayList<String> inTheaterMovies;

    private String selectedBranch;

    public void setSelectedBranch(String branchLocation) throws IOException {
        selectedBranch = branchLocation;
        branchNameLabel.setText(selectedBranch);
        requestServerUpdate(false);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
//        // if selected item is null
//        if (movieListView.getSelectionModel().getSelectedItem() == null) return;
//
//        // get screeningTime object
//        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
//        InTheaterMovie selectedMovie = inTheaterMovies.get(selectedIndex);
//
//        // load screening list selector
//        FXMLLoader screeningLoader = CinemaClient.setContent("screeningList");
//
//        // set selected movie
//        ScreeningListController screeningController = screeningLoader.getController();
//        screeningController.setSelectedBranch(selectedBranch);
//        screeningController.setSelectedMovie(selectedMovie);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    private void requestServerUpdate(boolean forceRefresh) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, String.format("get movie list,%s,%s", selectedBranch, forceRefresh));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("InTheaterMovie request sent");
    }

    void initializeList() {
        movieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[inTheaterMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = inTheaterMovies.get(i).split(",")[1];
        }
        // display movies
        movieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateInTheaterMovieEvent(NewInTheaterMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                inTheaterMovies = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("InTheaterMovie request received");
        });
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        requestServerUpdate(true);
    }

    @FXML
    void initialize() throws IOException {
        // register to EventBus
        EventBus.getDefault().register(this);
    }
}