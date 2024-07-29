package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@Entity
@Table(name = "customers")
public class Customer extends AbstractUser {
    @OneToMany(cascade = CascadeType.ALL)
    private List<Ticket> ownedTickets = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<SubscriptionCard> ownedSubscriptions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Link> ownedLinks = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Complaint> activeComplaints = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Purchase> purchaseHistory = new ArrayList<>();

    private String govId;

    public Customer(){
        super();
    }

    public Customer(String govId) {
        super();
        this.govId = govId;
    }

    public Customer(String firstName, String lastName, String govId) {
        super(firstName, lastName);
        this.govId = govId;
    }

    public void addTicketToList (Ticket ticket){
        this.ownedTickets.add(ticket);
    }
    public void addSubscriptionCardToList (SubscriptionCard subscriptionCard){
        this.ownedSubscriptions.add(subscriptionCard);
    }
    public void addLinkToList (Link link){
        this.ownedLinks.add(link);
    }
    public void addComplaintToList (Complaint complaint){
        this.activeComplaints.add(complaint);
    }
    public void addPurchaseToList (Purchase purchase){
        this.purchaseHistory.add(purchase);
    }

    public void cancelPurchase(Purchase purchase) {}
    public Complaint makeAComplaint(Complaint complaint) {
        return complaint;
    }
}
