package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractMovie;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "ComingSoonMovies")
public class ComingSoonMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate releaseDate;

    public ComingSoonMovie() {};

    public ComingSoonMovie(String movieName, String producerName, List<String> mainActors, String description, String picture, LocalDate releaseDate) {
        super(movieName, producerName, mainActors, description, picture);
        this.releaseDate = releaseDate;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
