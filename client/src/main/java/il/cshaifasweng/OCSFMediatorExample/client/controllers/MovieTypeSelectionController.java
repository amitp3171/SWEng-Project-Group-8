package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;

public class MovieTypeSelectionController {

    @FXML
    private Label welcomeUserLabel;

    private boolean isGuest = false;

    private String firstName;
    private String lastName;
    private String govId;

    void setCustomerData() {
        this.isGuest = true;
        welcomeUserLabel.setText("ברוח הבא, אורח!");
    }

    void setCustomerData(String firstName, String lastName, String govId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.govId = govId;
        welcomeUserLabel.setText(String.format("%s, %s %s!", "ברוך הבא", firstName, lastName));
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
    }

    @FXML
    void showComingSoonMovieList(ActionEvent event) {

    }

    @FXML
    void showHomeMovieList(ActionEvent event) {

    }

    @FXML
    void showInTheaterMovieList(ActionEvent event) throws IOException {
        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("branchSelector");
        DialogPane branchSelectorDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        BranchSelectorController branchSelectorController = dialogLoader.getController();
        branchSelectorController.setCustomerData(this.firstName, this.lastName, this.govId);

        // create new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(branchSelectorDialogPane);
        branchSelectorController.setDialog(dialog);

        // create hidden close button to support the close button (X)
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        // show dialog
        dialog.showAndWait();

        // unregister dialog in case X button was pressed
        if (EventBus.getDefault().isRegistered(branchSelectorController)) EventBus.getDefault().unregister(branchSelectorController);
    }

    @FXML
    void initialize() {}

}
