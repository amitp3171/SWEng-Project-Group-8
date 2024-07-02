package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "comingSoonMovie")
public class ComingSoonMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date releaseDate;

    public ComingSoonMovie() {}

    public ComingSoonMovie(String movieName, String producerName, List<String> mainActors, String description, String picture,Date releaseDate) {
        super(movieName, producerName, mainActors, description, picture);
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}