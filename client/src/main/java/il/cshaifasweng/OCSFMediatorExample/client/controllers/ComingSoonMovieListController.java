package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComingSoonMovieListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComingSoonMovieListController {
    @FXML
    private ListView<String> comingSoonMovieListView;

    UserDataManager userDataManager;

    DataParser dataParser;

    private ArrayList<Map<String, String>> comingSoonMovies = new ArrayList<>();

    @FXML
    private Menu addMovieMenu;

    @FXML
    private Menu removeMovieMenu;

    @FXML
    private MenuItem addComingSoonMovie;

    @FXML
    private MenuItem removeComingSoonMovie;

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (comingSoonMovieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = comingSoonMovieListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMovie = comingSoonMovies.get(selectedIndex);

        CinemaClient.getDialogCreationManager().loadDialog("comingSoonMovieInfo",selectedMovie);
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
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    private void requestComingSoonMovieList(boolean forceRefresh) throws IOException {
        // send request to server
        CinemaClient.sendToServer("get ComingSoonMovie list", String.valueOf(forceRefresh));
    }

    void initializeList() {
        comingSoonMovieListView.getItems().clear();

        // get movie names
        String[] movieNames = new String[comingSoonMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = comingSoonMovies.get(i).get("movieName");
        }

        // display movies
        comingSoonMovieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateComingSoonMovieEvent(NewComingSoonMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                comingSoonMovies.clear();

                ArrayList<String> receivedData =  CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String movie : receivedData) {
                    comingSoonMovies.add(dataParser.parseMovie(movie));
                }

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
    void onAddComingSoonMovie(ActionEvent event) throws IOException {

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("addComingSoonMovie", comingSoonMovies);

        requestComingSoonMovieList(true);

//        if (result == ButtonType.OK) {
//            // Get the movie data from the dialog
//            String movieName = controller.getMovieName();
//            String producerName = controller.getProducerName();
//            List<String> mainActors = controller.getMainActors();
//            String description = controller.getDescription();
//            String picture = controller.getPicture();
//            // Create the new movie string in the same format as the existing movies
//            String newMovie = String.format("%d,%s,%s,%s,%s,%s", comingSoonMovies.size(), movieName, description, String.join(";", mainActors), producerName, picture);
//            // Add the new movie to the list and refresh the display
//            comingSoonMovies.add(newMovie);
//            initializeList();
//            // Send the new movie list to the server
//            saveNewMovieList();
//        }
    }

    @FXML
    void onRemoveComingSoonMovie(ActionEvent event) throws IOException {
        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("removeComingSoonMovie",comingSoonMovies);
        requestComingSoonMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();
        dataParser = CinemaClient.getDataParser();

        // register to EventBus
        EventBus.getDefault().register(this);

        requestComingSoonMovieList(false);

        if (userDataManager.isEmployee() && userDataManager.getEmployeeType().equals("ContentManager")) {
            addMovieMenu.setVisible(true);
            removeMovieMenu.setVisible(true);
            addComingSoonMovie.setVisible(true);
            removeComingSoonMovie.setVisible(true);
        }
    }
}