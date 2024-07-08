package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.text.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

public class ScreeningEditorController {
    @FXML
    private TextField screeningHourTF;
    private Dialog<ButtonType> dialog;

    private StringBuilder selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setScreeningHour(String screeningHour) {
        screeningHourTF.setText(screeningHour.split(",")[0]);
    }

    public void setSelectedScreeningTime(StringBuilder selectedScreeningTime) {
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

        selectedScreeningTime.delete(0, selectedScreeningTime.length());
        selectedScreeningTime.append(newTime);

        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void initialize() {}
}
