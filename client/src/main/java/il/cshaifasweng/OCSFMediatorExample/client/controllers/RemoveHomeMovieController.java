package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RemoveHomeMovieController implements DialogInterface {

    @FXML
    private ComboBox<String> chooseMovieComboBox;

    @FXML
    private Button removeMovieButton;

    @FXML
    private Label statusLabel;

    private DataParser dataParser;

    private Dialog<ButtonType> dialog;

    private ArrayList<Map<String, String>> homeMovies;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.homeMovies = (ArrayList<Map<String, String>>) params[0];

        String[] items = new String[this.homeMovies.size()];

        for (int i = 0; i < this.homeMovies.size(); i++) {
            items[i] = this.homeMovies.get(i).get("movieName");
        }

        chooseMovieComboBox.getItems().addAll(Arrays.asList(items));
    }

    @FXML
    private void onMovieSelected(ActionEvent event) {
        removeMovieButton.setDisable(false);
    }

    @FXML
    void onCancelRemovingMovie(ActionEvent event) throws IOException {
        dialog.close();
    }

    @FXML
    void onRemoveHomeMovie(ActionEvent event) throws IOException {
        int selectedIndex = chooseMovieComboBox.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMovie = homeMovies.get(selectedIndex);

        if (selectedMovie.get("additionalFields").split(",")[1].equals("true")) {
            statusLabel.setText("לא ניתן למחוק סרט זה כיוון שקיימות הקרנות פעילות");
            statusLabel.setVisible(true);
            return;
        }
        CinemaClient.sendToServer("remove home movie", selectedMovie.get("id"));
        dialog.close();
    }
}
