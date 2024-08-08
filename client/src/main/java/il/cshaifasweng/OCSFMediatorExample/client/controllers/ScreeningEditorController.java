package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.text.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

public class ScreeningEditorController implements DialogInterface {
    @FXML
    private TextField screeningHourTF;
    private Dialog<ButtonType> dialog;

    private StringBuilder selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) { // String screeningHour, StringBuilder selectedScreeningTime
        screeningHourTF.setText(((String)params[0]).split(",")[0]);
        this.selectedScreeningTime = (StringBuilder) params[1];
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

        selectedScreeningTime.delete(0, selectedScreeningTime.length());
        selectedScreeningTime.append(newTime);

        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void initialize() {}
}
