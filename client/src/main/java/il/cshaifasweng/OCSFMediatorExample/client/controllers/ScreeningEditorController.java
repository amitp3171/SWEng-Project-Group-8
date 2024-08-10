package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Dictionary;

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

    private Dictionary<String, String> selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) { // String screeningHour, Dictionary<String, String> selectedScreeningTime
        screeningHourTF.setText(((String)params[0]).split(",")[0]);
        this.selectedScreeningTime = (Dictionary<String, String>) params[1];
        screeningDatePicker.setValue(LocalDate.parse(selectedScreeningTime.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @FXML
    void cancelScreeningUpdate(ActionEvent event) {
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void updateScreeningHour(ActionEvent event) throws ParseException {
        String newTime = screeningHourTF.getText();
        String newDate = screeningDatePicker.getValue().toString();

        if (newTime.equals(selectedScreeningTime.get("time")) && newDate.equals(selectedScreeningTime.get("date"))) {
            dialog.setResult(ButtonType.CANCEL);
        }
        else {
            selectedScreeningTime.put("time", screeningHourTF.getText());
            selectedScreeningTime.put("date", screeningDatePicker.getValue().toString());
            dialog.setResult(ButtonType.OK);
        }

        dialog.close();
    }

    @FXML
    void initialize() {}
}
