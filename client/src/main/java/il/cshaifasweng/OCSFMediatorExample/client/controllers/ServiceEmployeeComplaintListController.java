
package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewServiceEmployeeComplaintListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ServiceEmployeeComplaintListController {

    UserDataManager userDataManager;

    private ArrayList<Map<String, String>> complaints = new ArrayList<>();

    private DataParser dataParser;

    @FXML
    private ListView<String> complaintListView;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("employeePersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    public void getComplaintList() throws IOException {
        CinemaClient.sendToServer("get Complaint list for ServiceEmployee");
    }


    @Subscribe
    public void onGetServiceEmployeeComplaintListEvent(NewServiceEmployeeComplaintListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String complaintContent : receivedData) {
                    System.out.println(complaintContent);
                    complaints.add(dataParser.parseComplaint(complaintContent));
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

    //TODO: calculate time since complaint created
    void initializeList() {
        complaintListView.getItems().clear();
        // get movie names
        String[] complaintTitles = new String[complaints.size()];
        for (int i = 0; i < complaintTitles.length; i++) {
            System.out.println(complaints.get(i).toString());
            complaintTitles[i] = complaints.get(i).get("title");
            complaintTitles[i] = String.format("תלונה #%s : %s", complaints.get(i).get("id"), "\"" + complaintTitles[i] + "\"");
            //debug
            System.out.println(complaintTitles[i]);
//            String remainTime = calculateRemainingTime(complaints.get(i).get("receivedAt"));
//            complaintTitles += "זמן נותר לטיפול: " + remainTime;
        }
        // display movies
        complaintListView.getItems().addAll(complaintTitles);
    }

//    private String calculateRemainingTime(String receivedAt) {
//
//    }

    //TODO: fix when creat class ServiceEmployeeComplaintInfoController
    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (complaintListView.getSelectionModel().getSelectedItem() == null) return;

        // get complaint object
        int selectedIndex = complaintListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedComplaint = complaints.get(selectedIndex);

        // set selected complaint
        ServiceEmployeeComplaintInfoController serviceEmployeeComplaintInfo = CinemaClient.setContent("serviceEmployeeComplaintInfo").getController();
        serviceEmployeeComplaintInfo.setSelectedComplaint(selectedComplaint);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        userDataManager = CinemaClient.getUserDataManager();
        getComplaintList();
    }
}
