package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPriceChangeListRequestEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewServiceEmployeeComplaintListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class EmployeeRequestListController {

    @FXML
    private ListView<String> employeeRequestsListView;

    DataParser dataParser;

    private ArrayList<Map<String, String>> requests = new ArrayList<>();

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("employeePersonalArea");
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        if (requests.isEmpty()) {return;}

        Map<String, String> selectedRequest = requests.get(employeeRequestsListView.getSelectionModel().getSelectedIndex());

        ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("approveEmployeeRequestView", selectedRequest);

        if (status.equals(ButtonType.OK)) {
            int selectedIndex = employeeRequestsListView.getSelectionModel().getSelectedIndex();
            employeeRequestsListView.getItems().set(selectedIndex, "בקשה #" + selectedRequest.get("id") + " : " + parseStatus(selectedRequest.get("status")));
        }
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        CinemaClient.setContent("employeePersonalArea");
    }

    @Subscribe
    public void onNewPriceChangeRequestEvent(NewPriceChangeListRequestEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                requests.clear();
                employeeRequestsListView.getItems().clear();

                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String complaintContent : receivedData) {
                    requests.add(dataParser.parsePriceChangeRequest(complaintContent));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("serviceEmployeeComplaintList request received");
        });
    }

    private String parseStatus(String status) {
        switch (status) {
            case "pending":
                return "מחכה לאישור";
            case "accepted":
                return "אושרה";
            default:
                return "נדחתה";
        }
    }

    void initializeList() {
        ArrayList<String> items = new ArrayList<>();

        for (Map<String, String> request : requests) {
            items.add("בקשה #" + request.get("id") + " : " + parseStatus(request.get("status")));
        }

        employeeRequestsListView.getItems().addAll(items);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        CinemaClient.sendToServer("get Employee Price change requests");
    }

}
