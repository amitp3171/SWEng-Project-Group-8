package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Entity
@Table(name = "comingSoonMovie")
public class ComingSoonMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate releaseDate;

    public ComingSoonMovie() {}

    public ComingSoonMovie(String movieName, String producerName, List<String> mainActors, String description, String picture,LocalDate releaseDate) {
        super(movieName, producerName, mainActors, description, picture);
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        String formattedString = (this.releaseDate != null) ? this.releaseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "No release date";

        return String.format("%s,%s,%s,%s,%s,%s,%s", this.id, super.getMovieName(), super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture(), formattedString);
    }

}