package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.*;
import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class MovieTypeSelectionController {

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
    }

    @FXML
    void initialize() {

    }

}
