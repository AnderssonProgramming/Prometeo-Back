package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.UUID;





public class ProgressHistoryTest {

    private ProgressHistory progressHistory;
    private UUID testUserId;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() {
        testUserId = UUID.randomUUID();
        testDate = LocalDate.now();
        progressHistory = new ProgressHistory();
    }

    @Test
    public void testGetterAndSetterMethods() {
        // Set values
        progressHistory.setUserId(testUserId);
        progressHistory.setRecordDate(testDate);
        progressHistory.setMeasureType("Weight");
        progressHistory.setOldValue(80.5);
        progressHistory.setNewValue(78.2);
        progressHistory.setNotes("Weekly weight check");

        // Verify values
        assertEquals(testUserId, progressHistory.getUserId());
        assertEquals(testDate, progressHistory.getRecordDate());
        assertEquals("Weight", progressHistory.getMeasureType());
        assertEquals(80.5, progressHistory.getOldValue(), 0.001);
        assertEquals(78.2, progressHistory.getNewValue(), 0.001);
        assertEquals("Weekly weight check", progressHistory.getNotes());
    }

    @Test
    public void testCalculateChange() {
        progressHistory.setOldValue(100.0);
        progressHistory.setNewValue(125.0);
        assertEquals(25.0, progressHistory.calculateChange(), 0.001);

        progressHistory.setOldValue(80.0);
        progressHistory.setNewValue(70.0);
        assertEquals(-10.0, progressHistory.calculateChange(), 0.001);
    }

    @Test
    public void testCalculatePercentageChange() {
        // Positive change
        progressHistory.setOldValue(100.0);
        progressHistory.setNewValue(125.0);
        assertEquals(25.0, progressHistory.calculatePercentageChange(), 0.001);

        // Negative change
        progressHistory.setOldValue(80.0);
        progressHistory.setNewValue(60.0);
        assertEquals(-25.0, progressHistory.calculatePercentageChange(), 0.001);

        // Zero old value (should return 0 to avoid division by zero)
        progressHistory.setOldValue(0.0);
        progressHistory.setNewValue(50.0);
        assertEquals(0.0, progressHistory.calculatePercentageChange(), 0.001);

        // Negative old value
        progressHistory.setOldValue(-20.0);
        progressHistory.setNewValue(-30.0);
        assertEquals(-50.0, progressHistory.calculatePercentageChange(), 0.001);
    }

    @Test
    public void testAllArgsConstructor() {
        String notes = "Test notes";
        ProgressHistory ph = new ProgressHistory(testUserId, testDate, "BMI", 22.0, 23.5, notes);
        
        assertEquals(testUserId, ph.getUserId());
        assertEquals(testDate, ph.getRecordDate());
        assertEquals("BMI", ph.getMeasureType());
        assertEquals(22.0, ph.getOldValue(), 0.001);
        assertEquals(23.5, ph.getNewValue(), 0.001);
        assertEquals(notes, ph.getNotes());
        assertEquals(1.5, ph.calculateChange(), 0.001);
        assertEquals(6.818, ph.calculatePercentageChange(), 0.001);
    }

    @Test
    public void testNoArgsConstructor() {
        ProgressHistory ph = new ProgressHistory();
        assertNull(ph.getUserId());
        assertNull(ph.getRecordDate());
        assertNull(ph.getMeasureType());
        assertEquals(0.0, ph.getOldValue(), 0.001);
        assertEquals(0.0, ph.getNewValue(), 0.001);
        assertNull(ph.getNotes());
    }
}