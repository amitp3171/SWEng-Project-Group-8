package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Customer creator;
    private LocalTime receivedAt;
    private LocalDate receivedDate;
    private String title;
    private String complaintContent;
    private String response;

    @OneToOne(cascade = CascadeType.ALL)
    private Purchase relatedPurchase;

    public Complaint(Purchase relatedPurchase, Customer creator, LocalTime receivedAt, String title, String complaintContent) {
        this.relatedPurchase = relatedPurchase;
        this.creator = creator;
        this.receivedAt = receivedAt.truncatedTo(ChronoUnit.MINUTES);
        this.receivedDate = LocalDate.now();
        this.title = title;
        this.complaintContent = complaintContent;
        this.response = "[טרם התקבלה תגובה מהצוות]";
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
        this.receivedAt = receivedAt.truncatedTo(ChronoUnit.MINUTES);
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
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

    public Purchase getRelatedPurchase() {
        return this.relatedPurchase;
    }

    public void setRelatedPurchase(Purchase relatedPurchase) {
        this.relatedPurchase = relatedPurchase;
    }

    @Override
    public String toString(){
        return String.join(",", String.valueOf(this.id) , this.receivedAt.toString(), this.receivedDate.toString(), this.title, this.complaintContent, this.response, this.relatedPurchase.getRelatedProduct().getClass().getName().substring(55));
    }

}
