package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewAddedHomeMovieEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewAddedHomeMovieEvent(Message message) {
        this.message = message;
    }
}
