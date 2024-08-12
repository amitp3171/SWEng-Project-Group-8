package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewHomeMovieListEvent;
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
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;

import java.io.IOException;
import java.util.ArrayList;

public class HomeMovieListController {
    @FXML
    private ListView<String> homeMovieListView;

    UserDataManager userDataManager;

    private ArrayList<Map<String, String>> homeMovies = new ArrayList<>();

    @FXML
    private Menu addMovieMenu;

    @FXML
    private Menu removeMovieMenu;

    @FXML
    private MenuItem addHomeMovie;

    @FXML
    private MenuItem removeHomeMovie;

    private DataParser dataParser;

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (homeMovieListView.getSelectionModel().getSelectedItem() == null) return;

        int selectedIndex = homeMovieListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMovie = homeMovies.get(selectedIndex);

        CinemaClient.getDialogCreationManager().loadDialog("homeMovieInfo", selectedMovie);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    private void requestHomeMovieList(boolean forceRefresh) throws IOException {
        // send request to server
        CinemaClient.sendToServer("get HomeMovie list", String.valueOf(forceRefresh));
    }

    void initializeList() {
        homeMovieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[homeMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = homeMovies.get(i).get("movieName");
        }
        // display movies
        homeMovieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateHomeMovieEvent(NewHomeMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                homeMovies.clear();
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String movie : receivedData) {
                    homeMovies.add(dataParser.parseMovie(movie));
                }

                if (!homeMovies.isEmpty())
                    initializeList();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("HomeMovie request received");
        });
    }

    @FXML
    void onAddHomeMovie(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("addHomeMovie", homeMovies);
        requestHomeMovieList(true);
    }

    @FXML
    void onRemoveHomeMovie(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("removeHomeMovie", homeMovies);
        requestHomeMovieList(true);
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        requestHomeMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();
        dataParser = CinemaClient.getDataParser();

        // register to EventBus
        EventBus.getDefault().register(this);

        requestHomeMovieList(false);

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager")) {
            addMovieMenu.setVisible(true);
            removeMovieMenu.setVisible(true);
            addHomeMovie.setVisible(true);
            removeHomeMovie.setVisible(true);
        }
    }
}