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
}
