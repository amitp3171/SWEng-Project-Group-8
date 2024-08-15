package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewBranchComplaintReportEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewBranchComplaintReportEvent(Message message) {
        this.message = message;
    }
}
