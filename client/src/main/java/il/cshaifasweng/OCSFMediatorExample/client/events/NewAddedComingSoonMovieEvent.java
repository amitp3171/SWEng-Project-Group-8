package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewAddedComingSoonMovieEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewAddedComingSoonMovieEvent(Message message) {
        this.message = message;
    }
}
