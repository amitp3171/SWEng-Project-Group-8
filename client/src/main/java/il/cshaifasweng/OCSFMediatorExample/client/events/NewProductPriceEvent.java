package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewProductPriceEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewProductPriceEvent(Message message) {
        this.message = message;
    }
}
