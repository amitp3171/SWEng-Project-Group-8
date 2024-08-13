package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RemoveComingSoonMovieController implements DialogInterface {

    @FXML
    private ComboBox<String> chooseMovieComboBox;

    @FXML
    private Button removeMovieButton;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;

    private ArrayList<Map<String, String>> comingSoonMovies;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.comingSoonMovies = (ArrayList<Map<String, String>>) params[0];

        String[] items = new String[this.comingSoonMovies.size()];

        for (int i = 0; i < this.comingSoonMovies.size(); i++) {
            items[i] = this.comingSoonMovies.get(i).get("movieName");
        }

        chooseMovieComboBox.getItems().addAll(Arrays.asList(items));
    }

    @FXML
    void onCancelRemovingMovie(ActionEvent event) throws IOException {
        dialog.close();
    }

    @FXML
    void onMovieChosen(ActionEvent event) {
        removeMovieButton.setDisable(false);
    }

    @FXML
    void onRemoveComingSoonMovie(ActionEvent event) throws IOException {
        int selectedIndex = chooseMovieComboBox.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMovie = comingSoonMovies.get(selectedIndex);

        CinemaClient.sendToServer("remove coming soon movie", selectedMovie.get("id"));
        dialog.close();
    }
}
