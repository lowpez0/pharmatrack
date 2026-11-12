package pharmatrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmatrack.entity.MedicineOrder;
import java.util.List;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Integer> {

    List<MedicineOrder> findAllByOrderByDateOrderedAsc();

}
