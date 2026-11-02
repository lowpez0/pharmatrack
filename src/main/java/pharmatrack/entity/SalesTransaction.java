package pharmatrack.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_transaction")
public class SalesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_dt", nullable = false, updatable = false)
    private LocalDateTime transactionDt;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @OneToMany(mappedBy = "salesTransaction")
    private List<TransactionItem> transactionItems = new ArrayList<>();

    public SalesTransaction() {
    }

    @PrePersist
    protected void onCreate() {
        if (transactionDt == null) {
            transactionDt = LocalDateTime.now();
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(LocalDateTime transactionDt) {
        this.transactionDt = transactionDt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(List<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }
}