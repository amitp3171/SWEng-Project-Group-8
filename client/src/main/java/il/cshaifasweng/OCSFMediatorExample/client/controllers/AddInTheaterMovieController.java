package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.ByteArrayInputStream;
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

public class AddInTheaterMovieController implements DialogInterface {

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

    @FXML
    private Label statusLabel;

    DataParser dataParser;

    private Dialog<ButtonType> dialog;

    private ArrayList<Map<String, String>> inTheaterMovies;

    private String selectedBranch;

    @FXML
    private void initialize() {
        dataParser = CinemaClient.getDataParser();
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {
        this.inTheaterMovies = (ArrayList<Map<String, String>>)items[0];
        this.selectedBranch = (String)items[1];
    }

    @FXML
    void onCancelAddingMovie(ActionEvent event) throws IOException {
        dialog.setResult(ButtonType.CANCEL);
        dialog.close();
    }

    @FXML
    private void onAddInTheaterMovie() throws IOException {
        // Add the movie if the input is valid
        if (isInputValid()) {
            String mainActorsString = String.join(";", getMainActors());
            String description = "[" + getDescription() + "]";
            CinemaClient.sendToServer("add new in theaters movie", String.join(",", getMovieName(), getProducerName(), mainActorsString, description, encodedImage, selectedBranch));
            dialog.setResult(ButtonType.OK);
            dialog.close();
        } else {
            statusLabel.setVisible(true);
        }
    }

    @FXML
    private void onChoosePicture(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("בחר תמונה");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"));
        fileChooser.setInitialDirectory(new File("C:/"));

        File selectedFile = fileChooser.showOpenDialog(CinemaClient.getStage());
        if (selectedFile != null) {
            this.encodedImage = java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(selectedFile.toPath()));

        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        for (Map<String, String> inTheaterMovie : inTheaterMovies) {

            // Check if the first field (movie name) is the same as the movieNameField's text
            if (inTheaterMovie.get("movieName").equals(movieNameField.getText())) {
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

        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            statusLabel.setText(errorMessage);
            return false;
        }
    }

}