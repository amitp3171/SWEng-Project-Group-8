package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screeningTimes")
public class ScreeningTime {
    // max seat capacity
    static int MAX_CAPACITY = 36;

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne/*(cascade = CascadeType.ALL)*/
    private Branch branch;

    @ManyToOne/*(cascade = CascadeType.ALL)*/
    InTheaterMovie inTheaterMovie;

    // date of screening
    private LocalDate date;
    // time of screening
    private LocalTime time;

    @ManyToOne/*(cascade = CascadeType.ALL)*/
    private Theater theater;

    // list of seats
    @OneToMany(fetch = FetchType.EAGER)/*(cascade = CascadeType.ALL)*/
    private List<Seat> seats = new ArrayList<>(MAX_CAPACITY);

    public ScreeningTime(Branch branch, LocalDate date, LocalTime time, Theater theater, InTheaterMovie inTheaterMovie) {
        this.branch = branch;
        this.date = date;
        this.time = time.truncatedTo(ChronoUnit.MINUTES);
        this.theater = theater;
        this.inTheaterMovie = inTheaterMovie;
        instantiateSeats();
    }

    public ScreeningTime() {
        instantiateSeats();
    }

    private void instantiateSeats() {
        // add MAX_CAPACITY new seats
        for (int i = 0; i < MAX_CAPACITY; i++) {
            Seat seat = new Seat(i+1);
            seat.setTheater(this.theater); // Set the theater reference in the seat
            seat.setScreening(this);
            seats.add(seat); // Use add instead of set
        }
    }

    public int getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time.truncatedTo(ChronoUnit.MINUTES);
    }

    public void setTime(String time) {
        this.time = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;

        if (theater != null) {
            for (Seat seat : seats) {
                seat.setTheater(theater);
            }
        }
    }

    public InTheaterMovie getInTheaterMovie() {
        return inTheaterMovie;
    }

    public void setInTheaterMovie(InTheaterMovie inTheaterMovie) {
        this.inTheaterMovie = inTheaterMovie;
    }

    public List<Seat> getSeats() {
        return this.seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public Seat getSeat(int i) {
        return this.seats.get(i);
    }

    public void removeSeat(Seat seat) {
        this.seats.remove(seat);
    }

    @Override
    public String toString() {
        if (theater == null)
            return "";

        return String.join(",", String.valueOf(id), date.toString(), time.toString(), String.valueOf(theater.getTheaterID()));
    }
}