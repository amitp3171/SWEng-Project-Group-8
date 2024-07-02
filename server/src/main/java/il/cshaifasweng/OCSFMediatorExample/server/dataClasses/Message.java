package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

//public class Message<T> implements Serializable {
//    int id;
//    private LocalDateTime timeStamp;
//    private String message;
//    private Class<T> requestedClass;
//    private List<T> data;
//
//    public Message(int id, LocalDateTime timeStamp, String message) {
//        this.id = id;
//        this.timeStamp = timeStamp;
//        this.message = message;
//    }
//
//    public Message(int id, String message) {
//        this.id = id;
//        this.timeStamp = LocalDateTime.now();
//        this.message = message;
//        this.data = null;
//    }
//
//    public Message(int id, String message, Class<T> requestedClass) {
//        this.id = id;
//        this.message = message;
//        this.requestedClass = requestedClass;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public LocalDateTime getTimeStamp() {
//        return timeStamp;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setTimeStamp(LocalDateTime timeStamp) {
//        this.timeStamp = timeStamp;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Class<T> getRequestedClass() {
//        return requestedClass;
//    }
//
//    public List<T> getData() {
//        return data;
//    }
//
//    public void setData(List<T> data) {
//        this.data = data;
//    }
//}

public class Message implements Serializable {
    int id;
    private LocalDateTime timeStamp;
    private String message;
    private Class<?> requestedClass;
    private List<?> dataList;
    private List<?> data;

    public Message(int id, LocalDateTime timeStamp, String message) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;
    }

    public Message(int id, String message) {
        this.id = id;
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.data = null;
    }

    public Message(int id, String message, Class<?> requestedClass) {
        this.id = id;
        this.message = message;
        this.requestedClass = requestedClass;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Class<?> getRequestedClass() {
        return requestedClass;
    }

    public <T> List<T> getData() {
        return (List<T>) data;
    }

    public <T> List<T> getDataList() {
        return (List<T>) dataList;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }
}