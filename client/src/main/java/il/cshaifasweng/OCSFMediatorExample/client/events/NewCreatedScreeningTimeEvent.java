package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCreatedScreeningTimeEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCreatedScreeningTimeEvent(Message message) {
        this.message = message;
    }
}
