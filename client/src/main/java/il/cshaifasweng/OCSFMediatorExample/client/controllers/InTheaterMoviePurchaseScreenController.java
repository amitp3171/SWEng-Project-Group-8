package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewSeatListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleBooleanProperty;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class InTheaterMoviePurchaseScreenController {

    @FXML
    private ImageView movieImageView;

    @FXML
    private Label movieLabel;

    @FXML
    private Label movieSummaryLabel;

    @FXML
    private Label primaryActorsLabel;

    @FXML
    private Label producerNameLabel;

    @FXML
    private Label theaterNumberLabel;

    @FXML
    private Label screeningTimeAndDateLabel;

    @FXML
    private GridPane seatSelectionGridPane;

    @FXML
    private GridPane purchaseTicketButton;

    private String selectedBranch;
    private String selectedMovie;
    private String selectedScreening;
    private String selectedTheaterId;
    private ArrayList<String> seats;
    private ArrayList<String> selectedSeats = new ArrayList<>();

    public void setSelectedBranch(String branch) {
        this.selectedBranch = branch;
    }

    public void setSelectedMovie(String selectedMovie) throws IOException {
        this.selectedMovie = selectedMovie;

        // id, movieName, super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture()
        String[] parsedMovie = selectedMovie.split(",(?![^\\[]*\\])");

        String title = parsedMovie[1];
        String description = parsedMovie[2].substring(1, parsedMovie[2].length() - 1);
        String mainActors = parsedMovie[3].substring(1, parsedMovie[3].length() - 1);
        String producerName = parsedMovie[4];

        movieLabel.setText(title);
        movieSummaryLabel.setText(String.format("תקציר: %s", description));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", mainActors));
        producerNameLabel.setText(String.format("מפיק: %s", producerName));

        movieLabel.setTooltip(new Tooltip(title));
        movieSummaryLabel.setTooltip(new Tooltip(description));
        primaryActorsLabel.setTooltip(new Tooltip(mainActors));
        producerNameLabel.setTooltip(new Tooltip(producerName));
    }

    public void setSelectedScreening(String selectedScreening) throws IOException {
        this.selectedScreening = selectedScreening;

        String[] parsedScreeningTime = selectedScreening.split(",");

        this.selectedTheaterId = parsedScreeningTime[3];

        this.theaterNumberLabel.setText(String.format("אולם %s", (1 + (Integer.parseInt(parsedScreeningTime[3])-1) % 10)));

        this.screeningTimeAndDateLabel.setText(String.format("מועד הקרנה: %s, %s", parsedScreeningTime[2], parsedScreeningTime[1]));

        this.requestSeatList();
    }

    public void requestSeatList() throws IOException {
        // send request to server
        int messageId = CinemaClient.getNextMessageId();
        // message_text,branch_location,movie_id
        Message newMessage = new Message(messageId, "get Seat list");
        newMessage.setData(this.selectedTheaterId);
        CinemaClient.getClient().sendToServer(newMessage);
        System.out.println("Theater data request sent");
    }

    private void populateSeats() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Circle seat = new Circle(10);

                boolean isTaken = Boolean.parseBoolean(this.seats.get(row*6 + col).split(",")[1]);

                seat.setFill(isTaken ? Color.RED : Color.GREEN);
                seat.setStroke(Color.BLACK);

                // create a StackPane to hold the circle
                StackPane cellPane = new StackPane(seat);
                cellPane.setPrefSize(40, 40);

                GridPane.setMargin(cellPane, new Insets(2));
                seatSelectionGridPane.add(cellPane, col, row);

                final int finalRow = row;
                final int finalCol = col;

                // if taken, no listener!
                if (isTaken) return;

                cellPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        handleSeatClick(seat, finalRow, finalCol);
                    }
                });
            }
        }
    }

    /* handlers are created for previously non-taken seats */
    private void handleSeatClick(Circle seat, int row, int col) {
        String newSelectedSeat = Integer.toString(row*6 + col);

        boolean isOccupied = (seat.getFill() == Color.YELLOW);

        if (isOccupied) {
            seat.setFill(Color.GREEN);
            selectedSeats.remove(newSelectedSeat);
        }
        else {
            seat.setFill(Color.YELLOW);
            selectedSeats.add(newSelectedSeat);
        }
    }

    @Subscribe
    public void onUpdateSeatListEvent(NewSeatListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                this.seats = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                populateSeats();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("update Seats list request received");
        });
    }

    @FXML
    void purchaseTicketButton(ActionEvent event) throws IOException {
        if (CinemaClient.getUserDataManager().isGuest()) {
              CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");
        }
        // TODO: move to next screen (payment?) - do this as a function, will be used in login as well
        // TODO: make sure selectedSeats is not empty
    }

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        EventBus.getDefault().unregister(this);
        ScreeningListController screeningListController = CinemaClient.setContent("screeningList").getController();
        screeningListController.setSelectedBranch(this.selectedBranch);
        screeningListController.setSelectedMovie(this.selectedMovie, false);
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
    }
}
