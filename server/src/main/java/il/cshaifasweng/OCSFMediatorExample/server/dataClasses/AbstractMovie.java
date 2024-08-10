package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class AbstractMovie {
    protected String movieName;
    protected String producerName;
    @ElementCollection
    protected List<String> mainActors = new ArrayList<>();
    protected String description;
    protected String picture;

    // Constructor
    public AbstractMovie(String movieName, String producerName, List<String> mainActors, String description, String picture) {
        this.movieName = movieName;
        this.producerName = producerName;
        this.mainActors = mainActors;
        this.description = description;
        this.picture = picture;
    }

    public AbstractMovie() {}

    // Getters and setters
    public String getMovieName() {
        return this.movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getProducerName() {
        return this.producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public List<String> getMainActors() {
        return this.mainActors;
    }

    public void setMainActors(List<String> mainActors) {
        this.mainActors = mainActors;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


}
