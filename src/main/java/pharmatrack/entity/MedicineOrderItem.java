package pharmatrack.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "med_order_item")
public class MedicineOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private MedicineOrder medicineOrder;

    @Column(name = "item_details", columnDefinition = "TEXT", nullable = false)
    private String itemDetails;

    @Column(name = "item_quantity", nullable = false)
    private int itemQuantity;

    @Column(name = "item_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal itemPrice;

    public MedicineOrderItem() {}

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public MedicineOrder getMedicineOrder() {
        return medicineOrder;
    }

    public void setMedicineOrder(MedicineOrder medicineOrder) {
        this.medicineOrder = medicineOrder;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
