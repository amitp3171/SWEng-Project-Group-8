package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;

@Entity
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productClass;

    private double price;

    public Price() {}

    public Price(String productClass, double price) {
        this.productClass = productClass;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
