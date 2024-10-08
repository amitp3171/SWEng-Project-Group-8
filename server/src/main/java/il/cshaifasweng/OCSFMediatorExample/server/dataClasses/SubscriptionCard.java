package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "subscriptioncards")
public class SubscriptionCard extends AbstractProduct{

    private int remainingTickets;

    public SubscriptionCard(Customer owner, double price) {
        super(owner, price);
        this.remainingTickets = 20;
    }

    public SubscriptionCard() {}

    public int getRemainingTickets() {
        return this.remainingTickets;
    }

    public void setRemainingTickets(int remainingTickets) {
        this.remainingTickets = remainingTickets;
    }

    public void useTickets(int amount) {
        this.remainingTickets -= amount;
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(super.getId()), String.valueOf(this.remainingTickets));
    }
}
