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
        String newDate = screeningDatePicker.getValue().toString();
        String[] parsedScreeningTime = selectedScreeningTime.toString().split(",");
        parsedScreeningTime[2] = newTime;
        parsedScreeningTime[1] = newDate;
        selectedScreeningTime.delete(0, selectedScreeningTime.length());
        selectedScreeningTime.append(String.join(",", parsedScreeningTime));
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void onDateSelected(ActionEvent event) {

    }

    @FXML
    void initialize() {}
}
