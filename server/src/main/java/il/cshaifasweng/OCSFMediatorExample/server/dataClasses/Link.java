package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Entity
@Table(name = "links")
public class Link extends AbstractProduct{

    @ManyToOne
    private HomeMovie homeMovie;
    private LocalDate availableDay;
    private LocalTime availableHour;
    private LocalDate expiresDate;
    private LocalDateTime expiresAt;
    private String link;

    public Link(Customer owner, double price, HomeMovie homeMovie, LocalDate availableDay, LocalTime availableHour) {
        super(owner, price);
        this.homeMovie = homeMovie;
        this.availableDay = availableDay;
        this.availableHour = availableHour.truncatedTo(ChronoUnit.MINUTES);
        this.expiresAt = LocalDateTime.of(availableDay, this.availableHour).plusHours((long) homeMovie.getMovieLength() + 1);
        this.link = String.format("www.thesirtiamovies.co.il/%s/%s", owner.getId(), homeMovie.getMovieName().replace(" ", "").substring(1,3));

//        if (availableHour.plusHours((long) homeMovie.getMovieLength() + 1).isBefore(availableHour)) {
//            expiresDate = availableDay.plusDays(1);
//        }
//        else {
//            expiresDate = availableDay;
//        }
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
        this.availableHour = availableHour.truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalDate getAvailableDay() {
        return availableDay;
    }

    public void setAvailableDay(LocalDate availableDay) { this.availableDay = availableDay; }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDate getExpiresDate() {
        return this.expiresDate;
    }

    public void setExpiresDate(LocalDate expiresDate) {
        this.expiresDate = expiresDate;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(super.getId()),
                String.valueOf(super.getPrice()),
                this.homeMovie.getMovieName(),
                this.availableDay.toString(),
                this.availableHour.toString(),
                this.expiresAt.toString(),
                this.link);
    }
}

