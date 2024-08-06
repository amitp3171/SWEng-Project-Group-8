package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewInTheaterMovieListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class InTheaterMovieListController {
    @FXML
    private ListView<String> movieListView;

    @FXML
    private Label branchNameLabel;

    private String firstName;
    private String lastName;
    private String govId = null;

    private boolean isGuest = false;

    private String employeeUserName = null;
    private String employeeType = null;

    private ArrayList<String> allInTheaterMovies;

    private ArrayList<String> inTheaterMovies;

    private String selectedBranch;

    private boolean forceRefresh;

    void setCustomerData() {
        this.isGuest = true;
    }

    void setCustomerData(String firstName, String lastName, String govId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.govId = govId;
    }

    void setEmployeeData(String firstName, String lastName, String userName, String employeeType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeUserName = userName;
        this.employeeType = employeeType;
        this.isGuest = false;
    }

    public void setSelectedBranch(String branchLocation) throws IOException {
        selectedBranch = branchLocation;
        branchNameLabel.setText(selectedBranch);
        requestInTheaterMovieList(false);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (movieListView.getSelectionModel().getSelectedItem() == null) return;

        // get screeningTime object
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        String selectedMovie = inTheaterMovies.get(selectedIndex);

        // load screening list selector
        FXMLLoader screeningLoader = CinemaClient.setContent("screeningList");

        // set selected movie
        ScreeningListController screeningController = screeningLoader.getController();
        if (this.isGuest)
            screeningController.setCustomerData();
        else if (this.employeeType == null)
            screeningController.setCustomerData(this.firstName, this.lastName, this.govId);
        else
            screeningController.setEmployeeData(this.firstName, this.lastName, this.employeeUserName, this.employeeType);
        screeningController.setSelectedBranch(selectedBranch);
        screeningController.setSelectedMovie(selectedMovie, forceRefresh);

        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        MovieTypeSelectionController movieTypeSelectionController = CinemaClient.setContent("movieTypeSelection").getController();
        if (isGuest)
            movieTypeSelectionController.setCustomerData();
        else if (this.employeeType == null)
            movieTypeSelectionController.setCustomerData(this.firstName, this.lastName, this.govId);
        else
            movieTypeSelectionController.setEmployeeData(this.firstName, this.lastName, this.employeeUserName, this.employeeType);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onAddScreening(ActionEvent event) throws IOException {
        // load dialog fxml
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader("screeningCreator");
        DialogPane screeningCreatorDialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        // get controller
        ScreeningCreatorController screeningCreatorController = dialogLoader.getController();
        screeningCreatorController.setData(this.allInTheaterMovies, this.selectedBranch);

        // create new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(screeningCreatorDialogPane);
        screeningCreatorController.setDialog(dialog);

        // create hidden close button to support the close button (X)
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        // show dialog
        dialog.showAndWait();

        // unregister dialog in case X button was pressed
        if (EventBus.getDefault().isRegistered(screeningCreatorController)) EventBus.getDefault().unregister(screeningCreatorController);
    }

    private void requestInTheaterMovieList(boolean forceRefresh) throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        Message newMessage = new Message(messageId, "get InTheaterMovie list");
        newMessage.setData(String.format("%s,%s", selectedBranch, forceRefresh));
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("InTheaterMovie request sent");
    }

    void initializeList() {
        movieListView.getItems().clear();
        // get movie names
        String[] movieNames = new String[inTheaterMovies.size()];
        for (int i = 0; i < movieNames.length; i++) {
            movieNames[i] = inTheaterMovies.get(i).split(",")[1];
        }
        // display movies
        movieListView.getItems().addAll(movieNames);
    }

    @Subscribe
    public void onUpdateInTheaterMovieEvent(NewInTheaterMovieListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                inTheaterMovies = new ArrayList<>();
                allInTheaterMovies = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String movie : allInTheaterMovies) {
                    boolean isInBranch = Boolean.parseBoolean(movie.split(",")[7]);
                    if (isInBranch)
                        inTheaterMovies.add(movie);
                }

                // update list
                if (!inTheaterMovies.isEmpty())
                    initializeList();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("InTheaterMovie request received");
        });
    }

    @FXML
    void onRefreshList(ActionEvent event) throws IOException {
        this.forceRefresh = true;
        requestInTheaterMovieList(true);
    }

    @FXML
    void initialize() throws IOException {
        forceRefresh = false;
        // register to EventBus
        EventBus.getDefault().register(this);
    }
}