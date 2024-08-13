package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewTheaterIdListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ScreeningEditorController implements DialogInterface {
    @FXML
    private TextField screeningHourTF;

    @FXML
    private DatePicker screeningDatePicker;

    @FXML
    private ChoiceBox<String> theaterChoiceBox;

    @FXML
    private Label screeningExistsLabel;

    @FXML
    private Button removeScreeningButton;

    private Dialog<ButtonType> dialog;

    private ArrayList<String> theaterIds;

    private ArrayList<Map<String, String>> availableScreeningTimes;

    private Map<String, String> selectedScreeningTime;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) { // String screeningHour, Map<String, String> selectedScreeningTime, ArrayList<Map<String,String>> availableScreeningTimes;
        screeningHourTF.setText(((String)params[0]).split(",")[0]);
        this.selectedScreeningTime = (Map<String, String>) params[1];
        this.availableScreeningTimes = (ArrayList<Map<String, String>>) params[3];
        screeningDatePicker.setValue(LocalDate.parse(selectedScreeningTime.get("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        theaterChoiceBox.setValue(String.valueOf(1 + (Integer.parseInt(selectedScreeningTime.get("theaterId"))-1) % 10));

        if (this.selectedScreeningTime.get("additionalFields").equals("true")) {
            removeScreeningButton.setText("לא ניתן למחוק הקרנה זו כיוון שנקנו כרטיסים");
            removeScreeningButton.setDisable(true);
        }

        try {
            CinemaClient.sendToServer("get Theater ID list", (String)params[2]);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onReceivedTheaterIdList(NewTheaterIdListEvent event) {
        Platform.runLater(() -> {
            try {
                theaterIds = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @FXML
    void cancelScreeningUpdate(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void updateScreeningHour(ActionEvent event) throws ParseException {
        String newTime = screeningHourTF.getText();
        String newDate = screeningDatePicker.getValue().toString();
        String newTheaterId = theaterIds.get(Integer.parseInt(theaterChoiceBox.getValue())-1);

        for (Map<String, String> screening : availableScreeningTimes) {
            if (screening.get("time").equals(newTime) && screening.get("date").equals(newDate) && screening.get("theaterId").equals(newTheaterId)) {
                screeningExistsLabel.setVisible(true);
                return;
            }
        }

        if (newTime.equals(selectedScreeningTime.get("time")) && newDate.equals(selectedScreeningTime.get("date")) && newTheaterId.equals(selectedScreeningTime.get("theaterId"))) {
            dialog.setResult(ButtonType.CANCEL);
        }
        else {
            selectedScreeningTime.replace("time", screeningHourTF.getText());
            selectedScreeningTime.replace("date", screeningDatePicker.getValue().toString());
            selectedScreeningTime.replace("theaterId", newTheaterId);
            dialog.setResult(ButtonType.OK);
        }
        EventBus.getDefault().unregister(this);
        dialog.close();
    }

    //remove screening time if there are no tickets purchased
    @FXML
    void removeScreeningTime(ActionEvent event) {
        dialog.setResult(ButtonType.FINISH);
        EventBus.getDefault().unregister(this);
        dialog.close();
    }

    @FXML
    void initialize() {
        // register to EventBus
        EventBus.getDefault().register(this);
        theaterChoiceBox.getItems().addAll(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
    }
}
