package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCreateCustomerCredentialsEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCreateCustomerCredentialsEvent(Message message) {
        this.message = message;
    }
}
