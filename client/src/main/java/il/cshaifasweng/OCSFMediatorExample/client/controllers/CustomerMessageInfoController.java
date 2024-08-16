package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import javafx.event.ActionEvent;
import javafx.scene.text.TextAlignment;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerMessageInfoController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label sendTimeLabel;

    DataParser dataParser;

    private Map<String, String> selectedMessage;

    public void setSelectedMessage(Map<String, String> selectedMessage) throws IOException {
        this.selectedMessage = selectedMessage;
        String title = selectedMessage.get("messageHeadline");
        String content = selectedMessage.get("messageBody").substring(1, selectedMessage.get("messageBody").length() - 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedSendTime = selectedMessage.get("sendTime").formatted(formatter);

        titleLabel.setText(title);
        contentLabel.setText(content);
        sendTimeLabel.setText(formattedSendTime);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        // get controller
        CinemaClient.setContent("customerMessageList").getController();
    }

    @FXML
    void initialize() {
        dataParser = CinemaClient.getDataParser();
    }

}