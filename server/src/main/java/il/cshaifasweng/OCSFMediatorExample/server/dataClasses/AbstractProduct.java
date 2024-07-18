package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractProduct{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Customer owner;
    private int price;

    public AbstractProduct(Customer owner, int price) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
