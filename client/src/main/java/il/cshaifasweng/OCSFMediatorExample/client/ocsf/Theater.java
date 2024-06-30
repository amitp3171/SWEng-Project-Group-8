package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater
{
    // max seat capacity
    static int MAX_CAPACITY = 40;

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int theaterID;
    // list of seats
    @OneToMany(cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>(MAX_CAPACITY);


    public Theater() {
        // add MAX_CAPACITY new seats
        for (int i = 0; i < MAX_CAPACITY; i++) {
            seats.set(i, new Seat());
        }
    }

    public void selectSeats(List<Seat> selectedSeats) {
        // set selected seats as taken
        //        for (Seat seat : selectedSeats) {
        //            seat.setTaken(true);
        //        }
    }

    public int getTheaterID() {
        return theaterID;
    }

    public void setTheaterID(int theaterID) {
        this.theaterID = theaterID;
    }
}
