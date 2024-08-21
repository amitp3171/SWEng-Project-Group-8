package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchTicketReportEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCompanySubscriptionCardLinkReportEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCompanyTicketReportEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SubscriptionCardLinkReportViewController implements DialogInterface {

    @FXML
    private Label reportTitleLabel;

    @FXML
    private ListView<String> reportListView;

    Dialog<ButtonType> dialog;

    ArrayList<String> purchases;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        //
    }

    @FXML
    void onOkPressed(ActionEvent event) {
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    void initializeList() {
        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < Math.min(purchases.size(), LocalDate.now().lengthOfMonth()); i++) {
            items.add(LocalDate.now().getYear() + "/" + LocalDate.now().getMonthValue() + "/" + (i+1) + ": " + purchases.get(i) + " רכישות");
        }

        reportListView.getItems().addAll(items);
    }

    @Subscribe
    public void onUpdateCompanyReport(NewCompanySubscriptionCardLinkReportEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                this.purchases =  CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                initializeList();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("get Company Ticket Report request received");
        });
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);

        CinemaClient.sendToServer("get Company SubscriptionCardLink Report");
        reportTitleLabel.setText("דוח כרטיסיות וחבילות צפייה רשתי");

    }

}
