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

import java.io.IOException;
import java.util.ArrayList;

public class HomeMovieListController {
    @FXML
    private ListView<String> homeMovieListView;

    UserDataManager userDataManager;

    private ArrayList<String> homeMovies;

    @FXML
    private MenuItem addHomeMovie;

    @FXML
    private MenuItem removeHomeMovie;

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (homeMovieListView.getSelectionModel().getSelectedItem() == null) return;

        int selectedIndex = homeMovieListView.getSelectionModel().getSelectedIndex();
        String selectedMovie = homeMovies.get(selectedIndex);

        // TODO: display dialog with selected movie info
        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("homeMovieInfo");
        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        HomeMovieInfoController homeMovieInfoController = dialogLoader.getController();
        // set selected movie
        homeMovieInfoController.setHomeMovie(selectedMovie);

        // create new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(screeningDialogPane);
        homeMovieInfoController.setDialog(dialog);

        // create hidden close button to support the close button (X)
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        // show dialog
        dialog.showAndWait();
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
            movieNames[i] = homeMovies.get(i).split(",")[1];
        }
        // display movies
        homeMovieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateHomeMovieEvent(NewHomeMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                homeMovies = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

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

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("addHomeMovie", homeMovies);
        requestHomeMovieList(true);
    }

    @FXML
    void onRemoveHomeMovie(ActionEvent event) throws IOException {

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("removeHomeMovie", homeMovies);
        requestHomeMovieList(true);
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        requestHomeMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();

        // register to EventBus
        EventBus.getDefault().register(this);

        requestHomeMovieList(false);

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager")) {
            addHomeMovie.setVisible(true);
            removeHomeMovie.setVisible(true);
        }
    }
}