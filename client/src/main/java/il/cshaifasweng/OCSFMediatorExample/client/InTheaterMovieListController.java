package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.Branch;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.DatabaseBridge;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.InTheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.ScreeningTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class InTheaterMovieListController {
    @FXML
    private ListView<String> movieListView;

    private List<InTheaterMovie> inTheaterMovies;

    private Branch selectedBranch;

    public void setSelectedBranch(Branch branch) {
        selectedBranch = branch;
        initializeList();
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (movieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        InTheaterMovie selectedMovie = inTheaterMovies.get(selectedIndex);

        // load screening list selector
        FXMLLoader screeningLoader = CinemaClient.setContent("screeningList");

        // set selected movie
        ScreeningListController screeningController = screeningLoader.getController();
        screeningController.setSelectedMovie(selectedMovie);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("movieTypeSelection");
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    void initializeList() {
        movieListView.getItems().clear();

        // get db
        DatabaseBridge db = DatabaseBridge.getInstance();
        inTheaterMovies = new ArrayList<>();

        // filter movies by branch
        for (InTheaterMovie movie : db.getAll(InTheaterMovie.class)) {
            // check if movie has screenings in the selected branch
            for (ScreeningTime screeningTime : movie.getScreenings()) {
                // if any of the screenings are in the selected branch, add the movie
                if (screeningTime.getBranch().equals(selectedBranch)) {
                    inTheaterMovies.add(movie);
                    break;
                }
            }
        }

        // get movie names
        String[] movieNames = new String[inTheaterMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = inTheaterMovies.get(i).getMovieName();
        }

        // display movies
        movieListView.getItems().addAll(movieNames);
    }

    @FXML
    void initialize() {}
}