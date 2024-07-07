package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.InTheaterMovie;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class NewInTheaterMovieListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewInTheaterMovieListEvent(Message message) {
        this.message = message;
    }
}
