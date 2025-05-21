package edu.eci.cvds.prometeo.dto;

import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class ReservationDTOTest {

    @Test
    public void testIdGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        UUID id = UUID.randomUUID();
        
        assertNull(dto.getId());
        dto.setId(id);
        assertEquals(id, dto.getId());
    }
    
    @Test
    public void testUserIdGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        UUID userId = UUID.randomUUID();
        
        assertNull(dto.getUserId());
        dto.setUserId(userId);
        assertEquals(userId, dto.getUserId());
    }
    
    @Test
    public void testSessionIdGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        UUID sessionId = UUID.randomUUID();
        
        assertNull(dto.getSessionId());
        dto.setSessionId(sessionId);
        assertEquals(sessionId, dto.getSessionId());
    }
    
    @Test
    public void testStatusGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        ReservationStatus status = ReservationStatus.CONFIRMED; // Assuming this enum value exists
        
        assertNull(dto.getStatus());
        dto.setStatus(status);
        assertEquals(status, dto.getStatus());
    }
    
    @Test
    public void testReservationDateGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        LocalDateTime date = LocalDateTime.now();
        
        assertNull(dto.getReservationDate());
        dto.setReservationDate(date);
        assertEquals(date, dto.getReservationDate());
    }
    
    @Test
    public void testCancellationDateGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        LocalDateTime date = LocalDateTime.now();
        
        assertNull(dto.getCancellationDate());
        dto.setCancellationDate(date);
        assertEquals(date, dto.getCancellationDate());
    }
    
    @Test
    public void testCheckInTimeGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        LocalDateTime time = LocalDateTime.now();
        
        assertNull(dto.getCheckInTime());
        dto.setCheckInTime(time);
        assertEquals(time, dto.getCheckInTime());
    }
    
    @Test
    public void testEquipmentIdsGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        List<UUID> equipmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        
        assertNull(dto.getEquipmentIds());
        dto.setEquipmentIds(equipmentIds);
        assertEquals(equipmentIds, dto.getEquipmentIds());
    }
    
    @Test
    public void testNotesGetterAndSetter() {
        ReservationDTO dto = new ReservationDTO();
        String notes = "Test notes";
        
        assertNull(dto.getNotes());
        dto.setNotes(notes);
        assertEquals(notes, dto.getNotes());
    }
    
    @Test
    public void testAllFieldsSetAndGet() {
        ReservationDTO dto = new ReservationDTO();
        
        // Set up test data
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        ReservationStatus status = ReservationStatus.CONFIRMED; // Assuming this enum value exists
        LocalDateTime reservationDate = LocalDateTime.now();
        LocalDateTime cancellationDate = LocalDateTime.now().plusDays(1);
        LocalDateTime checkInTime = LocalDateTime.now().plusHours(2);
        List<UUID> equipmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        String notes = "Important reservation notes";
        
        // Set all fields
        dto.setId(id);
        dto.setUserId(userId);
        dto.setSessionId(sessionId);
        dto.setStatus(status);
        dto.setReservationDate(reservationDate);
        dto.setCancellationDate(cancellationDate);
        dto.setCheckInTime(checkInTime);
        dto.setEquipmentIds(equipmentIds);
        dto.setNotes(notes);
        
        // Verify all fields
        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(sessionId, dto.getSessionId());
        assertEquals(status, dto.getStatus());
        assertEquals(reservationDate, dto.getReservationDate());
        assertEquals(cancellationDate, dto.getCancellationDate());
        assertEquals(checkInTime, dto.getCheckInTime());
        assertEquals(equipmentIds, dto.getEquipmentIds());
        assertEquals(notes, dto.getNotes());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two identical DTOs
        ReservationDTO dto1 = new ReservationDTO();
        ReservationDTO dto2 = new ReservationDTO();
        
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        ReservationStatus status = ReservationStatus.CONFIRMED;
        LocalDateTime reservationDate = LocalDateTime.now();
        
        dto1.setId(id);
        dto1.setUserId(userId);
        dto1.setSessionId(sessionId);
        dto1.setStatus(status);
        dto1.setReservationDate(reservationDate);
        
        dto2.setId(id);
        dto2.setUserId(userId);
        dto2.setSessionId(sessionId);
        dto2.setStatus(status);
        dto2.setReservationDate(reservationDate);
        
        // Test equals
        assertEquals(dto1, dto2);
        
        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Modify one field and verify they're no longer equal
        dto2.setId(UUID.randomUUID());
        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    public void testToString() {
        ReservationDTO dto = new ReservationDTO();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setNotes("Test notes");
        
        String toString = dto.toString();
        
        // Verify toString contains key field values
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains("Test notes"));
        assertTrue(toString.contains("ReservationDTO"));
    }
    
    @Test
    public void testEmptyEquipmentList() {
        ReservationDTO dto = new ReservationDTO();
        List<UUID> emptyList = List.of();
        
        dto.setEquipmentIds(emptyList);
        assertEquals(emptyList, dto.getEquipmentIds());
        assertTrue(dto.getEquipmentIds().isEmpty());
    }
}