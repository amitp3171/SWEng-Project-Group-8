package il.cshaifasweng.OCSFMediatorExample.client;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;
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
