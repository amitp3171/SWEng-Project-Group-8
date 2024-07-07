package il.cshaifasweng.OCSFMediatorExample.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class CinemaClient extends Application {
    private static Stage appStage;
    private static Scene scene;
    private static SimpleClient client;
    private static ObjectMapper mapper = new ObjectMapper();

    private static int nextMessageId;

    @Override
    public void start(Stage stage) throws IOException {
    	EventBus.getDefault().register(this);
        mapper.registerModule(new JavaTimeModule());
    	client = SimpleClient.getClient();
        appStage = stage;
    	client.openConnection();
        client.sendToServer(new Message(0, "add client"));
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static int getNextMessageId() {
        nextMessageId++;
        return nextMessageId;
    }

    public static SimpleClient getClient() {
        return client;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    // set scene root
    static FXMLLoader setRoot(String fxml) throws IOException {
        // get fxml loader
        FXMLLoader loader = getFXMLLoader(fxml);
        // set root of scene to loader
        scene.setRoot(loadFXML(loader));
        // return used loader
        return loader;
    }

    // get fxml loader
    public static FXMLLoader getFXMLLoader(String fxml) throws IOException {
        return new FXMLLoader(CinemaClient.class.getResource(fxml + ".fxml"));
    }

    // load fxml given fxml path
    public static Parent loadFXML(String fxml) throws IOException {
        return getFXMLLoader(fxml).load();
    }

    // load fxml given fxml loader
    public static Parent loadFXML(FXMLLoader loader) throws IOException {
        return loader.load();
    }

    // set content given fxml path
    public static FXMLLoader setContent(String fxml) throws IOException {
        // set root of scene to the specified fxml
        FXMLLoader loader = setRoot(fxml);
        // update stage
        appStage.setScene(scene);
        // show stage
        appStage.show();
        // return used loader
        return loader;
    }

    public static void setWindowTitle(String title) {
        appStage.setTitle(title);
    }

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
		super.stop();
	}

    @Subscribe
    public void onMessageEvent(MessageEvent message) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
//        Platform.runLater(() -> {
//            Alert alert = new Alert(AlertType.INFORMATION,
//                    String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
//                            message.getMessage().getId(),
//                            message.getMessage().getMessage(),
//                            message.getMessage().getTimeStamp().format(dtf))
//            );
//            alert.setTitle("new message");
//            alert.setHeaderText("New Message:");
//            alert.show();
//        });
    }

	public static void main(String[] args) {
        launch();
    }
}