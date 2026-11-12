
package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pharmatrack.entity.Batch;
import pharmatrack.entity.Medicine;
import pharmatrack.repository.BatchRepository;
import pharmatrack.repository.MedicineRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BatchService {

    private final BatchRepository batchRepository;
    private final MedicineRepository medicineRepository;

    public BatchService(
            BatchRepository batchRepository,
            MedicineRepository medicineRepository) {
        this.batchRepository = batchRepository;
        this.medicineRepository = medicineRepository;
    }

    public List<Batch> getBatchesByMedicineId(Integer medicineId) {
        return batchRepository
                .findByMedicine_MedicineIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
                        medicineId, 0, LocalDate.now());
    }

    @Transactional
    public Batch saveBatch(Integer medicineId, Batch batch) {

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Medicine not found"));

        if (batch.getQuantityReceived() <= 0) {
            throw new IllegalArgumentException(
                    "Received quantity must be greater than zero");
        }

        batch.setQuantityRemaining(batch.getQuantityReceived());
        medicine.addBatch(batch);
        return batchRepository.save(batch);
    }

    // deducts stock using FEFO
    @Transactional
    public void deductStock(Integer medicineId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        List<Batch> batches = batchRepository
                .findByMedicine_MedicineIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
                        medicineId, 0, LocalDate.now());

        int remainingToDeduct = quantity;

        for (Batch batch : batches) {

            int available = batch.getQuantityRemaining();

            int deduction = Math.min(available, remainingToDeduct);

            batch.setQuantityRemaining(available - deduction);

            remainingToDeduct -= deduction;

            if (remainingToDeduct == 0) {
                break;
            }
        }
        if (remainingToDeduct > 0) {
            throw new IllegalStateException(
                    "Insufficient stock for medicine ID: " + medicineId);
        }
    }

    @Transactional
    public Batch updateBatch(Integer batchId, Batch updatedBatch) {

        Batch existingBatch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Batch not found"));

        // instances when admin incorrectly inputs wrong quantity_received
        int quantitySold = existingBatch.getQuantityReceived()
                - existingBatch.getQuantityRemaining();

        int newQuantityReceived = updatedBatch.getQuantityReceived();

        if (newQuantityReceived < quantitySold) {
            throw new IllegalArgumentException(
                    "Received quantity cannot be less than the quantity already deducted");
        }

        existingBatch.setBatchNumber(updatedBatch.getBatchNumber());
        existingBatch.setSupplier(updatedBatch.getSupplier());
        existingBatch.setExpiryDate(updatedBatch.getExpiryDate());
        existingBatch.setDateReceived(updatedBatch.getDateReceived());

        existingBatch.setQuantityReceived(newQuantityReceived);
        existingBatch.setQuantityRemaining(newQuantityReceived - quantitySold);

        return existingBatch;
    }


}
