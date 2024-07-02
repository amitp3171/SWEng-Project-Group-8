package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
public class Branch {
    // location is the primary key
    @Id
    private String location;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Theater> theaterList = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    private List<InTheaterMovie> inTheaterMovieList = new ArrayList<>();
    public Branch() {}

    public Branch(String location) {
        this.location = location;
    }

    public List<Theater> getTheaterList() {
        return theaterList;
    }

    public void addTheaterToList(Theater theater) {
        this.theaterList.add(theater);
    }

    public void addInTheaterMovieToList(InTheaterMovie inTheaterMovie) {
        this.inTheaterMovieList.add(inTheaterMovie);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<InTheaterMovie> getInTheaterMovieList() {
        return inTheaterMovieList;
    }

    public void setInTheaterMovieList(List<InTheaterMovie> inTheaterMovieList) {
        this.inTheaterMovieList = inTheaterMovieList;
    }
}