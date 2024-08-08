package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.EventBus;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AddComingSoonMovieController {

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

    @FXML
    private Button cancelButton;

    @FXML
    private Button addButton;

    @FXML
    private Label statusLabel;

    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }



    @FXML
    private void onAddComingSoonMovie() {
        if (isInputValid()) {
            // Create a new ComingSoonMovie (this would typically be an entity you want to send to the server)
            String movieName = getMovieName();
            String producerName = getProducerName();
            List<String> mainActors = getMainActors();
            String description = getDescription();
            String picture = getPicture();

            // TODO: Send the data to the server or add it to the local list

            okClicked = true;
            dialogStage.close();
        } else {
            statusLabel.setText("Please fill out all fields correctly!");
            statusLabel.setVisible(true);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (movieNameField.getText() == null || movieNameField.getText().isEmpty()) {
            errorMessage += "No valid movie name!\n";
        }
        if (producerNameField.getText() == null || producerNameField.getText().isEmpty()) {
            errorMessage += "No valid producer name!\n";
        }
        if (mainActorsField.getText() == null || mainActorsField.getText().isEmpty()) {
            errorMessage += "No valid main actors!\n";
        }
        if (descriptionField.getText() == null || descriptionField.getText().isEmpty()) {
            errorMessage += "No valid description!\n";
        }
        if (pictureField.getText() == null || pictureField.getText().isEmpty()) {
            errorMessage += "No valid picture!\n";
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

    public String getPicture() {
        return pictureField.getText();
    }

    @FXML
    void onCancelAddingMovie(ActionEvent event) throws IOException {
        dialogStage.close();
    }
}
