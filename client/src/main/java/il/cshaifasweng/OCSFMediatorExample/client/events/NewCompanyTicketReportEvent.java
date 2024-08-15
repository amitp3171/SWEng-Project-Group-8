package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCompanyTicketReportEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCompanyTicketReportEvent(Message message) {
        this.message = message;
    }
}
