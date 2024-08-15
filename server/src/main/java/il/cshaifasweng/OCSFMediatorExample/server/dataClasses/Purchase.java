package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.ALL)
    private AbstractProduct relatedProduct;
    @ManyToOne
    private Customer customer;

    private String paymentMethod;
    private LocalDate paymentDate;
    private LocalTime paymentTime;

    public Purchase(AbstractProduct relatedProduct, Customer customer, String paymentMethod, LocalDate paymentDate, LocalTime paymentTime) {
        this.relatedProduct = relatedProduct;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.paymentTime = paymentTime.truncatedTo(ChronoUnit.MINUTES);
    }

    public Purchase() {

    }

    public AbstractProduct getRelatedProduct() {
        return relatedProduct;
    }

    public void setRelatedProduct(AbstractProduct relatedProduct) {
        this.relatedProduct = relatedProduct;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalTime paymentTime) {
        this.paymentTime = paymentTime.truncatedTo(ChronoUnit.MINUTES);
    }

    @Override
    public String toString() {
        return String.join(",", String.valueOf(this.id), String.valueOf(this.customer.getId()), String.valueOf(this.relatedProduct.getId()), this.paymentTime.toString(), this.paymentDate.toString(), this.paymentMethod);
    }
}
