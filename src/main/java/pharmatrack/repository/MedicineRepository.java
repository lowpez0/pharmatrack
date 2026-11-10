
package pharmatrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmatrack.entity.Medicine;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {

    List<Medicine> findByCategoryIgnoreCase(String category);

    List<Medicine> findByGenericMedicineNameContainingIgnoreCase(String name);

    boolean existsByGenericMedicineNameAndMedicineBrandAndDosageFormAndStrength(
            String genericMedicineName,
            String medicineBrand,
            String dosageForm,
            String strength
    );
}
