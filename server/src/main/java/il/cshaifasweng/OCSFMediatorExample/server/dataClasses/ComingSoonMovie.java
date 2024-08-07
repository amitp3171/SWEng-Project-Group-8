package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


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

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", this.id, super.getMovieName(), super.getDescription(), super.getMainActors(), super.getProducerName(), super.getPicture());
    }

}