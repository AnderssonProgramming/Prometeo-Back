package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;






public class ReservationTest {

    private Reservation reservation;
    private UUID userId;
    private UUID sessionId;
    private UUID equipmentId1;
    private UUID equipmentId2;
    private LocalDateTime reservationDateTime;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        equipmentId1 = UUID.randomUUID();
        equipmentId2 = UUID.randomUUID();
        reservationDateTime = LocalDateTime.of(2023, 10, 15, 14, 30);

        reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(userId);
        reservation.setSessionId(sessionId);
        reservation.setReservationDate(reservationDateTime);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setEquipmentIds(Arrays.asList(equipmentId1, equipmentId2));
    }

    @Test
    public void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        UUID completedById = UUID.randomUUID();
        LocalDateTime completedAt = LocalDateTime.now();
        LocalDateTime canceledAt = LocalDateTime.now();
        LocalDateTime attendanceTime = LocalDateTime.now();
        
        reservation.setId(id);
        reservation.setAttended(true);
        reservation.setCancellationReason("Personal reasons");
        reservation.setCompletedById(completedById);
        reservation.setCompletedAt(completedAt);
        reservation.setCanceledAt(canceledAt);
        reservation.setAttendanceTime(attendanceTime);
        reservation.setNotes("Test notes");
        
        assertEquals(id, reservation.getId());
        assertEquals(userId, reservation.getUserId());
        assertEquals(sessionId, reservation.getSessionId());
        assertEquals(reservationDateTime, reservation.getReservationDate());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals(2, reservation.getEquipmentIds().size());
        assertTrue(reservation.getEquipmentIds().contains(equipmentId1));
        assertTrue(reservation.getEquipmentIds().contains(equipmentId2));
        assertTrue(reservation.getAttended());
        assertEquals("Personal reasons", reservation.getCancellationReason());
        assertEquals(completedById, reservation.getCompletedById());
        assertEquals(completedAt, reservation.getCompletedAt());
        assertEquals(canceledAt, reservation.getCanceledAt());
        assertEquals(attendanceTime, reservation.getAttendanceTime());
        assertEquals("Test notes", reservation.getNotes());
    }
    
    @Test
    public void testDateTimeMethods() {
        LocalDate date = LocalDate.of(2023, 10, 15);
        LocalTime time = LocalTime.of(14, 30);
        
        assertEquals(date, reservation.getDate());
        assertEquals(time, reservation.getStartTime());
        assertEquals(time.plusHours(1), reservation.getEndTime());
        
        // Test setting new date and time
        LocalDate newDate = LocalDate.of(2023, 11, 20);
        LocalTime newTime = LocalTime.of(16, 45);
        
        reservation.setDate(newDate);
        reservation.setStartTime(newTime);
        
        assertEquals(newDate, reservation.getDate());
        assertEquals(newTime, reservation.getStartTime());
        assertEquals(newTime.plusHours(1), reservation.getEndTime());
    }
    
    @Test
    public void testStatusTransitions() {
        // Test confirm
        reservation.confirm();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertTrue(reservation.isActive());
        
        // Test cancel
        reservation.cancel();
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertFalse(reservation.isActive());
        assertNotNull(reservation.getCanceledAt());
        
        // Test complete
        reservation.complete();
        assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
        assertFalse(reservation.isActive());
    }
    
    @Test
    public void testSetStatusWithString() {
        reservation.setStatus("CONFIRMED");
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }
    
    @Test
    public void testCheckInAndCancellationMethods() {
        LocalDateTime checkInTime = LocalDateTime.now();
        LocalDateTime cancellationDate = LocalDateTime.now();
        
        reservation.setCheckInTime(checkInTime);
        reservation.setCancellationDate(cancellationDate);
        
        assertEquals(checkInTime, reservation.getCheckInTime());
        assertEquals(cancellationDate, reservation.getCancellationDate());
    }
    
    @Test
    public void testIsActive() {
        // By default, reservation is PENDING
        assertTrue(reservation.isActive());
        
        // When confirmed
        reservation.setStatus(ReservationStatus.CONFIRMED);
        assertTrue(reservation.isActive());
        
        // When cancelled
        reservation.setStatus(ReservationStatus.CANCELLED);
        assertFalse(reservation.isActive());
        
        // When completed
        reservation.setStatus(ReservationStatus.COMPLETED);
        assertFalse(reservation.isActive());
    }
    
    @Test
    public void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        List<UUID> equipmentIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        LocalDateTime dateTime = LocalDateTime.now();
        
        Reservation newReservation = new Reservation(
            id, userId, sessionId, dateTime, ReservationStatus.CONFIRMED,
            equipmentIds, true, "No reason", UUID.randomUUID(),
            dateTime, dateTime, dateTime, "Test notes"
        );
        
        assertEquals(id, newReservation.getId());
        assertEquals(userId, newReservation.getUserId());
        assertEquals(sessionId, newReservation.getSessionId());
        assertEquals(dateTime, newReservation.getReservationDate());
        assertEquals(ReservationStatus.CONFIRMED, newReservation.getStatus());
        assertEquals(equipmentIds, newReservation.getEquipmentIds());
        assertTrue(newReservation.getAttended());
        assertEquals("No reason", newReservation.getCancellationReason());
        assertEquals(dateTime, newReservation.getCompletedAt());
        assertEquals(dateTime, newReservation.getCanceledAt());
        assertEquals(dateTime, newReservation.getAttendanceTime());
        assertEquals("Test notes", newReservation.getNotes());
    }
}