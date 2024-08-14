package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@Entity()
//public class EmployeeMessage {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//    private String messageHeadline;
//    private String messageBody;
//    private LocalDateTime sendTime;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    private AbstractEmployee sender;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    private AbstractEmployee receiver;
//
//
//    public EmployeeMessage() {}
//
//    public EmployeeMessage(String messageHeadline, String messageBody, LocalDateTime sendTime, AbstractEmployee sender, AbstractEmployee receiver) {
//        this.messageHeadline = messageHeadline;
//        this.messageBody = messageBody;
//        this.sendTime = sendTime;
//        this.sender = sender;
//        this.receiver = receiver;
//
//    }
//
//
//    public String getMessageHeadline() {
//        return messageHeadline;
//    }
//    public void setMessageHeadline(String messageHeadline) {
//        this.messageHeadline = messageHeadline;
//    }
//    public String getMessageBody() {
//        return messageBody;
//    }
//    public void setMessageBody(String messageBody) {
//        this.messageBody = messageBody;
//    }
//    public LocalDateTime getSendTime() {
//        return sendTime;
//    }
//    public void setSendTime(LocalDateTime sendTime) {
//        this.sendTime = sendTime;
//    }
//
//    public String toString() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedSendTime = this.sendTime.format(formatter);
//        return String.join(",", String.valueOf(this.id), this.messageHeadline, this.messageBody, formattedSendTime);
//    }
//}
