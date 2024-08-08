package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater
{
    // max seat capacity
    static int MAX_CAPACITY = 36;

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int theaterID;
    private int theaterNumber;
    // list of seats
    @OneToMany(cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>(MAX_CAPACITY);


    public Theater() {
        // add MAX_CAPACITY new seats
        for (int i = 0; i < MAX_CAPACITY; i++) {
            Seat seat = new Seat();
            seat.setTheater(this); // Set the theater reference in the seat
            seats.add(seat); // Use add instead of set
        }
    }

    public Theater(int theaterNumber) {
        this.theaterNumber = theaterNumber;
        // add MAX_CAPACITY new seats
        for (int i = 0; i < MAX_CAPACITY; i++) {
            Seat seat = new Seat();
            seat.setTheater(this); // Set the theater reference in the seat
            seats.add(seat); // Use add instead of set
        }
    }

//    public void selectSeats(List<Seat> selectedSeats) {
//        // set selected seats as taken
//        //        for (Seat seat : selectedSeats) {
//        //            seat.setTaken(true);
//        //        }
//    }

    public int getTheaterID() {
        return theaterID;
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    public Seat getSeat(int seatNumber) {
        return this.seats.get(seatNumber);
    }

    @Override
    public String toString() {
        return String.format("%s,%s", theaterID, theaterNumber);
    }
}
