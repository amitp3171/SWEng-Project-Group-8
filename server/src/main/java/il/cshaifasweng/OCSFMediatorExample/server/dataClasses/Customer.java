package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(cascade = CascadeType.ALL)
    private List<CustomerMessage> messages = new ArrayList<>();

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
    public void removeTicketFromList (Ticket ticket){
        this.ownedTickets.remove(ticket);
    }
    public void addSubscriptionCardToList (SubscriptionCard subscriptionCard){
        this.ownedSubscriptions.add(subscriptionCard);
    }
    public void removeSubscriptionCardFromList (SubscriptionCard subscriptionCard){
        this.ownedSubscriptions.remove(subscriptionCard);
    }
    public void addLinkToList (Link link){
        this.ownedLinks.add(link);
    }
    public void removeLinkFromList (Link link){
        this.ownedLinks.remove(link);
    }
    public void addComplaintToList (Complaint complaint){
        this.activeComplaints.add(complaint);
    }
    public void addPurchaseToList (Purchase purchase){
        this.purchaseHistory.add(purchase);
    }
    public void removePurchaseFromList (Purchase purchase){
        this.purchaseHistory.remove(purchase);
    }

    public void addMessageToList (CustomerMessage message){
        this.messages.add(message);
    }
    public void removeMessageFromList (CustomerMessage message){
        this.messages.remove(message);
    }
    public List<CustomerMessage> getMessages(){
        return this.messages;
    }

    public List<Purchase> getPurchaseHistory(){
        return this.purchaseHistory;
    }

    public boolean isAvailableSubscriptionCardOwned() {
        for (SubscriptionCard subscriptionCard : this.ownedSubscriptions) {
            if (subscriptionCard.getRemainingTickets()>0)
                return true;
        }
        return false;
    }


    public void cancelPurchase(Purchase purchase) {}
    public Complaint makeAComplaint(Complaint complaint) {
        return complaint;
    }
}
