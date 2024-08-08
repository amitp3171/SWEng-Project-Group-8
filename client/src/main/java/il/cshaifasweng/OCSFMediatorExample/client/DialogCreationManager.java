package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.DialogInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class DialogCreationManager {

    private static DialogCreationManager instance;

    private DialogCreationManager() {}

    public static synchronized DialogCreationManager getInstance() {
        if (instance == null) {
            instance = new DialogCreationManager();
        }
        return instance;
    }

    public static <T extends DialogInterface> ButtonType loadDialog(String fxml, Object... params) throws IOException {
        FXMLLoader dialogLoader = CinemaClient.getFXMLLoader(fxml);
        DialogPane dialogPane = (DialogPane) CinemaClient.loadFXML(dialogLoader);

        T dialogController = dialogLoader.getController();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(dialogPane);
        dialogController.setDialog(dialog);
        dialogController.setData(params);

        // create hidden close button to support the close button (X)
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        dialog.showAndWait();

        // unregister dialog in case X button was pressed
        if (EventBus.getDefault().isRegistered(dialogController)) EventBus.getDefault().unregister(dialogController);

        return dialog.getResult();
    }


}
