package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private boolean isTaken;

    @ManyToOne(cascade = CascadeType.ALL)
    private Theater theater;

    public Seat() {
        this.isTaken = false;
    }

    public int getId() {
        return id;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
