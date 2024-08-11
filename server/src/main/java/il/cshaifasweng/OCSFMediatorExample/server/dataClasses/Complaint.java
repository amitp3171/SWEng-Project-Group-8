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
    private String title;
    private String complaintContent;
    private String response;


    public Complaint(Customer creator, LocalTime receivedAt, String title, String complaintContent) {
        this.creator = creator;
        this.receivedAt = receivedAt;
        this.title = title;
        this.complaintContent = complaintContent;
        this.response = "NULL";
    }

    public Complaint() {

    }

    public Customer getCreator() {
        return creator;
    }

    public void setCreator(Customer creator) {
        this.creator = creator;
    }

    public LocalTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString(){
        return String.join(",",String.valueOf(this.id) , this.title, this.complaintContent, this.response);
    }
}
