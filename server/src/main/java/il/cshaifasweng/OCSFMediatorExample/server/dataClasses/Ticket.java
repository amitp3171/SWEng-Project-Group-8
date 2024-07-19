package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;
import javax.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket extends AbstractProduct {

    private String movieName;

    @ManyToOne(cascade = CascadeType.ALL)
    private ScreeningTime screeningTime;

    @OneToOne(cascade = CascadeType.ALL)
    private Seat seat;

    public Ticket(Customer owner, int price, String movieName, ScreeningTime screeningTime, Seat seat) {
        super(owner, price);
        this.movieName = movieName;
        this.screeningTime = screeningTime;
        this.seat = seat;
    }

    public Ticket() {}

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public ScreeningTime getScreeningTime() {
        return screeningTime;
    }

    public void setScreeningTime(ScreeningTime screeningTime) {
        this.screeningTime = screeningTime;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
