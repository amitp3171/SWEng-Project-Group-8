package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewProductPriceEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewSeatListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private Button cardPurchaseButton;

    @FXML
    private Button subscriptionCardPurchaseButton;

    @FXML
    private Label statusLabel;

    private DataParser dataParser;
    UserDataManager userDataManager;

    private double productPrice;

    private String selectedBranch;
    private Map<String, String> selectedMovie;
    private Map<String, String> selectedScreening;
    private String selectedTheaterId;
    private ArrayList<Map<String, String>> seats = new ArrayList<>();
    private ArrayList<Map<String, String>> selectedSeats = new ArrayList<>();
    private ArrayList<String> selectedSeatIds = new ArrayList<>();

    public void setSelectedBranch(String branch) {
        this.selectedBranch = branch;
    }

    public void setSelectedMovie(Map<String, String> selectedMovie) throws IOException {
        this.selectedMovie = selectedMovie;

        String title = selectedMovie.get("movieName");
        String description = selectedMovie.get("description").substring(1, selectedMovie.get("description").length() - 1);
        String mainActors = selectedMovie.get("mainActors").substring(1, selectedMovie.get("mainActors").length() - 1);
        String producerName = selectedMovie.get("producerName");

        movieLabel.setText(title);
        movieSummaryLabel.setText(String.format("תקציר: %s", description));
        primaryActorsLabel.setText(String.format("שחקנים ראשיים: %s", mainActors));
        producerNameLabel.setText(String.format("מפיק: %s", producerName));

        movieLabel.setTooltip(new Tooltip(title));
        movieSummaryLabel.setTooltip(new Tooltip(description));
        primaryActorsLabel.setTooltip(new Tooltip(mainActors));
        producerNameLabel.setTooltip(new Tooltip(producerName));
    }

    public void setSelectedScreening(Map<String, String> selectedScreening) throws IOException {
        this.selectedScreening = selectedScreening;

        this.selectedTheaterId = selectedScreening.get("theaterId");

        this.theaterNumberLabel.setText(String.format("אולם %s", (1 + (Integer.parseInt(this.selectedTheaterId)-1) % 10)));

        this.screeningTimeAndDateLabel.setText(String.format("מועד הקרנה: %s, %s", selectedScreening.get("time"), selectedScreening.get("date")));

        this.requestSeatList();
    }

    public void requestSeatList() throws IOException {
        CinemaClient.sendToServer("get Seat list", this.selectedScreening.get("id"));
    }

    private void populateSeats() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                Circle seat = new Circle(10);

                boolean isTaken = Boolean.parseBoolean(this.seats.get(row*6 + col).get("isTaken"));

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
                if (isTaken) continue;

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
        int newSelectedSeatIdx = row*6 + col;

        boolean isOccupied = (seat.getFill() == Color.YELLOW);

        Map<String, String> newSelectedSeat = this.seats.get(newSelectedSeatIdx);

        if (isOccupied) {
            seat.setFill(Color.GREEN);
            this.selectedSeats.remove(newSelectedSeat);
            this.selectedSeatIds.remove(newSelectedSeat.get("id"));
        }
        else {
            seat.setFill(Color.YELLOW);
            this.selectedSeats.add(newSelectedSeat);
            this.selectedSeatIds.add(newSelectedSeat.get("id"));
        }

        cardPurchaseButton.setDisable(selectedSeatIds.isEmpty());
        subscriptionCardPurchaseButton.setDisable(selectedSeatIds.isEmpty());
    }

    @Subscribe
    public void onUpdateSeatListEvent(NewSeatListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String seat : receivedData)
                    this.seats.add(dataParser.parseSeat(seat));

                populateSeats();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("update Seats list request received");
        });
    }

    void handleDialogStatus(ButtonType status, boolean isCardPurchase) throws IOException {
        if (status == ButtonType.OK) {
            CinemaClient.sendToServer("create Ticket Purchase",
                    String.join(",",
                            CinemaClient.getUserDataManager().getGovId(),
                            this.selectedScreening.get("id"),
                            selectedSeatIds.toString(),
                            String.valueOf(this.productPrice),
                            isCardPurchase ? "Credit Card" : "Subscription Card"));
        }
        else {
            statusLabel.setText("תשלום בוטל");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
        }
    }

    void onSuccessfulPayment() {
        for (Map<String, String> seat : this.selectedSeats) {
            seat.replace("isTaken", String.valueOf(true));
        }

        populateSeats();

        this.selectedSeats.clear();
        this.selectedSeatIds.clear();

        statusLabel.setText("תשלום בוצע בהצלחה, ניתן לראות את הרכישה באיזור האישי");
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setVisible(true);
        cardPurchaseButton.setDisable(true);
        subscriptionCardPurchaseButton.setDisable(true);
    }

    // TODO: if successfull, set seat to red and remove listener
    @FXML
    void onCardPurchase(ActionEvent event) throws IOException {
        if (CinemaClient.getUserDataManager().isGuest()) {
              CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");
              return;
        }

        // TODO: move to next screen (payment selection - credit)
        ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("cardPaymentPrompt", this.productPrice, selectedSeats.size());

        handleDialogStatus(status, true);
    }

    @Subscribe
    public void onPurchaseStatusUpdate(NewPurchaseStatusEvent event) {
        // on event received
        Platform.runLater(() -> {
            String status = event.getMessage().getData();

            if (status.equals("payment successful")) {
                onSuccessfulPayment();
            }
        });
    }

    @FXML
    void onSubscriptionCardPurchase(ActionEvent event) throws IOException {
        if (CinemaClient.getUserDataManager().isGuest()) {
            CinemaClient.getDialogCreationManager().loadDialog("createCustomerCredentialsPrompt");
            return;
        }


        ButtonType status = CinemaClient.getDialogCreationManager().loadDialog("subscriptionCardPaymentPrompt", selectedSeats.size());

        handleDialogStatus(status, false);
    }
    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        EventBus.getDefault().unregister(this);
        if (userDataManager.isCustomer())
            CinemaClient.setContent("customerPersonalArea");
        else
            CinemaClient.setContent("employeePersonalArea");
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
    void onLogOut(ActionEvent event) throws IOException {
        CinemaClient.setContent("primary");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onUpdateProductPrice(NewProductPriceEvent event) {
        // on event received
        Platform.runLater(() -> {
            this.productPrice = Double.parseDouble(event.getMessage().getData());
            System.out.println("update Price request received");
        });
    }

    @FXML
    void initialize() throws IOException {
        userDataManager = CinemaClient.getUserDataManager();

        dataParser = CinemaClient.getDataParser();
        EventBus.getDefault().register(this);
        CinemaClient.sendToServer("get Product price", "Ticket");
    }
}
