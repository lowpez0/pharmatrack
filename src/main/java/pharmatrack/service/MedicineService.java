package pharmatrack.service;

import org.springframework.stereotype.Service;
import pharmatrack.entity.Medicine;
import pharmatrack.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepo;

    public MedicineService(MedicineRepository medicineRepo) {
        this.medicineRepo = medicineRepo;
    }


    public Medicine createMedicine(Medicine medicine) {

        if (medicine.getSellingPrice() == null ||
                medicine.getSellingPrice().signum() < 0) {
            throw new IllegalArgumentException("Invalid selling price");
        }

        if (medicine.getMinStockLevel() < 0) {
            throw new IllegalArgumentException("Minimum stock level cannot be negative");
        }

        boolean exists = medicineRepo
                .existsByGenericMedicineNameAndMedicineBrandAndDosageFormAndStrength(
                        medicine.getGenericMedicineName(),
                        medicine.getMedicineBrand(),
                        medicine.getDosageForm(),
                        medicine.getStrength()
                );

        if (exists) {
            throw new IllegalArgumentException("Medicine already exists");
        }

        return medicineRepo.save(medicine);
    }

    @Transactional(readOnly = true)
    public List<Medicine> getAllMedicines() {
        return medicineRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Medicine getMedicineById(Integer medicineId) {
        return medicineRepo.findById(medicineId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Medicine not found"));
    }
}
