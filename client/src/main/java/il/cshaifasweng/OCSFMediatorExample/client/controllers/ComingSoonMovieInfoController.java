package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class ComingSoonMovieInfoController implements DialogInterface {

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

    @FXML
    private Label releaseDateLabel;

    private Dialog<ButtonType> dialog;

    private String selectedMovie;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {
        this.selectedMovie = (String)items[0];
        setComingSoonMovie(selectedMovie);
    }

    public void setComingSoonMovie(String movie) {
        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
        String[] parsedMovie = selectedMovie.split(",(?![^\\[]*\\])");

        String title = parsedMovie[1];
        String description = parsedMovie[2].substring(1, parsedMovie[2].length() - 1);
        String mainActors = parsedMovie[3].substring(1, parsedMovie[3].length() - 1);
        String producerName = parsedMovie[4];
        LocalDate releaseDate = LocalDate.parse(parsedMovie[6]);

        // Format LocalDate to String
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedReleaseDate = releaseDate.format(formatter);

        movieLabel.setText(title);
        movieSummaryLabel.setText(String.format("תקציר: %s", description));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", mainActors));
        producerNameLabel.setText(String.format("מפיק: %s", producerName));
        releaseDateLabel.setText(String.format("תאריך עלייה לקולנוע: %s", formattedReleaseDate));
        movieLabel.setTooltip(new Tooltip(title));
        movieSummaryLabel.setTooltip(new Tooltip(description));
        primaryActorsLabel.setTooltip(new Tooltip(mainActors));
        producerNameLabel.setTooltip(new Tooltip(producerName));
        releaseDateLabel.setTooltip(new Tooltip(formattedReleaseDate));
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
