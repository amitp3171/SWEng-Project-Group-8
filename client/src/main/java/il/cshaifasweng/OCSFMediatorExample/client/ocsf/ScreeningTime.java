package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScreeningTime {
    private LocalTime times;
    private int theaterNumber;

    public ScreeningTime(LocalTime times, int theaterNumber) {
        this.times = times;
        this.theaterNumber = theaterNumber;
    }

    public LocalTime getTimes() {
        return times;
    }

    public void setTimes(LocalTime times) {
        this.times = times;
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }
}