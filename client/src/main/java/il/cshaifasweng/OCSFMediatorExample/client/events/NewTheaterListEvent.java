package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewTheaterListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewTheaterListEvent(Message message) {
        this.message = message;
    }
}
