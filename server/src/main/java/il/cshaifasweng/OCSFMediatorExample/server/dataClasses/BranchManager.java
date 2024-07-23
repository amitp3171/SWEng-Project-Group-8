package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "branchmanagers")
public class BranchManager extends AbstractEmployee{
    @OneToOne
    private Branch branch;

    public BranchManager(String firstName, String lastName, String username, String password, Branch branch) {
        super(firstName, lastName, username, password);
        this.branch = branch;
    }

    public BranchManager() {}

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
