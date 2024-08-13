package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCustomerMessageListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCustomerMessageListEvent(Message message) {
        this.message = message;
    }
}
