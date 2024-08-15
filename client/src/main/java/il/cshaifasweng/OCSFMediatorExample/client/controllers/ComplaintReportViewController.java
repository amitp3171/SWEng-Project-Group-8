package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCompanyComplaintReportEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCompanySubscriptionCardLinkReportEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ComplaintReportViewController implements DialogInterface {

    @FXML
    private Label reportTitleLabel;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> complaintBarChart;

    Dialog<ButtonType> dialog;

    ArrayList<String> complaints;

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

    void initializeBarChart() {
        xAxis.setCategories(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 31).mapToObj(String::valueOf).collect(Collectors.toList())
        ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int i = 0; i < Math.min(complaints.size(), LocalDate.now().lengthOfMonth()); i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i+1), Integer.parseInt(complaints.get(i))));
        }

        complaintBarChart.getData().add(series);

        complaintBarChart.setCategoryGap(0);
        complaintBarChart.setBarGap(0.1);
    }

    @Subscribe
    public void onUpdateComplaintCompanyReport(NewCompanyComplaintReportEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                this.complaints =  CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                initializeBarChart();
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
        if (CinemaClient.getUserDataManager().getEmployeeType().equals("BranchManager")) {
//            CinemaClient.sendToServer("get Branch Complaint Report", CinemaClient.getUserDataManager().getAdditionalFields());
//            reportTitleLabel.setText("דוח תלונות (" + CinemaClient.getUserDataManager().getAdditionalFields() + ")");
        }
        else {
            CinemaClient.sendToServer("get Company Complaint Report");
            reportTitleLabel.setText("דוח תלונות רשתי");
        }
    }

}
