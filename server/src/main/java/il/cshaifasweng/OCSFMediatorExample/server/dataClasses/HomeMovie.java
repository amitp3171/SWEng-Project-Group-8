package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "homeMovie")
public class HomeMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double movieLength;

    public HomeMovie() {}

    public HomeMovie(String movieName, String producerName, List<String> mainActors, String description, String picture, double movieLength) {
        super(movieName, producerName, mainActors, description, picture);
        this.movieLength = movieLength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMovieLength() { return movieLength; }

    public void setMovieLength(double movieLength) { this.movieLength = movieLength; }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(this.id), super.getMovieName(), super.getDescription(), super.getMainActors().toString(), super.getProducerName(), super.getPicture());
    }
}