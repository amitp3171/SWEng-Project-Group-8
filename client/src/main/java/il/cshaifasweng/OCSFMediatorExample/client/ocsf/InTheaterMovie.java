package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import java.util.*;

public class InTheaterMovie extends AbstractMovie{
    private Map<Branch, ArrayList<ScreeningTime>[]> screenings;

    public InTheaterMovie(String movieName, String producerName, List<String> mainActors, String description, String picture) {
        super(movieName, producerName, mainActors, description, picture);

        List<ScreeningTime>[] days = new ArrayList[7];
        for (int i = 0; i < 7; i++) {
            days[i] = new ArrayList<ScreeningTime>();
        }
        this.screenings = new HashMap<>();
    }
}
