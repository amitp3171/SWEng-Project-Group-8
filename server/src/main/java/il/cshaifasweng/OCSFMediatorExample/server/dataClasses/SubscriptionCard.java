package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "subscriptioncards")
public class SubscriptionCard extends AbstractProduct{

    private int remainingTickets;

    public SubscriptionCard(Customer owner, int price, int remainingTickets) {
        super(owner, price);
        this.remainingTickets = remainingTickets;
    }

    public SubscriptionCard() {}

    public int getRemainingTickets() {
        return remainingTickets;
    }

    public void setRemainingTickets(int remainingTickets) {
        this.remainingTickets = remainingTickets;
    }
}
