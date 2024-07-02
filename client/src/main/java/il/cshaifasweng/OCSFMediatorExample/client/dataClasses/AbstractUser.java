package il.cshaifasweng.OCSFMediatorExample.client.dataClasses;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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