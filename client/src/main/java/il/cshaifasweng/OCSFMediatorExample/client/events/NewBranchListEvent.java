package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewBranchListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewBranchListEvent(Message message) {
        this.message = message;
    }
}
