
package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            Map<String, String> complaint = complaints.get(i);
            String title = complaint.get("title");
            String remainingTime = calculateRemainingTime(complaint.get("receivedAt"), complaint.get("receivedDate"));
            if(complaint.get("response").equals("[טרם התקבלה תגובה מהצוות]")){
                if (remainingTime.equals("0"))
                    complaintTitles[i] = ("תלונה #" + complaint.get("id") + ": " + "\"" + title.substring(1, title.length() - 1) + "\"" + " (" + complaint.get("receivedAt") + ", " + complaint.get("receivedDate") + ")" + " חריגה בזמן הטיפול!");
                else {
                    complaintTitles[i] = ("תלונה #" + complaint.get("id") + ": " + "\"" + title.substring(1, title.length() - 1) + "\"" + " (" + complaint.get("receivedAt") + ", " + complaint.get("receivedDate") + ")" + " הזמן שנותר לטיפול: " + remainingTime);
                }
            }
            else {
//            complaintTitles[i] = String.format("תלונה #%s : %s (%s(", complaints.get(i).get("id"), "\"" + complaintTitles[i].substring(1, complaintTitles[i].length() - 1) + "\"", complaints.get(i).get("receivedAt"));
                complaintTitles[i] = ("תלונה #" + complaint.get("id") + ": " + "\"" + title.substring(1, title.length() - 1) + "\"" + " (" + complaint.get("receivedAt") + ", " + complaint.get("receivedDate") + ")" + " התלונה טופלה");
            }
        }
        // display movies
        complaintListView.getItems().addAll(complaintTitles);
    }

    //calculate the remaining time to handle the complaint
    private String calculateRemainingTime(String receivedAt, String receiveDate) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalTime time = LocalTime.parse(receivedAt, timeFormatter);
        LocalDate date = LocalDate.parse(receiveDate, dateFormatter);

        LocalDateTime receivedDateTime = LocalDateTime.of(date, time);
        LocalDateTime deadlineDateTime = receivedDateTime.plusHours(24);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, deadlineDateTime);
        if (duration.isNegative()) {
            return "0";
        } else {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            return String.format("%02d:%02d", hours, minutes);
        }

    }


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
        serviceEmployeeComplaintInfo.setComplaintStatus(calculateRemainingTime(selectedComplaint.get("receivedAt"), selectedComplaint.get("receivedDate")));

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
