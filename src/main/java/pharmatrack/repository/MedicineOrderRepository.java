package pharmatrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmatrack.entity.MedicineOrder;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Integer> {


}
