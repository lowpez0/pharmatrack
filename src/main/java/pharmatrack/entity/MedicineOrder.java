package pharmatrack.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "med_order")
public class MedicineOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "supplier", nullable = false, length = 100)
    private String supplier;

    @Column(name = "ttl_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "dt_ordered", nullable = false)
    private LocalDate dateOrdered;

    @Column(name = "dt_received")
    private LocalDate dateReceived;

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @OneToMany(mappedBy = "medicineOrder")
    private List<MedicineOrderItem> medicineOrderItems = new ArrayList<>();

    public MedicineOrder() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(LocalDate dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<MedicineOrderItem> getMedicineOrderItems() {
        return medicineOrderItems;
    }

    public void setMedicineOrderItems(List<MedicineOrderItem> medicineOrderItems) {
        this.medicineOrderItems = medicineOrderItems;
    }
}


