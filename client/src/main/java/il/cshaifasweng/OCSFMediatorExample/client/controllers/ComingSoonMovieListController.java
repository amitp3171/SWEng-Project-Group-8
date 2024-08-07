package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComingSoonMovieListEvent;
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

import java.io.IOException;
import java.util.ArrayList;

public class ComingSoonMovieListController {
    @FXML
    private ListView<String> comingSoonMovieListView;

    UserDataManager userDataManager;

    private ArrayList<String> comingSoonMovies;

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (comingSoonMovieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = comingSoonMovieListView.getSelectionModel().getSelectedIndex();
        String selectedMovie = comingSoonMovies.get(selectedIndex);

        // TODO: display dialog with selected movie info
        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("comingSoonMovieInfo");
        DialogPane screeningDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        ComingSoonMovieInfoController comingSoonMovieInfoController = dialogLoader.getController();
        // set selected movie
        comingSoonMovieInfoController.setComingSoonMovie(selectedMovie);

        // create new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(screeningDialogPane);
        comingSoonMovieInfoController.setDialog(dialog);

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
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    private void requestComingSoonMovieList(boolean forceRefresh) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get ComingSoonMovie list");
        newMessage.setData(String.valueOf(forceRefresh));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("ComingSoonMovie request sent");;
    }

    void initializeList() {
        comingSoonMovieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[comingSoonMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = comingSoonMovies.get(i).split(",")[1];
        }
        // display movies
        comingSoonMovieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateInTheaterMovieEvent(NewComingSoonMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                comingSoonMovies = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

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
        requestComingSoonMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();

        // register to EventBus
        EventBus.getDefault().register(this);

        requestComingSoonMovieList(false);
    }
}