package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewPriceChangeListRequestEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewPriceChangeListRequestEvent(Message message) {
        this.message = message;
    }
}
