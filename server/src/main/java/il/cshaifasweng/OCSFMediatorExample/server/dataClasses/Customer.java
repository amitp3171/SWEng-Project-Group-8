package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customers")
public class Customer extends AbstractUser {

    private String idNum;
    // TODO: Add list for each product

    public Customer(String firstName, String lastName, String idNum) {
        super(firstName, lastName);
        this.idNum = idNum;
    }

    public Customer() {
        super("John", "Doe");
    }

    // TODO: Add cancelPurchase and makeAComplaint method
}
