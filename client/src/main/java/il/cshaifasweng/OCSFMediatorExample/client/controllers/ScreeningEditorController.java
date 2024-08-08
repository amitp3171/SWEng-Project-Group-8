package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

public class ScreeningEditorController implements DialogInterface {
    @FXML
    private TextField screeningHourTF;
    private Dialog<ButtonType> dialog;
    @FXML
    private DatePicker screeningDatePicker;

    private StringBuilder selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) { // String screeningHour, StringBuilder selectedScreeningTime
        screeningHourTF.setText(((String)params[0]).split(",")[0]);
        this.selectedScreeningTime = (StringBuilder) params[1];
        screeningDatePicker.setValue(LocalDate.parse(selectedScreeningTime.toString().split(",")[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
        String[] parsedScreeningTime = selectedScreeningTime.toString().split(",");
        int startingIndex = parsedScreeningTime[0].length() + parsedScreeningTime[1].length() + 2;
        int endingIndex = startingIndex + parsedScreeningTime[3].length() + 3;
        selectedScreeningTime.replace(startingIndex, endingIndex, newTime);
        // get the new date
        String newDate = screeningDatePicker.getValue().toString();
        int startingIndex2 = parsedScreeningTime[0].length() + 1;
        int endingIndex2 = startingIndex2 + parsedScreeningTime[1].length();
        selectedScreeningTime.replace(startingIndex2, endingIndex2, newDate);
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void onDateSelected(ActionEvent event) {

    }

    @FXML
    void initialize() {}
}
