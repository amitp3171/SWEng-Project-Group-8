package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractProduct {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
//    @SequenceGenerator(name = "product_seq", sequenceName = "product_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer owner;

    private double price;

    public AbstractProduct(Customer owner, double price) {
        this.owner = owner;
        this.price = price;
    }

    public AbstractProduct() {}

    public int getId() {
        return id;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
