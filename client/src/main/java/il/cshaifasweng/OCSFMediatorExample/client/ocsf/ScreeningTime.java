package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screeningTimes")
public class ScreeningTime {
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;
    // time of screening
    private LocalTime time;
    // day of the week of screening
    private String day;
    @ManyToOne(cascade = CascadeType.ALL)
    private Theater theater;

    public ScreeningTime(Branch branch, LocalTime time, String day, Theater theater) {
        this.branch = branch;
        this.time = time;
        this.day = day;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}