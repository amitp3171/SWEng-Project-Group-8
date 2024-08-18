package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewPurchaseListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewPurchaseListEvent(Message message) {
        this.message = message;
    }
}