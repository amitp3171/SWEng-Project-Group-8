package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewSeatListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewSeatListEvent(Message message) {
        this.message = message;
    }
}
