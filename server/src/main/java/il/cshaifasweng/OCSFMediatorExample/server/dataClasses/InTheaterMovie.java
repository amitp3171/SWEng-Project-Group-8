package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inTheaterMovies")
public class InTheaterMovie extends AbstractMovie {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(mappedBy = "inTheaterMovie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScreeningTime> screenings = new ArrayList<>();
    @ManyToMany
    private List<Branch> branches = new ArrayList<>();

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

    public void addBranch(Branch branch) {
        branches.add(branch);
    }

    public void removeBranch(Branch branch) {
        branches.remove(branch);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ScreeningTime> getScreenings(){ return this.screenings;  }
    public void setScreenings(List<ScreeningTime> screenings){
        this.screenings = screenings;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(this.id), super.getMovieName(), super.getDescription(), super.getMainActors().toString(), super.getProducerName(), super.getPicture());
    }
}