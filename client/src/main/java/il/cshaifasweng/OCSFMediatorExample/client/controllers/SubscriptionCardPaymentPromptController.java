package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewPurchaseStatusEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewSubscriptionCardListEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewSubscriptionCardUsedEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SubscriptionCardPaymentPromptController implements DialogInterface {

    @FXML
    private Label amountOfTicketsLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button payButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<String> subscriptionCardSelectionComboBox;

    Dialog<ButtonType> dialog;

    DataParser dataParser;

    int amountOfTickets;

    ArrayList<Map<String, String>> subscriptionCardList = new ArrayList<>();

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public void setData(Object... params) {
        this.amountOfTickets = (int)params[0];

        amountOfTicketsLabel.setText(String.format("נבחרו %d מושבים", amountOfTickets));
    }

    @FXML
    void cancelPayment(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        dialog.setResult(ButtonType.CANCEL);
        dialog.close();
    }

    @FXML
    void onSubscriptionCardSelected(ActionEvent event) {
        payButton.setDisable(false);
        subscriptionCardSelectionComboBox.setPromptText(subscriptionCardSelectionComboBox.getSelectionModel().getSelectedItem().split(",")[0]);
    }

    @FXML
    void onPayPressed(ActionEvent event) throws IOException {
        int selectedIndex = subscriptionCardSelectionComboBox.getSelectionModel().getSelectedIndex();
        if (subscriptionCardList.get(selectedIndex).get("remainingTickets").equals("0")) {
            statusLabel.setText("לא נותרו כרטיסים למימוש בכרטיסיה זו");
            statusLabel.setVisible(true);
            return;
        }
        else if (Integer.parseInt(subscriptionCardList.get(selectedIndex).get("remainingTickets")) < this.amountOfTickets) {
            statusLabel.setText("לא נותרו מספיק כרטיסים למימוש בכרטיסיה זו");
            statusLabel.setVisible(true);
            return;
        }
        CinemaClient.sendToServer("use SubscriptionCard", String.join(",", subscriptionCardList.get(selectedIndex).get("id"), String.valueOf(this.amountOfTickets)));
    }

    @Subscribe
    public void onSubscriptionCardUseStatusEvent(NewSubscriptionCardUsedEvent event) {
        // on event received
        Platform.runLater(() -> {
            String status = event.getMessage().getData();

            if (status.equals("payment successful")) {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
        });
    }

    private void requestSubscriptionCardList() throws IOException {
        CinemaClient.sendToServer("get Customer SubscriptionCard list", CinemaClient.getUserDataManager().getGovId());
    }

    @Subscribe
    public void onSubscriptionCardListEvent(NewSubscriptionCardListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                this.subscriptionCardList.clear();
                ArrayList<String> messageData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);

                for (String subscriptionCard : messageData) {
                    this.subscriptionCardList.add(dataParser.parseSubscriptionCard(subscriptionCard));
                }

                initializeList();

                System.out.println("update SubscriptionCard list received");
            } catch (IOException e) {e.printStackTrace();}
        });
    }

    private void initializeList() {
        if (subscriptionCardList.isEmpty()) {
            statusLabel.setText("אין כרטיסיות זמינות למימוש");
            statusLabel.setVisible(true);
            return;
        }

        ArrayList<String> items = new ArrayList<>();

        for (Map<String, String> subscriptionCard : subscriptionCardList) {
            items.add(String.format("כרטיסיה #%s, נותרו %s כרטיסים", subscriptionCard.get("id"), subscriptionCard.get("remainingTickets")));
        }

        subscriptionCardSelectionComboBox.getItems().addAll(items);
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        dataParser = CinemaClient.getDataParser();
        requestSubscriptionCardList();
    }

}
