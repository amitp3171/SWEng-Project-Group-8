package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "inTheaterMovies")
public class InTheaterMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<ScreeningTime> screenings = new ArrayList<>();

    public InTheaterMovie(String movieName, String producerName, List<String> mainActors, String description, String picture) {
        super(movieName, producerName, mainActors, description, picture);
    }

    public InTheaterMovie() {
        super();
    }
    public void addScreeningTime(ScreeningTime screeningTime) {
        screenings.add(screeningTime);
    }
    public void removeScreeningTime(ScreeningTime screeningTime) {
        screenings.remove(screeningTime);
    }
    public List<ScreeningTime> getScreenings(){ return this.screenings;  }
}