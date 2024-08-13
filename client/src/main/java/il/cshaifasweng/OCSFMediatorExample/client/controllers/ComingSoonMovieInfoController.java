package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
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

    private Map<String, String> selectedMovie;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {
        this.selectedMovie = (Map<String, String>) items[0];
        setComingSoonMovie();
    }

    public void setComingSoonMovie() {
        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
        String title = this.selectedMovie.get("title");
        String description = this.selectedMovie.get("description").substring(1, this.selectedMovie.get("description").length() - 1);
        String mainActors = this.selectedMovie.get("mainActors").substring(1, this.selectedMovie.get("mainActors").length() - 1);
        String producerName = this.selectedMovie.get("producerName");
        LocalDate releaseDate = LocalDate.parse(this.selectedMovie.get("additionalFields"));

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
