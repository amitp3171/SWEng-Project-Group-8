package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEmployee extends AbstractUser {

    private String username;
    private String password;

    public AbstractEmployee(String firstName, String lastName, String username, String password) {
        super(firstName, lastName);
        this.username = username;
        this.password = password;
    }
}