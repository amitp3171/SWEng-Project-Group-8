package il.cshaifasweng.OCSFMediatorExample.client.events;

import il.cshaifasweng.OCSFMediatorExample.client.dataClasses.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

import java.util.List;

public class NewBranchListEvent {
    //private Message<Branch> message;
    private Message message;

    private List<Branch> branches;

//    public Message<Branch> getMessage() {
//        return message;
//    }

    public Message getMessage() {
        return message;
    }

    public List<Branch> getBranches() {
        return branches;
    }

//    public NewBranchListEvent(Message<Branch> message) {
//        this.message = message;
//        this.branches = message.getData();
//    }

    public NewBranchListEvent(Message message) {
        this.message = message;
        this.branches = message.getDataList();
    }
}
