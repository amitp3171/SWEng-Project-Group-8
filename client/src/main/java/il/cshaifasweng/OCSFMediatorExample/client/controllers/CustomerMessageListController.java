
package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewCustomerMessageListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerMessageListController {

    UserDataManager userDataManager;

    private ArrayList<Map<String, String>> messages = new ArrayList<>();

    private DataParser dataParser;

    @FXML
    private ListView<String> messageListView;

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("customerPersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    public void getMessageList() throws IOException {
        CinemaClient.sendToServer("get Customer Message list", userDataManager.getGovId());
    }

    @Subscribe
    public void onGetCustomerMessageListEvent(NewCustomerMessageListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String messageContent : receivedData) {
                    System.out.println(messageContent);
                    messages.add(dataParser.parseCustomerMessage(messageContent));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("customerMessageList request received");
        });
    }

    void initializeList() {
        messageListView.getItems().clear();
        // get movie names
        String[] messageTitles = new String[messages.size()];
        for (int i = 0; i < messageTitles.length; i++) {
            messageTitles[i] = messages.get(i).get("messageHeadline");
            messageTitles[i] = String.format("הודעה #%s : %s", messages.get(i).get("id"), "\"" + messageTitles[i] + "\"");
        }
        // display movies
        messageListView.getItems().addAll(messageTitles);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (messageListView.getSelectionModel().getSelectedItem() == null) return;

        // get message object
        int selectedIndex = messageListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedMessage = messages.get(selectedIndex);

        // set selected message
        CustomerMessageInfoController messageInfoController = CinemaClient.setContent("customerMessageInfo").getController();
        messageInfoController.setSelectedMessage(selectedMessage);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        userDataManager = CinemaClient.getUserDataManager();
        getMessageList();
    }
}
