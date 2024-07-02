package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.Branch;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.DatabaseBridge;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.InTheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.ScreeningTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;

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
        dialog.setResult(ButtonType.OK);
        dialog.close();
    }

    @FXML
    void initialize() {
        DatabaseBridge db = DatabaseBridge.getInstance();
        // get movies from DB
        availableBranches = db.getAll(Branch.class, false);
        // get branch locations
        String[] branchLocations = new String[availableBranches.size()];
        for (int i = 0; i < availableBranches.size(); i++) {
            branchLocations[i] = availableBranches.get(i).getLocation();
        }
        // add locations to listBox
        selectBranchListBox.getItems().addAll(branchLocations);
    }

}
