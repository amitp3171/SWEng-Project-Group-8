package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewScreeningTimeListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewScreeningTimeListEvent(Message message) {
        this.message = message;
    }
}
