package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewComplaintListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewComplaintListEvent(Message message) {
        this.message = message;
    }
}
