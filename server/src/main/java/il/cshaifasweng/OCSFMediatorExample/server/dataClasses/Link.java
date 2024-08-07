package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalTime;
import java.time.LocalDate;


@Entity
@Table(name = "links")
public class Link extends AbstractProduct{

    @ManyToOne
    private HomeMovie homeMovie;
    private LocalDate availableDay;
    private LocalTime availableHour;
    private LocalTime expiresAt;

    public Link(Customer owner, int price, HomeMovie homeMovie,LocalDate availableDay, LocalTime availableHour, LocalTime expiresAt) {
        super(owner, price);
        this.homeMovie = homeMovie;
        this.availableDay = availableDay;
        this.availableHour = availableHour;
        this.expiresAt = expiresAt;
    }

    public Link() {
        super();
    }

    public HomeMovie getHomeMovie() {
        return homeMovie;
    }

    public void setHomeMovie(HomeMovie homeMovie) {
        this.homeMovie = homeMovie;
    }

    public LocalTime getAvailableHour() {
        return availableHour;
    }

    public void setAvailableHour(LocalTime availableHour) {
        this.availableHour = availableHour;
    }

    public LocalDate getAvailableDay() {
        return availableDay;
    }

    public void setAvailableDay(LocalDate availableDay) { this.availableDay = availableDay; }

    public LocalTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
