package il.cshaifasweng.OCSFMediatorExample.client.dataClasses;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "homeMovie")
public class HomeMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String link;

    public HomeMovie() {}

    public HomeMovie(String movieName, String producerName, List<String> mainActors, String description, String picture, String link) {
        super(movieName, producerName, mainActors, description, picture);
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}