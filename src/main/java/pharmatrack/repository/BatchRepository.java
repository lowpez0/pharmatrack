
package pharmatrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmatrack.entity.Batch;

import java.time.LocalDate;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Integer> {

    List<Batch> findByMedicine_MedicineIdOrderByExpiryDateAsc(
            Integer medicineId
    );

    List<Batch> findByMedicine_MedicineIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
            Integer medicineId,
            int quantity,
            LocalDate date
    );
}
