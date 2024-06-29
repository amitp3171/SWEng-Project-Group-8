package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import java.util.*;

public class InTheaterMovie extends AbstractMovie{
    private Map<Theater, List<ScreeningTime>[]> screenings;
    private List<ScreeningTime>[] days;

    public InTheaterMovie(String movieName, String producerName, List<String> mainActors, String description, String picture, List<ScreeningTime> screenings) {
        super(movieName, producerName, mainActors, description, picture);
        this.days = new List[7];
        this.screenings = new HashMap<>;
    }
}
