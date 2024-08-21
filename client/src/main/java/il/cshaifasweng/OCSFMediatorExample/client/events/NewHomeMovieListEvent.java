package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewHomeMovieListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewHomeMovieListEvent(Message message) {
        this.message = message;
    }
}