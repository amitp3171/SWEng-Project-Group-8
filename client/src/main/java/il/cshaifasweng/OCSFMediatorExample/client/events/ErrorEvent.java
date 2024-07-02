package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class ErrorEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public ErrorEvent(Message message) {
        this.message = message;
    }
}
