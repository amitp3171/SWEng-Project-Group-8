package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.ALL)
    private AbstractProduct relatedProduct;
    private String paymentMethod;
    private LocalTime paymentTime;

    public Purchase(AbstractProduct relatedProduct, String paymentMethod, LocalTime paymentTime) {
        this.relatedProduct = relatedProduct;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
    }

    public Purchase() {

    }

    public AbstractProduct getRelatedProduct() {
        return relatedProduct;
    }

    public void setRelatedProduct(AbstractProduct relatedProduct) {
        this.relatedProduct = relatedProduct;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalTime paymentTime) {
        this.paymentTime = paymentTime;
    }
}
