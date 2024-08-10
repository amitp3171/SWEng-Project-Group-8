package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
public class Theater
{
    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int theaterID;
    private int theaterNumber;

    public Theater() {}

    public Theater(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    public int getTheaterID() {
        return theaterID;
    }

    public int getTheaterNumber() {
        return theaterNumber;
    }

    public void setTheaterNumber(int theaterNumber) {
        this.theaterNumber = theaterNumber;
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(theaterID), String.valueOf(theaterNumber));
    }
}
