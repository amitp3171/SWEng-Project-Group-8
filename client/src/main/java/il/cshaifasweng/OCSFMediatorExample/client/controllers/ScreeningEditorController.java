package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

public class ScreeningEditorController {
    @FXML
    private TextField screeningHourTF;
    private Dialog<ButtonType> dialog;

    private ScreeningTime selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setScreeningHour(String screeningHour) {
        screeningHourTF.setText(screeningHour.split(",")[0]);
    }

    public void setSelectedScreeningTime(ScreeningTime selectedScreeningTime) {
        this.selectedScreeningTime = selectedScreeningTime;
    }

    @FXML
    void cancelScreeningUpdate(ActionEvent event) {
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void updateScreeningHour(ActionEvent event) throws ParseException {
        // get the new time
        String newTime = screeningHourTF.getText();
        newTime = newTime.split(",")[0];
        LocalTime parsedNewTime = LocalTime.parse(newTime, DateTimeFormatter.ofPattern("HH:mm"));

        selectedScreeningTime.setTime(parsedNewTime);

        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void initialize() {}
}
