package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewRemovedHomeMovieEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewRemovedHomeMovieEvent(Message message) {
        this.message = message;
    }
}
