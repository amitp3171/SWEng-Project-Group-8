package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewTheaterIdListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewTheaterIdListEvent(Message message) {
        this.message = message;
    }
}
