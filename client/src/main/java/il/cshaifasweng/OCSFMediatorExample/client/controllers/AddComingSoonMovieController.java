package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewAddedComingSoonMovieEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.Subscribe;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddComingSoonMovieController implements DialogInterface {

    @FXML
    private TextField movieNameField;

    @FXML
    private TextField producerNameField;

    @FXML
    private TextField mainActorsField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Button choosePicture;

    @FXML
    private ImageView image;

    private String encodedImage = null;

    @FXML
    private TextField releaseDateField;

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
    }


    @FXML
    private void onAddComingSoonMovie() throws IOException {
        // Add the movie if the input is valid
        if (isInputValid()) {
            String mainActorsString = String.join(";", getMainActors());
            String description = "[" + getDescription() + "]";
            CinemaClient.sendToServer("add new coming soon movie", String.join(",", getMovieName(), getProducerName(), mainActorsString, description, encodedImage, getReleaseDate()));
            dialog.close();
        } else {
            statusLabel.setVisible(true);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        for (Map<String, String> movieMap : comingSoonMovies) {
            // Check if the first field (movie name) is the same as the movieNameField's text
            if (movieMap.get("movieName").equals(movieNameField.getText())) {
                errorMessage = "הסרט כבר קיים!" + "\n";
                break;  // No need to check further if we found a match
            }
        }
        if(errorMessage.isEmpty()) {
            if (movieNameField.getText() == null || movieNameField.getText().isEmpty()) {
                errorMessage = "שם סרט לא תקין!" + "\n";
            }
            else if (producerNameField.getText() == null || producerNameField.getText().isEmpty()) {
                errorMessage = "שם מפיק לא תקין!" + "\n";
            }
            else if (mainActorsField.getText() == null || mainActorsField.getText().isEmpty()) {
                errorMessage = "רשימת סרטים לא תקינה!" + "\n";
            }
            else if (descriptionField.getText() == null || descriptionField.getText().isEmpty()) {
                errorMessage = "תיאור סרט לא תקין!" + "\n";
            }
            else if (encodedImage == null) {
                errorMessage = "תמונה לא תקינה!" + "\n";
            }
            // Validate the release date format
            else if (releaseDateField.getText() == null || releaseDateField.getText().isEmpty()) {
                errorMessage = "תאריך עלייה לקולנוע לא תקין! חייב להיות מפורמט yyyy-mm-dd!" + "\n";
            } else {
                try {
                    LocalDate.parse(releaseDateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException e) {
                    errorMessage = "תאריך עלייה לקולנוע לא תקין! חייב להיות מפורמט yyyy-mm-dd!" + "\n";
                }
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            statusLabel.setText(errorMessage);
            return false;
        }
    }

    public String getMovieName() {
        return movieNameField.getText();
    }

    public String getProducerName() {
        return producerNameField.getText();
    }

    public List<String> getMainActors() {
        return Arrays.asList(mainActorsField.getText().split(","));
    }

    public String getDescription() {
        return descriptionField.getText();
    }

    public String getReleaseDate() { return releaseDateField.getText(); }

    @FXML
    void onCancelAddingMovie(ActionEvent event) throws IOException {
        dialog.close();
    }

    @FXML
    private void onChoosePicture(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("בחר תמונה");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        fileChooser.setInitialDirectory(new File("C:/"));

        File selectedFile = fileChooser.showOpenDialog(CinemaClient.getStage());
        if (selectedFile != null) {
            this.encodedImage = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(selectedFile.toPath()));

        }
    }

    @Subscribe
    public void onAddedComingSoonMovieEvent(NewAddedComingSoonMovieEvent event) {
        Platform.runLater(() -> {
            String status = event.getMessage().getData();
            if (status.equals("request successful"))
                statusLabel.setText("סרט נוצר בהצלחה");
            else
                statusLabel.setText("שגיאה (הקרנה קיימת)");
            statusLabel.setVisible(true);
        });
    }
}
