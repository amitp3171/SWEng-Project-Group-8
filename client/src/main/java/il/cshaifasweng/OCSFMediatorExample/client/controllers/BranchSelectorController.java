package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BranchSelectorController {
    @FXML
    private ComboBox<String> selectBranchListBox;

    private Dialog<ButtonType> dialog;

    private ArrayList<String> availableBranches;

    private String selectedBranch;

    private String firstName;
    private String lastName;
    private String govId;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setCustomerData(String firstName, String lastName, String govId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.govId = govId;
//        welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", firstName, lastName));
    }

    @FXML
    void chooseBranch(ActionEvent event) {
        // update selected branch
        selectedBranch = availableBranches.get(selectBranchListBox.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void cancelSelectBranch(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CLOSE);
        dialog.close();
    }

    @FXML
    void updateBranch(ActionEvent event) throws IOException {
        // avoid empty selection
        if (selectedBranch == null) return;
        // create controller
        InTheaterMovieListController inTheaterMovieListController = CinemaClient.setContent("inTheaterMovieList").getController();
        inTheaterMovieListController.setCustomerData(this.firstName, this.lastName, this.govId);
        // set selected branch
        inTheaterMovieListController.setSelectedBranch(selectedBranch);
        // close dialog
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @Subscribe
    public void onUpdateBranchEvent(NewBranchListEvent event) {
        Platform.runLater(() -> {
            try {
                availableBranches = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // add locations to listBox
            selectBranchListBox.getItems().addAll(availableBranches);
            // refresh listBox
            System.out.println("Branch request received");
        });
    }

    @FXML
    void initialize() throws IOException {
        // register to EventBus
        EventBus.getDefault().register(this);
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get Branch list");
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("Branch request sent");
    }

}
