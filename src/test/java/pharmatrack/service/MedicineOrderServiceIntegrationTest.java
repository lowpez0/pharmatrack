
package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pharmatrack.entity.MedicineOrder;
import pharmatrack.entity.enums.OrderStatus;
import pharmatrack.repository.MedicineOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MedicineOrderServiceIntegrationTest {

    @Autowired
    private MedicineOrderService medicineOrderService;

    @Autowired
    private MedicineOrderRepository medicineOrderRepo;

    private MedicineOrder createTestOrder(
            String supplier, LocalDate dateOrdered) {

        MedicineOrder order = new MedicineOrder();

        order.setSupplier(supplier);
        order.setTotalAmount(new BigDecimal("1500.00"));
        order.setDateOrdered(dateOrdered);
        order.setOrderStatus(OrderStatus.PENDING);

        return order;
    }

    @Test
    void createMedicineOrder_shouldSaveOrder() {

        MedicineOrder order = createTestOrder(
                "ABC Pharmaceuticals",
                LocalDate.of(2026, 1, 10)
        );

        MedicineOrder savedOrder =
                medicineOrderService.createMedicineOrder(order);

        assertNotEquals(0, savedOrder.getOrderId());
        assertEquals("ABC Pharmaceuticals", savedOrder.getSupplier());
        assertEquals(new BigDecimal("1500.00"), savedOrder.getTotalAmount());
        assertEquals(OrderStatus.PENDING, savedOrder.getOrderStatus());

        assertTrue(medicineOrderRepo.existsById(savedOrder.getOrderId()));
    }

    @Test
    void createMedicineOrder_shouldThrowExceptionWhenSupplierIsBlank() {
        MedicineOrder order = createTestOrder(
                "",
                LocalDate.of(2026, 1, 10)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> medicineOrderService.createMedicineOrder(order)
        );
    }

    @Test
    void getAllMedicineOrderByDateOrderedDesc_shouldReturnOrdersInDescendingOrder() {

        MedicineOrder laterOrder = medicineOrderService.createMedicineOrder(
                createTestOrder(
                        "Supplier B",
                        LocalDate.of(2026, 5, 10)
                )
        );

        MedicineOrder earlierOrder = medicineOrderService.createMedicineOrder(
                createTestOrder(
                        "Supplier A",
                        LocalDate.of(2026, 2, 10)
                )
        );

        List<MedicineOrder> orders =
                medicineOrderService.getAllMedicineOrderByDateOrderedDesc();

        int earlierIndex = -1;
        int laterIndex = -1;

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId() == earlierOrder.getOrderId()) {
                earlierIndex = i;
            }

            if (orders.get(i).getOrderId() == laterOrder.getOrderId()) {
                laterIndex = i;
            }
        }

        assertNotEquals(-1, earlierIndex);
        assertNotEquals(-1, laterIndex);
        assertTrue(laterIndex < earlierIndex);
    }

    @Test
    void getMedicineOrderById_shouldReturnMatchingOrder() {

        MedicineOrder savedOrder = medicineOrderService.createMedicineOrder(
                createTestOrder(
                        "ABC Pharmaceuticals",
                        LocalDate.of(2026, 1, 10)
                )
        );

        MedicineOrder foundOrder =
                medicineOrderService.getMedicineOrderById(savedOrder.getOrderId());

        assertEquals(savedOrder.getOrderId(), foundOrder.getOrderId());
        assertEquals(savedOrder.getSupplier(), foundOrder.getSupplier());
    }

    @Test
    void getMedicineOrderById_shouldThrowExceptionWhenOrderDoesNotExist() {

        Integer invalidId = -1;

        assertThrows(
                EntityNotFoundException.class,
                () -> medicineOrderService.getMedicineOrderById(invalidId)
        );
    }

    @Test
    void updateMedicineOrder_shouldUpdateExistingOrder() {

        MedicineOrder savedOrder = medicineOrderService.createMedicineOrder(
                createTestOrder(
                        "Old Supplier",
                        LocalDate.of(2026, 1, 10)
                )
        );

        MedicineOrder updatedOrder = new MedicineOrder();
        updatedOrder.setSupplier("New Supplier");
        updatedOrder.setDateOrdered(LocalDate.of(2026, 2, 15));
        updatedOrder.setDateReceived(LocalDate.of(2026, 2, 20));
        updatedOrder.setOrderStatus(OrderStatus.RECEIVED);

        MedicineOrder result = medicineOrderService.updateMedicineOrder(
                savedOrder.getOrderId(),
                updatedOrder
        );

        assertEquals("New Supplier", result.getSupplier());
        assertEquals(LocalDate.of(2026, 2, 15), result.getDateOrdered());
        assertEquals(LocalDate.of(2026, 2, 20), result.getDateReceived());
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus());

        //should be unchanged.
        assertEquals(new BigDecimal("1500.00"), result.getTotalAmount());
        assertEquals(savedOrder.getOrderId(), result.getOrderId());
    }

    @Test
    void deleteMedicineOrder_shouldDeleteExistingOrder() {

        MedicineOrder savedOrder = medicineOrderService.createMedicineOrder(
                createTestOrder(
                        "ABC Pharmaceuticals",
                        LocalDate.of(2026, 1, 10)
                )
        );

        medicineOrderService.deleteMedicineOrder(savedOrder.getOrderId());

        assertFalse(medicineOrderRepo.existsById(savedOrder.getOrderId()));
    }
}
