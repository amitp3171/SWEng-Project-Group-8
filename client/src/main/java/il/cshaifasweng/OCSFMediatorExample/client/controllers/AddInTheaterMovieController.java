package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class AddInTheaterMovieController {

    @FXML
    private TextField movieNameField;

    @FXML
    private TextField producerNameField;

    @FXML
    private TextField mainActorsField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField pictureField;

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

    public String getPicture() {
        return pictureField.getText();
    }
}