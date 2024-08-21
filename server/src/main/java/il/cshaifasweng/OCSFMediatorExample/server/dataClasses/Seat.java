package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int seatNumber;

    private boolean isTaken;

    @ManyToOne/*(cascade = CascadeType.ALL)*/
    private Theater theater;

    @ManyToOne/*(cascade = CascadeType.ALL)*/
    private ScreeningTime screeningTime;

    public Seat() {
        this.isTaken = false;
    }

    public Seat(int seatNumber) {
        this.isTaken = false;
        this.seatNumber = seatNumber;
    }

    public int getId() {
        return id;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public void setScreening(ScreeningTime screeningTime) {
        this.screeningTime = screeningTime;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", this.id, this.isTaken);
    }
}
