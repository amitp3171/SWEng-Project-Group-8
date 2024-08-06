package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewComingSoonMovieListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewComingSoonMovieListEvent(Message message) {
        this.message = message;
    }
}
