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
    private List<ScreeningTime> screenings;

    public InTheaterMovie(String movieName, String producerName, List<String> mainActors, String description, String picture, List<ScreeningTime> screenings) {
        super(movieName, producerName, mainActors, description, picture);
        this.screenings = screenings;
    }

    public InTheaterMovie() {
        super();
    }
}
