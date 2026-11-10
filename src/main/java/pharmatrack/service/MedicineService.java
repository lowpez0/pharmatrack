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

    @Transactional(readOnly = true)
    public List<Medicine> getMedicinesByCategory(String category) {
        return medicineRepo.findByCategoryIgnoreCase(category);
    }

    public Medicine updateMedicine(Integer medicineId, Medicine updatedMedicine) {
        Medicine existingMedicine = getMedicineById(medicineId);

        if (updatedMedicine.getSellingPrice() == null ||
                updatedMedicine.getSellingPrice().signum() < 0) {
            throw new IllegalArgumentException("Invalid selling price");
        }

        if (updatedMedicine.getMinStockLevel() < 0) {
            throw new IllegalArgumentException(
                    "Minimum stock level cannot be negative"
            );
        }

        existingMedicine.setGenericMedicineName(updatedMedicine.getGenericMedicineName());
        existingMedicine.setMedicineBrand(updatedMedicine.getMedicineBrand());
        existingMedicine.setDosageForm(updatedMedicine.getDosageForm());
        existingMedicine.setStrength(updatedMedicine.getStrength());
        existingMedicine.setCategory(updatedMedicine.getCategory());
        existingMedicine.setSellingPrice(updatedMedicine.getSellingPrice());
        existingMedicine.setMinStockLevel(updatedMedicine.getMinStockLevel());

        return medicineRepo.save(existingMedicine);
    }

    public void deleteMedicine(Integer medicineId) {
        Medicine medicine = getMedicineById(medicineId);

        if (!medicine.getBatches().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete medicine with existing batches");
        }

        medicineRepo.delete(medicine);
    }

    public List<Medicine> searchMedicines(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        String searchKeyword = keyword.trim();

        return medicineRepo
                .findByGenericMedicineNameStartingWithIgnoreCaseOrMedicineBrandStartingWithIgnoreCase(
                        searchKeyword,
                        searchKeyword
                );
    }
}
