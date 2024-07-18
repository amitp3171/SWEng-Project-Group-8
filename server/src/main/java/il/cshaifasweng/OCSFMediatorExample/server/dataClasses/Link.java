package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "links")
public class Link extends AbstractProduct{

    private String movieName;
    private String screeningTime;
    private LocalTime availableAt;
    private LocalTime expiresAt;

    public Link(Customer owner, int price, String movieName, String screeningTime, LocalTime availableAt, LocalTime expiresAt) {
        super(owner, price);
        this.movieName = movieName;
        this.screeningTime = screeningTime;
        this.availableAt = availableAt;
        this.expiresAt = expiresAt;
    }

    public Link() {
        super();
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getScreeningTime() {
        return screeningTime;
    }

    public void setScreeningTime(String screeningTime) {
        this.screeningTime = screeningTime;
    }

    public LocalTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(LocalTime availableAt) {
        this.availableAt = availableAt;
    }

    public LocalTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
