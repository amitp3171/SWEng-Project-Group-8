package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "serviceemployees")
public class ServiceEmployee extends AbstractEmployee{
    public ServiceEmployee(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password);
    }

    public ServiceEmployee() {}

    public void giveRefund(){}
    public void handleComplaint(){}
}
