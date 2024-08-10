package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewRemovedComingSoonMovieEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewRemovedComingSoonMovieEvent(Message message) {
        this.message = message;
    }
}
