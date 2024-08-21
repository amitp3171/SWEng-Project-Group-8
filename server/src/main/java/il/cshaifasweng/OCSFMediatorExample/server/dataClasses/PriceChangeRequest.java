package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity()
public class PriceChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private ContentManager sender;

    private double newPrice;

    private String selectedProduct;

    private String status;

    public PriceChangeRequest() {}

    public PriceChangeRequest(ContentManager sender, String selectedProduct, double newPrice) {
        this.sender = sender;
        this.newPrice = newPrice;
        this.selectedProduct = selectedProduct;
        this.status = "pending";
    }

    public int getId() {
        return id;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public ContentManager getSender() {
        return sender;
    }

    public void setSender(ContentManager sender) {
        this.sender = sender;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(String selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public String toString() {
        return String.join(",", String.valueOf(this.id), String.valueOf(this.newPrice), this.selectedProduct, this.status);
    }
}
