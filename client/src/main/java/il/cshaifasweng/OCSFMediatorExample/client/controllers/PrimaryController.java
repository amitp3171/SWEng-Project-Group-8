package il.cshaifasweng.OCSFMediatorExample.client.controllers;


import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

	@FXML
	void showMovieListCustomer(ActionEvent event) throws IOException {
		// load dialog fxml
		FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("customerLoginPrompt");
		DialogPane customerLoginDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

		// get controller
		CustomerLoginController customerLoginController = dialogLoader.getController();

		// create new dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().setContent(customerLoginDialogPane);
		customerLoginController.setDialog(dialog);

		// create hidden close button to support the close button (X)
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.setVisible(false);

		// show dialog
		dialog.showAndWait();

		// unregister dialog in case X button was pressed
		if (EventBus.getDefault().isRegistered(customerLoginController)) EventBus.getDefault().unregister(customerLoginController);
	}

	@FXML
	void showMovieListEmployee(ActionEvent event) throws IOException {
		// load dialog fxml
		FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("employeeLoginPrompt");
		DialogPane employeeLoginDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

		// get controller
		EmployeeLoginController employeeLoginController = dialogLoader.getController();

		// create new dialog
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.getDialogPane().setContent(employeeLoginDialogPane);
		employeeLoginController.setDialog(dialog);

		// create hidden close button to support the close button (X)
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.setVisible(false);

		// show dialog
		dialog.showAndWait();

		// unregister dialog in case X button was pressed
		if (EventBus.getDefault().isRegistered(employeeLoginController)) EventBus.getDefault().unregister(employeeLoginController);
	}

	@FXML
	void showMovieListGuest(ActionEvent event) throws IOException {
		CinemaClient.setContent("movieTypeSelection");
	}

	@FXML
	void onCloseProgram(ActionEvent event) {
		System.exit(0);
	}

	@Subscribe
	public void setDataFromServerTF(MessageEvent event) {
		//
	}

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);
	}
}
