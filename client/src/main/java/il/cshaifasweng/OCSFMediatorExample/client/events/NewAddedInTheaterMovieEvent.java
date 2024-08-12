package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewAddedInTheaterMovieEvent {

    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewAddedInTheaterMovieEvent(Message message) {
        this.message = message;
    }
}
