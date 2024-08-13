package il.cshaifasweng.OCSFMediatorExample.client.controllers;


import java.io.IOException;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DialogCreationManager;
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
		CinemaClient.getDialogCreationManager().loadDialog("customerLoginPrompt");
	}

	@FXML
	void showMovieListEmployee(ActionEvent event) throws IOException {
		CinemaClient.getDialogCreationManager().loadDialog("employeeLoginPrompt");
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
	void initialize() throws IOException {
		if (!CinemaClient.getUserDataManager().isGuest())
			CinemaClient.sendToServer("logout user", CinemaClient.getUserDataManager().getId());
		CinemaClient.getUserDataManager().resetData();
		EventBus.getDefault().register(this);
	}
}
