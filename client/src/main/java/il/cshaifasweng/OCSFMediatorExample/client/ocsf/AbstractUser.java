package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;

    public AbstractUser(String firstName , String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}