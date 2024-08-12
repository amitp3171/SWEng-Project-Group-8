package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewSubscriptionCardListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewSubscriptionCardListEvent(Message message) {
        this.message = message;
    }
}
