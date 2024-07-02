package il.cshaifasweng.OCSFMediatorExample.client.controllers;


import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

	@FXML
	void showMovieList(ActionEvent event) throws IOException {
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
