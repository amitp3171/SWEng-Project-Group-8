package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "companymanagers")
public class CompanyManager extends AbstractEmployee{

    public CompanyManager(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password);
    }

    public CompanyManager() {
        super();
    }

    public void handlePriceUpdateRequest(){}
}
