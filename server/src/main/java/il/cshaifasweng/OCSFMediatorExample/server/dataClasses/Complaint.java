package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalTime;
@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Customer creator;
    private LocalTime recievedAt;

    public Complaint(Customer creator, LocalTime recievedAt) {
        this.creator = creator;
        this.recievedAt = recievedAt;
    }

    public Complaint() {

    }

    public Customer getCreator() {
        return creator;
    }

    public void setCreator(Customer creator) {
        this.creator = creator;
    }

    public LocalTime getRecievedAt() {
        return recievedAt;
    }

    public void setRecievedAt(LocalTime recievedAt) {
        this.recievedAt = recievedAt;
    }
}
