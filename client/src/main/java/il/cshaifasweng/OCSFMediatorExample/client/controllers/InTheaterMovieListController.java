package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewInTheaterMovieListEvent;
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

public class InTheaterMovieListController {
    @FXML
    private ListView<String> movieListView;

    @FXML
    private Label branchNameLabel;

    @FXML
    private MenuItem addScreeningMenuitem;

    UserDataManager userDataManager;

    DataParser dataParser;

    private ArrayList<Map<String, String>> allInTheaterMovies = new ArrayList<>();

    private ArrayList<Map<String, String>> inTheaterMovies = new ArrayList<>();

    private String selectedBranch;

    private boolean forceRefresh;

    public void setSelectedBranch(String branchLocation) throws IOException {
        selectedBranch = branchLocation;
        branchNameLabel.setText(selectedBranch);
        requestInTheaterMovieList(false);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (movieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMovie = inTheaterMovies.get(selectedIndex);

        // set selected movie
        ScreeningListController screeningController = CinemaClient.setContent("screeningList").getController();
        screeningController.setSelectedBranch(selectedBranch);
        screeningController.setSelectedMovie(selectedMovie, forceRefresh);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onAddScreening(ActionEvent event) throws IOException {
        CinemaClient.getDialogCreationManager().loadDialog("screeningCreator", this.allInTheaterMovies, this.selectedBranch);
    }

    private void requestInTheaterMovieList(boolean forceRefresh) throws IOException {
        CinemaClient.sendToServer("get InTheaterMovie list", String.join(",", selectedBranch, String.valueOf(forceRefresh)));
    }

    void initializeList() {
        movieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[inTheaterMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = inTheaterMovies.get(i).get("movieName");
        }
        // display movies
        movieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateInTheaterMovieEvent(NewInTheaterMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> messageData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String movie : messageData) {
                    Map<String, String> movieDictionary = dataParser.parseMovie(movie);
                    allInTheaterMovies.add(movieDictionary);

                    boolean isInBranch = Boolean.parseBoolean(movieDictionary.get("additionalFields"));
                    if (isInBranch)
                        inTheaterMovies.add(movieDictionary);
                }

                if (!inTheaterMovies.isEmpty())
                    initializeList();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("InTheaterMovie request received");
        });
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        this.forceRefresh = true;
        requestInTheaterMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();
        dataParser = CinemaClient.getDataParser();

        forceRefresh = false;

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager"))
            addScreeningMenuitem.setVisible(true);

        // register to EventBus
        EventBus.getDefault().register(this);
    }
}