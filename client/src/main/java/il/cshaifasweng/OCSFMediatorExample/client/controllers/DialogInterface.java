package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public interface DialogInterface {
    void setDialog(Dialog<ButtonType> dialog);

    void setData(Object... items);
}
