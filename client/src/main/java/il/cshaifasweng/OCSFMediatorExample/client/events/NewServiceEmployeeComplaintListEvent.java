package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewServiceEmployeeComplaintListEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewServiceEmployeeComplaintListEvent(Message message) {
        this.message = message;
    }
}
