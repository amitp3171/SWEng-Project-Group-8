package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "screeningTimes")
public class ScreeningTime {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;

    @ManyToOne(cascade = CascadeType.ALL)
    InTheaterMovie inTheaterMovie;

    // date of screening
    private LocalDate date;
    // time of screening
    private LocalTime time;

    @ManyToOne(cascade = CascadeType.ALL)
    private Theater theater;

    public ScreeningTime(Branch branch, LocalDate date, LocalTime time, Theater theater, InTheaterMovie inTheaterMovie) {
        this.branch = branch;
        this.date = date;
        this.time = time;
        this.theater = theater;
        this.inTheaterMovie = inTheaterMovie;
    }

    public ScreeningTime() {}

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
        this.time = time;
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
    }

    public InTheaterMovie getInTheaterMovie() {
        return inTheaterMovie;
    }

    public void setInTheaterMovie(InTheaterMovie inTheaterMovie) {
        this.inTheaterMovie = inTheaterMovie;
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(id), date.toString(), time.toString(), String.valueOf(theater.getTheaterID()));
    }
}