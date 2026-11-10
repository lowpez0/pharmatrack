
package pharmatrack.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pharmatrack.entity.Medicine;
import pharmatrack.repository.MedicineRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MedicineServiceIntegrationTest {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicineRepository medicineRepo;

    private Medicine createTestMedicine(
            String genericName, String brand) {

        Medicine medicine = new Medicine();

        medicine.setGenericMedicineName(genericName);
        medicine.setMedicineBrand(brand);
        medicine.setDosageForm("Tablet");
        medicine.setStrength("500mg");
        medicine.setCategory("Analgesic");
        medicine.setSellingPrice(new BigDecimal("5.00"));
        medicine.setMinStockLevel(10);

        return medicine;
    }


    @Test
    void createMedicine_shouldSaveMedicine() {

        Medicine medicine = createTestMedicine(
                "Paracetamol", "Biogesic");

        Medicine saved = medicineService.createMedicine(medicine);

        assertNotNull(saved.getMedicineId());
        assertEquals("Paracetamol",
                saved.getGenericMedicineName());
        assertEquals("Biogesic",
                saved.getMedicineBrand());
    }

    @Test
    void createMedicine_shouldThrowExceptionWhenPriceIsNegative() {

        Medicine medicine = createTestMedicine(
                "Paracetamol", "Biogesic");

        medicine.setSellingPrice(new BigDecimal("-5.00"));

        assertThrows(IllegalArgumentException.class,
                () -> medicineService.createMedicine(medicine));
    }

    @Test
    void createMedicine_shouldThrowExceptionWhenStockLevelIsNegative() {

        Medicine medicine = createTestMedicine(
                "Paracetamol", "Biogesic");

        medicine.setMinStockLevel(-1);

        assertThrows(IllegalArgumentException.class,
                () -> medicineService.createMedicine(medicine));
    }

    @Test
    void createMedicine_shouldThrowExceptionWhenMedicineAlreadyExists() {

        Medicine medicine = createTestMedicine(
                "Paracetamol", "Biogesic");

        medicineService.createMedicine(medicine);

        Medicine duplicate = createTestMedicine(
                "Paracetamol", "Biogesic");

        assertThrows(IllegalArgumentException.class,
                () -> medicineService.createMedicine(duplicate));
    }


    @Test
    void getAllMedicines_shouldReturnAllMedicines() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        medicineRepo.save(createTestMedicine(
                "Ibuprofen", "Medicol"));

        List<Medicine> medicines =
                medicineService.getAllMedicines();

        assertEquals(2, medicines.size());
    }

    @Test
    void getMedicineById_shouldReturnMedicine() {

        Medicine saved = medicineRepo.save(
                createTestMedicine("Paracetamol", "Biogesic"));

        Medicine result =
                medicineService.getMedicineById(saved.getMedicineId());

        assertEquals(saved.getMedicineId(), result.getMedicineId());
        assertEquals("Paracetamol",
                result.getGenericMedicineName());
    }

    @Test
    void getMedicineById_shouldThrowExceptionWhenNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> medicineService.getMedicineById(-1));
    }

    @Test
    void getMedicinesByCategory_shouldReturnMatchingMedicines() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        Medicine medicine = createTestMedicine(
                "Amoxicillin", "Amoxil");

        medicine.setCategory("Antibiotic");

        medicineRepo.save(medicine);

        List<Medicine> result =
                medicineService.getMedicinesByCategory("analgesic");

        assertEquals(1, result.size());
        assertEquals("Paracetamol",
                result.get(0).getGenericMedicineName());
    }

    @Test
    void updateMedicine_shouldUpdateMedicineDetails() {

        Medicine saved = medicineRepo.save(
                createTestMedicine("Paracetamol", "Biogesic"));

        Medicine updated = createTestMedicine(
                "Paracetamol", "Tempra");

        updated.setSellingPrice(new BigDecimal("10.00"));
        updated.setMinStockLevel(20);

        Medicine result = medicineService.updateMedicine(
                saved.getMedicineId(), updated);

        assertEquals("Tempra", result.getMedicineBrand());
        assertEquals(new BigDecimal("10.00"),
                result.getSellingPrice());
        assertEquals(20, result.getMinStockLevel());
    }


    @Test
    void deleteMedicine_shouldDeleteMedicine() {

        Medicine saved = medicineRepo.save(
                createTestMedicine("Paracetamol", "Biogesic"));

        System.out.println(saved.getMedicineId());
        Integer medicineId = saved.getMedicineId();

        medicineService.deleteMedicine(medicineId);

        assertFalse(medicineRepo.existsById(medicineId));
    }

    @Test
    void deleteMedicine_shouldThrowExceptionWhenNotFound() {

        assertThrows(EntityNotFoundException.class,
                () -> medicineService.deleteMedicine(-1));
    }


    @Test
    void searchMedicines_shouldFindByGenericName() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        List<Medicine> result =
                medicineService.searchMedicines("Para");


        assertEquals(1, result.size());
        assertEquals("Paracetamol",
                result.get(0).getGenericMedicineName());
    }

    @Test
    void searchMedicines_shouldFindByBrandName() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        List<Medicine> result =
                medicineService.searchMedicines("Bio");

        assertEquals(1, result.size());
        assertEquals("Biogesic",
                result.get(0).getMedicineBrand());
    }

    @Test
    void searchMedicines_shouldIgnoreCase() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        List<Medicine> result =
                medicineService.searchMedicines("para");

        assertEquals(1, result.size());
    }

    @Test
    void searchMedicines_shouldReturnEmptyListForBlankKeyword() {

        assertTrue(medicineService.searchMedicines("").isEmpty());
        assertTrue(medicineService.searchMedicines("   ").isEmpty());
        assertTrue(medicineService.searchMedicines(null).isEmpty());
    }

    @Test
    void searchMedicines_shouldTrimKeyword() {

        medicineRepo.save(createTestMedicine(
                "Paracetamol", "Biogesic"));

        List<Medicine> result =
                medicineService.searchMedicines("  Para  ");

        assertEquals(1, result.size());
    }
}
