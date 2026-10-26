package pharmatrack.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Integer medicineId;

    @Column(name = "generic_medicine_name", nullable = false, length = 100)
    private String genericMedicineName;

    @Column(name = "medicine_brand", nullable = false, length = 100)
    private String medicineBrand;

    @Column(name = "dosage_form", nullable = false, length = 100)
    private String dosageForm;

    @Column(name = "strength", nullable = false, length = 100)
    private String strength;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "selling_price", precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "min_stock_level", nullable = false)
    private int minStockLevel;

    @OneToMany(mappedBy = "medicine")
    private List<Batch> batches = new ArrayList<>();

    public Medicine() {}

    public Integer getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Integer medicineId) {
        this.medicineId = medicineId;
    }

    public String getGenericMedicineName() {
        return genericMedicineName;
    }

    public void setGenericMedicineName(String genericMedicineName) {
        this.genericMedicineName = genericMedicineName;
    }

    public String getMedicineBrand() {
        return medicineBrand;
    }

    public void setMedicineBrand(String medicineBrand) {
        this.medicineBrand = medicineBrand;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }
}
