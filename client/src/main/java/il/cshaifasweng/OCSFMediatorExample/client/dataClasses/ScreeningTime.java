package il.cshaifasweng.OCSFMediatorExample.client.dataClasses;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "screeningTimes")
public class ScreeningTime {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;

    // enum for day of the week
    public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    @Enumerated(EnumType.STRING)
    private Day day;

    // time of screening
    private LocalTime time;

    @ManyToOne(cascade = CascadeType.ALL)
    private Theater theater;

    public ScreeningTime(Branch branch, Day day, LocalTime time, Theater theater) {
        this.branch = branch;
        this.day = day;
        this.time = time;
        this.theater = theater;
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

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}