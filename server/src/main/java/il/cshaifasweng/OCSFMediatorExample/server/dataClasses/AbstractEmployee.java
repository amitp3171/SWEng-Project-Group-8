package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;
import javax.persistence.*;


//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEmployee extends AbstractUser {

    private String username;
    private String password;

    public AbstractEmployee(String firstName, String lastName, String username, String password) {
        super(firstName, lastName);
        this.username = username;
        this.password = password;
    }

    public AbstractEmployee() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}