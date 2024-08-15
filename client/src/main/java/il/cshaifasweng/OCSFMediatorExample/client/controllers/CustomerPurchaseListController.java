
package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import il.cshaifasweng.OCSFMediatorExample.client.CinemaClient;
import il.cshaifasweng.OCSFMediatorExample.client.DataParser;
import il.cshaifasweng.OCSFMediatorExample.client.UserDataManager;
import il.cshaifasweng.OCSFMediatorExample.client.events.NewComplaintListEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerPurchaseListController {

    @FXML
    ListView<String> customerPurchasesListView;


    UserDataManager userDataManager;

    DataParser dataParser;

    private ArrayList<Map<String, String>> customerPurchases = new ArrayList<>();

    @FXML
    void onCloseProgram(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onGoBack(ActionEvent event) throws IOException {
        EventBus.getDefault().unregister(this);
        CinemaClient.setContent("customerPersonalArea");
    }

    @FXML
    void onLogOut(ActionEvent event) throws IOException {
        EventBus.getDefault().unregister(this);
        CinemaClient.setContent("primary");
    }

    @FXML
    void showPersonalArea(ActionEvent event) throws IOException {
        this.onGoBack(event);
    }

    public void getUserPurchaseHistory() throws IOException {
        CinemaClient.sendToServer("get Customer Purchase list", userDataManager.getGovId());
    }

    @Subscribe
    public void onGetCustomerPurchaseListEvent(NewComplaintListEvent event) {
        // on event received
        Platform.runLater(() -> {
            try {
                customerPurchases.clear();
                ArrayList<String> receivedData = CinemaClient.getMapper().readValue(event.getMessage().getData(), ArrayList.class);
                for (String purchase : receivedData) {
                    customerPurchases.add(dataParser.parsePurchase(purchase));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            // update list
            initializeList();
            System.out.println("Customer Purchase list request received");
        });
    }

    private void initializeList() {
        customerPurchasesListView.getItems().clear();

        ArrayList<String> items = new ArrayList<>();

        for (Map<String, String> purchase : customerPurchases) {
            items.add(String.format("רכישה #%s", purchase.get("id")));
        }

        customerPurchasesListView.getItems().addAll(items);
    }

    @FXML
    void onItemSelected(MouseEvent event) throws IOException {
        // if selected item is null
        if (customerPurchasesListView.getSelectionModel().getSelectedItem() == null) return;

        int selectedIndex = customerPurchasesListView.getSelectionModel().getSelectedIndex();
        Map<String, String> selectedPurchase = customerPurchases.get(selectedIndex);

        ButtonType result = CinemaClient.getDialogCreationManager().loadDialog("customerPurchaseView", selectedPurchase);

        if (result.equals(ButtonType.OK)) {
            customerPurchasesListView.getItems().remove(selectedIndex);
            customerPurchases.remove(selectedIndex);
        }

        customerPurchasesListView.getSelectionModel().clearSelection();
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        userDataManager = CinemaClient.getUserDataManager();
        dataParser = CinemaClient.getDataParser();
        getUserPurchaseHistory();
    }
}
