package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewPurchaseStatusEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewPurchaseStatusEvent(Message message) {
        this.message = message;
    }
}
