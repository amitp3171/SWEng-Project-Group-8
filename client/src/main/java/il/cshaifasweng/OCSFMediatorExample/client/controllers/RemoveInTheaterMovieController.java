package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RemoveInTheaterMovieController implements DialogInterface {

    @FXML
    private ComboBox<String> chooseMovieComboBox;

    @FXML
    private Button removeMovieButton;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;

    private ArrayList<Map<String, String>> inTheaterMovies;

    private String selectedBranch;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.inTheaterMovies = (ArrayList<Map<String, String>>) params[0];
        this.selectedBranch = (String) params[1];

        String[] items = new String[this.inTheaterMovies.size()];

        for (int i = 0; i < this.inTheaterMovies.size(); i++) {
            items[i] = this.inTheaterMovies.get(i).get("movieName");
            System.out.println(inTheaterMovies.get(i).toString());
        }

        chooseMovieComboBox.getItems().addAll(Arrays.asList(items));
    }

    @FXML
    void onCancelRemovingMovie(ActionEvent event) throws IOException {
        dialog.setResult(ButtonType.CANCEL);
        dialog.close();
    }

    @FXML
    void onMovieChosen(ActionEvent event) {
        removeMovieButton.setDisable(false);
    }

    @FXML
    void onRemoveInTheaterMovie(ActionEvent event) throws IOException {
        int selectedIndex = chooseMovieComboBox.getSelectionModel().getSelectedIndex();

        Map<String, String> selectedMovie = inTheaterMovies.get(selectedIndex);

        if (selectedMovie.get("additionalFields").split(",")[1].equals("true")) {
            statusLabel.setText("לא ניתן למחוק סרט זה כיוון שקיימות הקרנות פעילות");
            statusLabel.setVisible(true);
            return;
        }

        CinemaClient.sendToServer("remove in theaters movie", String.join(",", selectedMovie.get("id"), this.selectedBranch));
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }
}
