package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.*;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewBranchListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.DatabaseBridge;
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

    private List<Branch> availableBranches;

    private Branch selectedBranch;

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
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
            availableBranches = event.getMessage().getDataList();
            // get branch locations
            String[] branchLocations = new String[availableBranches.size()];
            for (int i = 0; i < availableBranches.size(); i++) {
                branchLocations[i] = availableBranches.get(i).getLocation();
            }
            System.out.println(event.getBranches());
            // add locations to listBox
            selectBranchListBox.getItems().addAll(branchLocations);
            // refresh listBox
            System.out.println("Branch request received");
        });
    }

    @FXML
    void initialize() throws IOException {
//        DatabaseBridge db = DatabaseBridge.getInstance();
//        // get movies from DB
//        availableBranches = db.getAll(Branch.class, false);

        EventBus.getDefault().register(this);
        // get next message ID
        int messageId = CinemaClient.getNextMessageId();
        // generate message
        // Message<Branch> newMessage = new Message<>(messageId, "get branch list", Branch.class);
        Message newMessage = new Message(messageId, "get branch list");
        // send request to server
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("Branch request sent");
    }

}
