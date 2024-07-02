package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class UpdateMessageEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public UpdateMessageEvent(Message message) {
        this.message = message;
    }
}
