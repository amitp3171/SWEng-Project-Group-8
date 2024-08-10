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
    private LocalTime receivedAt;
    private String complaintContent;



    public Complaint(Customer creator, LocalTime receivedAt,String complaintContent1) {
        this.creator = creator;
        this.receivedAt = receivedAt;
        this.complaintContent = complaintContent1;
    }

    public Complaint() {

    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public Customer getCreator() {
        return creator;
    }

    public void setCreator(Customer creator) {
        this.creator = creator;
    }

    public LocalTime getreceivedAt() {
        return receivedAt;
    }

    public void setreceivedAt(LocalTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
