package pharmatrack.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pharmatrack.entity.MedicineOrder;
import pharmatrack.repository.MedicineOrderRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicineOrderService {

    private final MedicineOrderRepository medicineOrderRepo;

    public MedicineOrderService(MedicineOrderRepository medicineOrderRepo) {
        this.medicineOrderRepo = medicineOrderRepo;
    }

    public MedicineOrder createMedicineOrder(MedicineOrder medicineOrder) {
        if(medicineOrder.getSupplier().isEmpty()) {
            throw new IllegalArgumentException("Supplier cannot be blank");
        }

        return medicineOrderRepo.save(medicineOrder);
    }

    @Transactional(readOnly = true)
    public List<MedicineOrder> getAllMedicineOrderByDateOrderedAsc() {
        return medicineOrderRepo.findAllByOrderByDateOrderedAsc();
    }

    @Transactional(readOnly = true)
    public MedicineOrder getMedicineOrderById(Integer medicineOrderId) {
        return medicineOrderRepo.findById(medicineOrderId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Medicine Order not found"));
    }

    public void deleteMedicineOrder(Integer medicineOrder) {
        medicineOrderRepo.delete(getMedicineOrderById(medicineOrder));
    }

    public MedicineOrder updateMedicineOrder(Integer medicineOrderId, MedicineOrder updatedMedicineOrder) {
        MedicineOrder existingOrder = getMedicineOrderById(medicineOrderId);

        existingOrder.setSupplier(updatedMedicineOrder.getSupplier());
        existingOrder.setDateOrdered(updatedMedicineOrder.getDateOrdered());
        existingOrder.setDateReceived(updatedMedicineOrder.getDateReceived());
        existingOrder.setOrderStatus(updatedMedicineOrder.getOrderStatus());

        return medicineOrderRepo.save(existingOrder);
    }

}
