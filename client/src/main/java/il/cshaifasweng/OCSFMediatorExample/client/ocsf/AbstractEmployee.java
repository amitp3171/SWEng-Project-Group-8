package il.cshaifasweng.OCSFMediatorExample.client.ocsf;


import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

@MappedSuperclass

public abstract class AbstractEmployee extends AbstractUser {
    @ElementCollection // Use ElementCollection for collections of simple types
    private String username;
    private String password;


    public AbstractEmployee(String firstName, String lastName, String username, String password) {
        super(firstName, lastName);
        this.username = username;
        this.password = password;

    }
}
