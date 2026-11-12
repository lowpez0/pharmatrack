
package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pharmatrack.entity.Batch;
import pharmatrack.entity.Medicine;
import pharmatrack.repository.BatchRepository;
import pharmatrack.repository.MedicineRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BatchServiceIntegrationTest {

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    private Medicine medicine;

    @BeforeEach
    void setUp() {

        medicine = new Medicine();
        medicine.setGenericMedicineName("Paracetamol");
        medicine.setMedicineBrand("Biogesic");
        medicine.setDosageForm("Tablet");
        medicine.setStrength("500mg");
        medicine.setCategory("Analgesic");
        medicine.setSellingPrice(new BigDecimal("5.00"));
        medicine.setMinStockLevel(20);

        medicine = medicineRepository.save(medicine);
    }

    private Batch createTestBatch(
            String batchNumber,
            int quantity,
            LocalDate expiryDate) {

        Batch batch = new Batch();
        batch.setBatchNumber(batchNumber);
        batch.setQuantityReceived(quantity);
        batch.setQuantityRemaining(quantity);
        batch.setSupplier("Test Supplier");
        batch.setExpiryDate(expiryDate);
        batch.setDateReceived(LocalDate.now());

        return batch;
    }

    @Test
    void getBatchesByMedicineId_shouldReturnOnlyActiveAndUnexpiredBatchesOrderedByExpiryDate() {

        Batch laterExpiry = createTestBatch(
                "BATCH-002", 50, LocalDate.now().plusMonths(6));

        Batch earlierExpiry = createTestBatch(
                "BATCH-001", 30, LocalDate.now().plusMonths(2));

        Batch depleted = createTestBatch(
                "BATCH-003", 20, LocalDate.now().plusMonths(3));
        depleted.setQuantityRemaining(0);

        Batch expired = createTestBatch(
                "BATCH-004", 40, LocalDate.now().minusDays(1));

        medicine.addBatch(laterExpiry);
        medicine.addBatch(earlierExpiry);
        medicine.addBatch(depleted);
        medicine.addBatch(expired);

        batchRepository.saveAll(
                List.of(laterExpiry, earlierExpiry, depleted, expired));

        List<Batch> result =
                batchService.getBatchesByMedicineId(medicine.getMedicineId());

        assertEquals(2, result.size());

        assertEquals("BATCH-001", result.get(0).getBatchNumber());
        assertEquals("BATCH-002", result.get(1).getBatchNumber());
    }

    @Test
    void saveBatch_shouldSaveBatchAndInitializeRemainingQuantity() {

        Batch batch = createTestBatch(
                "BATCH-001", 100, LocalDate.now().plusMonths(6));

        Batch savedBatch = batchService.saveBatch(
                medicine.getMedicineId(), batch);

        assertNotNull(savedBatch.getBatchId());
        assertEquals(100, savedBatch.getQuantityReceived());
        assertEquals(100, savedBatch.getQuantityRemaining());
        assertEquals(medicine.getMedicineId(),
                savedBatch.getMedicine().getMedicineId());
    }

    @Test
    void saveBatch_shouldThrowExceptionWhenMedicineDoesNotExist() {

        Batch batch = createTestBatch(
                "BATCH-001", 100, LocalDate.now().plusMonths(6));

        assertThrows(EntityNotFoundException.class,
                () -> batchService.saveBatch(999999, batch));
    }

    @Test
    void saveBatch_shouldThrowExceptionWhenReceivedQuantityIsZeroOrNegative() {

        Batch batch = createTestBatch(
                "BATCH-001", 0, LocalDate.now().plusMonths(6));

        assertThrows(IllegalArgumentException.class,
                () -> batchService.saveBatch(
                        medicine.getMedicineId(), batch));
    }

    @Test
    void deductStock_shouldDeductFromEarliestExpiringBatchFirst() {

        Batch firstBatch = createTestBatch(
                "BATCH-001", 50, LocalDate.now().plusMonths(2));

        Batch secondBatch = createTestBatch(
                "BATCH-002", 100, LocalDate.now().plusMonths(5));

        medicine.addBatch(firstBatch);
        medicine.addBatch(secondBatch);

        batchRepository.saveAll(List.of(firstBatch, secondBatch));

        batchService.deductStock(medicine.getMedicineId(), 60);

        assertEquals(0, batchRepository.findById(
                        firstBatch.getBatchId()).orElseThrow()
                .getQuantityRemaining());

        assertEquals(90, batchRepository.findById(
                        secondBatch.getBatchId()).orElseThrow()
                .getQuantityRemaining());
    }

    @Test
    void deductStock_shouldThrowExceptionWhenQuantityIsZeroOrNegative() {

        assertThrows(IllegalArgumentException.class,
                () -> batchService.deductStock(
                        medicine.getMedicineId(), 0));
    }

    @Test
    void deductStock_shouldThrowExceptionWhenStockIsInsufficient() {

        Batch batch = createTestBatch(
                "BATCH-001", 20, LocalDate.now().plusMonths(3));

        medicine.addBatch(batch);
        batchRepository.save(batch);

        assertThrows(IllegalStateException.class,
                () -> batchService.deductStock(
                        medicine.getMedicineId(), 30));
    }

    // UPDATE BATCH

    @Test
    void updateBatch_shouldUpdateBatchDetailsAndRecalculateRemainingQuantity() {

        Batch batch = createTestBatch(
                "BATCH-001", 100, LocalDate.now().plusMonths(3));

        medicine.addBatch(batch);
        batchRepository.save(batch);

        // Simulate 30 units already sold
        batch.setQuantityRemaining(70);

        Batch updatedBatch = createTestBatch(
                "BATCH-UPDATED", 120, LocalDate.now().plusMonths(8));

        updatedBatch.setSupplier("New Supplier");
        updatedBatch.setDateReceived(LocalDate.now().minusDays(5));

        Batch result = batchService.updateBatch(
                batch.getBatchId(), updatedBatch);

        assertEquals("BATCH-UPDATED", result.getBatchNumber());
        assertEquals("New Supplier", result.getSupplier());
        assertEquals(120, result.getQuantityReceived());
        assertEquals(90, result.getQuantityRemaining());
        assertEquals(LocalDate.now().plusMonths(8),
                result.getExpiryDate());
        assertEquals(LocalDate.now().minusDays(5),
                result.getDateReceived());
    }

    @Test
    void updateBatch_shouldThrowExceptionWhenBatchDoesNotExist() {

        Batch updatedBatch = createTestBatch(
                "BATCH-UPDATED", 100, LocalDate.now().plusMonths(6));

        assertThrows(EntityNotFoundException.class,
                () -> batchService.updateBatch(999999, updatedBatch));
    }

    @Test
    void updateBatch_shouldThrowExceptionWhenNewQuantityIsLessThanQuantitySold() {

        Batch batch = createTestBatch(
                "BATCH-001", 100, LocalDate.now().plusMonths(3));

        medicine.addBatch(batch);
        batchRepository.save(batch);

        // 60 units already sold
        batch.setQuantityRemaining(40);

        Batch updatedBatch = createTestBatch(
                "BATCH-UPDATED", 50, LocalDate.now().plusMonths(6));

        assertThrows(IllegalArgumentException.class,
                () -> batchService.updateBatch(
                        batch.getBatchId(), updatedBatch));
    }
}
