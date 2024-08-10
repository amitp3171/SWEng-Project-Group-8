package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveComingSoonMovieController implements DialogInterface {

    @FXML
    private TextField movieNameField;

    @FXML
    private Label statusLabel;

    private Dialog<ButtonType> dialog;

    private ArrayList<String> comingSoonMovies;

    @FXML
    private void initialize() {
        statusLabel.setVisible(false);
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... items) {
        this.comingSoonMovies = (ArrayList<String>)items[0];
    }

    public String getMovieName() {
        return movieNameField.getText();
    }

    @FXML
    void onCancelRemovingMovie(ActionEvent event) throws IOException {
        dialog.close();
    }

    @FXML
    void onRemoveComingSoonMovie(ActionEvent event) throws IOException {
        // Get the movie that matches the input
        String movieToRemove = getMatchingMovieData();

        if (movieToRemove != null) {
            int messageId = CinemaClient.getNextMessageId();
            Message newMessage = new Message(messageId, "remove coming soon movie");

            // Send the entire movie object as data
            newMessage.setData(movieToRemove);
            CinemaClient.getClient().sendToServer(newMessage);
            System.out.println("remove coming soon movie request sent");

            dialog.close();
        } else {
            statusLabel.setVisible(true);
        }
    }

    private String getMatchingMovieData() {
        for (String comingSoonMovie : comingSoonMovies) {
            // Split the comingSoonMovie string by comma to get individual fields
            String[] movieDetails = comingSoonMovie.split(",");
            if (movieDetails[1].equals(movieNameField.getText())) {
                // Return the formatted string containing movie details
                return comingSoonMovie;
            }
        }
        statusLabel.setText("סרט לא קיים!");
        return null;
    }






}
