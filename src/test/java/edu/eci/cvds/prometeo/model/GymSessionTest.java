package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;





class GymSessionTest {

    @Test
    void testNoArgsConstructor() {
        GymSession session = new GymSession();
        assertNotNull(session);
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDate sessionDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int capacity = 20;
        int reservedSpots = 5;
        UUID trainerId = UUID.randomUUID();

        GymSession session = new GymSession(id, sessionDate, startTime, endTime, capacity, reservedSpots, trainerId);
        
        assertEquals(id, session.getId());
        assertEquals(sessionDate, session.getSessionDate());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals(capacity, session.getCapacity());
        assertEquals(reservedSpots, session.getReservedSpots());
        assertEquals(trainerId, session.getTrainerId());
    }

    @Test
    void testGettersAndSetters() {
        GymSession session = new GymSession();
        
        UUID id = UUID.randomUUID();
        LocalDate sessionDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        int capacity = 20;
        int reservedSpots = 5;
        UUID trainerId = UUID.randomUUID();
        
        session.setId(id);
        session.setSessionDate(sessionDate);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setCapacity(capacity);
        session.setReservedSpots(reservedSpots);
        session.setTrainerId(trainerId);
        
        assertEquals(id, session.getId());
        assertEquals(sessionDate, session.getSessionDate());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals(capacity, session.getCapacity());
        assertEquals(reservedSpots, session.getReservedSpots());
        assertEquals(trainerId, session.getTrainerId());
    }

    @Test
    void testHasAvailabilityWhenAvailable() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(5);
        
        assertTrue(session.hasAvailability());
    }

    @Test
    void testHasAvailabilityWhenFull() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(10);
        
        assertFalse(session.hasAvailability());
    }

    @Test
    void testGetAvailableSpots() {
        GymSession session = new GymSession();
        session.setCapacity(20);
        session.setReservedSpots(8);
        
        assertEquals(12, session.getAvailableSpots());
    }

    @Test
    void testReserveWhenAvailable() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(9);
        
        session.reserve();
        assertEquals(10, session.getReservedSpots());
    }

    @Test
    void testReserveWhenFull() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(10);
        
        assertThrows(IllegalStateException.class, session::reserve);
    }

    @Test
    void testCancelReservation() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(5);
        
        session.cancelReservation();
        assertEquals(4, session.getReservedSpots());
    }

    @Test
    void testCancelReservationWhenZeroReservations() {
        GymSession session = new GymSession();
        session.setCapacity(10);
        session.setReservedSpots(0);
        
        session.cancelReservation();
        assertEquals(0, session.getReservedSpots());
    }

    @Test
    void testGetDuration() {
        GymSession session = new GymSession();
        session.setStartTime(LocalTime.of(10, 0));
        session.setEndTime(LocalTime.of(11, 30));
        
        Duration expectedDuration = Duration.ofMinutes(90);
        assertEquals(expectedDuration, session.getDuration());
    }
}