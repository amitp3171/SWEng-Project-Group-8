package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Message implements Serializable {
    int id;
    String message;
    String data;
    private List<?> dataList;

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
        this.data = null;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public <T> List<T> getDataList() {
        return (List<T>) dataList;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }
}

