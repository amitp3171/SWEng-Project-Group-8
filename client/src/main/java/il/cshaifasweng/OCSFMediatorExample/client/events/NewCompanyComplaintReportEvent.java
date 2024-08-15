package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCompanyComplaintReportEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCompanyComplaintReportEvent(Message message) {
        this.message = message;
    }
}
