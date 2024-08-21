package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewCompanySubscriptionCardLinkReportEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewCompanySubscriptionCardLinkReportEvent(Message message) {
        this.message = message;
    }
}
