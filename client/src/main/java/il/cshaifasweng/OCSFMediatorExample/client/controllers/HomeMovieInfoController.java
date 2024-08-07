package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class HomeMovieInfoController {

    @FXML
    private ImageView movieImageView;

    @FXML
    private Label movieLabel;

    @FXML
    private Label movieSummaryLabel;

    @FXML
    private Label primaryActorsLabel;

    @FXML
    private Label producerNameLabel;

    private Dialog<ButtonType> dialog;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setHomeMovie(String movie) {
        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
        String[] parsedMovie = movie.split(",(?![^\\[]*\\])");

        movieLabel.setText(parsedMovie[1]);
        movieSummaryLabel.setText(String.format("תקציר: %s", parsedMovie[2]));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", parsedMovie[3].substring(1, parsedMovie[3].length() - 1)));
        producerNameLabel.setText(String.format("מפיק: %s",parsedMovie[4]));
    }

    @FXML
    void onCloseDialog(ActionEvent event) {
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void initialize() {

    }

}
