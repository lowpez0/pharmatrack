package pharmatrack.service;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pharmatrack.entity.MedicineOrder;
import pharmatrack.repository.MedicineOrderRepository;

@Service
@Transactional
public class MedicineOrderService {

    private final MedicineOrderRepository medicineOrderRepo;

    public MedicineOrderService(MedicineOrderRepository medicineOrderRepo) {
        this.medicineOrderRepo = medicineOrderRepo;
    }

    public MedicineOrderRepository getMedicineOrderRepo() {
        return medicineOrderRepo;
    }

    public MedicineOrder createMedicineOrder(MedicineOrder medicineOrder) {

        return null;
    }

}
