package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewAddedComingSoonMovieEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import org.greenrobot.eventbus.Subscribe;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddHomeMovieController implements DialogInterface {

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
    private TextField movieLengthField;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;

    private ArrayList<String> homeMovies;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {
        this.homeMovies = (ArrayList<String>)items[0];
    }



    @FXML
    private void onAddHomeMovie() throws IOException {

        // Add the movie if the input is valid
        if (isInputValid()) {

            int messageId = CinemaClient.getNextMessageId();
            Message newMessage = new Message(messageId, "add new home movie");
            String mainActorsString = String.join(";", getMainActors());
            String description = "[" + getDescription() + "]";
            newMessage.setData(String.format("%s,%s,%s,%s,%s,%s", getMovieName(), getProducerName(), mainActorsString, description, getPicture(), getMovieLength()));
            CinemaClient.getClient().sendToServer(newMessage);
            System.out.println("add new home movie request sent");


            dialog.close();
        } else {
            statusLabel.setVisible(true);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        for (String homeMovie :homeMovies) {
            // Split the comingSoonMovie string by comma to get individual fields
            String[] movieDetails = homeMovie.split(",");


            // Check if the first field (movie name) is the same as the movieNameField's text
            if (movieDetails.length > 0 && movieDetails[1].replace(",","").equals(movieNameField.getText())) {
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
            else if (pictureField.getText() == null || pictureField.getText().isEmpty()) {
                errorMessage = "תמונה לא תקינה!" + "\n";
            }
            // Validate the release date format
            else if (movieLengthField.getText() == null || movieLengthField.getText().isEmpty()) {
                errorMessage = "משך הסרט לא תקין! הכנס מספר!" + "\n";
            } else {
                try {
                    Double.parseDouble(movieLengthField.getText());
                } catch (NumberFormatException e) {
                    errorMessage = "משך הסרט לא תקין! הכנס מספר!" + "\n";
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

    public String getPicture() {
        return pictureField.getText();
    }

    public String getMovieLength() { return movieLengthField.getText(); }

    @FXML
    void onCancelAddingMovie(ActionEvent event) throws IOException {
        dialog.close();
    }

    @Subscribe
    public void onAddedHomeMovieEvent(NewAddedComingSoonMovieEvent event) {
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
