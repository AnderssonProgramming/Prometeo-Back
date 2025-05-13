package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;




public class ProgressHistoryDTOTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        ProgressHistoryDTO progressHistory = new ProgressHistoryDTO();
        
        // Assert
        assertNull(progressHistory.getId());
        assertNull(progressHistory.getUserId());
        assertNull(progressHistory.getRecordDate());
        assertNull(progressHistory.getMeasureType());
        assertEquals(0.0, progressHistory.getOldValue(), 0.001);
        assertEquals(0.0, progressHistory.getNewValue(), 0.001);
        assertNull(progressHistory.getNotes());
    }

    @Test
    public void testGettersAndSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate recordDate = LocalDate.now();
        String measureType = "Weight";
        double oldValue = 70.5;
        double newValue = 68.2;
        String notes = "Good progress";
        
        // Act
        ProgressHistoryDTO progressHistory = new ProgressHistoryDTO();
        progressHistory.setId(id);
        progressHistory.setUserId(userId);
        progressHistory.setRecordDate(recordDate);
        progressHistory.setMeasureType(measureType);
        progressHistory.setOldValue(oldValue);
        progressHistory.setNewValue(newValue);
        progressHistory.setNotes(notes);
        
        // Assert
        assertEquals(id, progressHistory.getId());
        assertEquals(userId, progressHistory.getUserId());
        assertEquals(recordDate, progressHistory.getRecordDate());
        assertEquals(measureType, progressHistory.getMeasureType());
        assertEquals(oldValue, progressHistory.getOldValue(), 0.001);
        assertEquals(newValue, progressHistory.getNewValue(), 0.001);
        assertEquals(notes, progressHistory.getNotes());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate recordDate = LocalDate.now();
        
        ProgressHistoryDTO dto1 = new ProgressHistoryDTO();
        dto1.setId(id);
        dto1.setUserId(userId);
        dto1.setRecordDate(recordDate);
        dto1.setMeasureType("Weight");
        dto1.setOldValue(70.5);
        dto1.setNewValue(68.2);
        dto1.setNotes("Good progress");
        
        ProgressHistoryDTO dto2 = new ProgressHistoryDTO();
        dto2.setId(id);
        dto2.setUserId(userId);
        dto2.setRecordDate(recordDate);
        dto2.setMeasureType("Weight");
        dto2.setOldValue(70.5);
        dto2.setNewValue(68.2);
        dto2.setNotes("Good progress");
        
        ProgressHistoryDTO differentDto = new ProgressHistoryDTO();
        differentDto.setId(UUID.randomUUID());
        
        // Assert
        assertEquals(dto1, dto1);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, differentDto);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
    }

    @Test
    public void testToString() {
        // Arrange
        UUID id = UUID.fromString("12345678-1234-1234-1234-123456789012");
        UUID userId = UUID.fromString("87654321-4321-4321-4321-210987654321");
        LocalDate recordDate = LocalDate.of(2023, 4, 15);
        
        ProgressHistoryDTO dto = new ProgressHistoryDTO();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setRecordDate(recordDate);
        dto.setMeasureType("Weight");
        dto.setOldValue(70.5);
        dto.setNewValue(68.2);
        dto.setNotes("Good progress");
        
        // Act
        String toStringResult = dto.toString();
        
        // Assert
        assertTrue(toStringResult.contains(id.toString()));
        assertTrue(toStringResult.contains(userId.toString()));
        assertTrue(toStringResult.contains(recordDate.toString()));
        assertTrue(toStringResult.contains("Weight"));
        assertTrue(toStringResult.contains("70.5"));
        assertTrue(toStringResult.contains("68.2"));
        assertTrue(toStringResult.contains("Good progress"));
    }
}