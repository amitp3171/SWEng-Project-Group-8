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
    public static String ip;
    private static Stage appStage;
    private static Scene scene;
    private static SimpleClient client;
    private static ObjectMapper mapper = new ObjectMapper();
    private static UserDataManager userDataManager;
    private static DataParser dataParser;
    private static DialogCreationManager dialogCreationManager;

    private static int nextMessageId;

    @Override
    public void start(Stage stage) throws IOException {
    	EventBus.getDefault().register(this);
        mapper.registerModule(new JavaTimeModule());
    	client = SimpleClient.getClient();
        userDataManager = UserDataManager.getInstance();
        dataParser = DataParser.getInstance();
        dialogCreationManager = DialogCreationManager.getInstance();
        appStage = stage;
    	client.openConnection();
        client.sendToServer(new Message(0, "add client"));
        scene = new Scene(loadFXML("primary"), 640, 640);
        stage.setScene(scene);
        stage.show();
    }

    private static int getNextMessageId() {
        nextMessageId++;
        return nextMessageId;
    }

    public static SimpleClient getClient() {
        return client;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static UserDataManager getUserDataManager() {
        return userDataManager;
    }

    public static DataParser getDataParser() {
        return dataParser;
    }

    public static void setUserDataManager(String id, String customerFirstName, String customerLastName, String customerGovId) {
        userDataManager.setId(id);
        userDataManager.setFirstName(customerFirstName);
        userDataManager.setLastName(customerLastName);
        userDataManager.setGovId(customerGovId);
    }

    public static void setUserDataManager(String id, String employeeFirstName, String employeeLastName, String employeeUserName, String employeeRole) {
        userDataManager.setId(id);
        userDataManager.setFirstName(employeeFirstName);
        userDataManager.setLastName(employeeLastName);
        userDataManager.setEmployeeUserName(employeeUserName);
        userDataManager.setEmployeeType(employeeRole);
    }

    public static void setUserDataManager(String id, String employeeFirstName, String employeeLastName, String employeeUserName, String employeeRole, String additionalFields) {
        userDataManager.setId(id);
        userDataManager.setFirstName(employeeFirstName);
        userDataManager.setLastName(employeeLastName);
        userDataManager.setEmployeeUserName(employeeUserName);
        userDataManager.setEmployeeType(employeeRole);
        userDataManager.setAdditionalFields(additionalFields);
    }

    public static DialogCreationManager getDialogCreationManager() {
        return dialogCreationManager;
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

    public static void sendToServer(String messageHeader) throws IOException {
        Message newMessage = new Message(getNextMessageId(), messageHeader);
        getClient().sendToServer(newMessage);
        System.out.printf("%s request sent.%n", messageHeader);
    }

    public static void sendToServer(String messageHeader, String messageData) throws IOException {
        Message newMessage = new Message(getNextMessageId(), messageHeader);
        newMessage.setData(messageData);
        getClient().sendToServer(newMessage);
        System.out.printf("%s request sent.%n", messageHeader);
    }

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
		super.stop();
        System.exit(0);
	}

    @Subscribe
    public void onMessageEvent(MessageEvent message) {

    }

    public static Stage getStage() {
        return appStage;
    }

	public static void main(String[] args) {
        if(args.length != 0){
            ip = args[0];
        }
        else {
            ip = "10.0.0.5";
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                userDataManager.logoutUser();
            }
        }));
        launch();
    }
}