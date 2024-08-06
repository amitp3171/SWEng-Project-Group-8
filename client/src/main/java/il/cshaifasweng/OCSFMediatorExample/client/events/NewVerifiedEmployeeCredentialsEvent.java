package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewVerifiedEmployeeCredentialsEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewVerifiedEmployeeCredentialsEvent(Message message) {
        this.message = message;
    }
}
