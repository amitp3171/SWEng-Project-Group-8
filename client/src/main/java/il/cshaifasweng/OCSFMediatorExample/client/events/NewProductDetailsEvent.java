package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewProductDetailsEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewProductDetailsEvent(Message message) {
        this.message = message;
    }
}
