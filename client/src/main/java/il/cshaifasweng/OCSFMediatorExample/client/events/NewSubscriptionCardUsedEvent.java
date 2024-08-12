package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewSubscriptionCardUsedEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewSubscriptionCardUsedEvent(Message message) {
        this.message = message;
    }
}
