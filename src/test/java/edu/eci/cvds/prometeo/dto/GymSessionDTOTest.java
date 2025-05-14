package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;





public class GymSessionDTOTest {

    @Test
    public void testIdGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        UUID id = UUID.randomUUID();
        
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    public void testSessionDateGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        LocalDate date = LocalDate.now();
        
        dto.setSessionDate(date);
        assertEquals(date, dto.getSessionDate());
    }

    @Test
    public void testStartTimeGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        LocalTime time = LocalTime.of(9, 0);
        
        dto.setStartTime(time);
        assertEquals(time, dto.getStartTime());
    }

    @Test
    public void testEndTimeGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        LocalTime time = LocalTime.of(10, 0);
        
        dto.setEndTime(time);
        assertEquals(time, dto.getEndTime());
    }

    @Test
    public void testCapacityGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        int capacity = 25;
        
        dto.setCapacity(capacity);
        assertEquals(capacity, dto.getCapacity());
    }

    @Test
    public void testReservedSpotsGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        int reservedSpots = 15;
        
        dto.setReservedSpots(reservedSpots);
        assertEquals(reservedSpots, dto.getReservedSpots());
    }

    @Test
    public void testTrainerIdGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        UUID trainerId = UUID.randomUUID();
        
        dto.setTrainerId(trainerId);
        assertEquals(trainerId, dto.getTrainerId());
    }

    @Test
    public void testSessionTypeGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        String sessionType = "Yoga";
        
        dto.setSessionType(sessionType);
        assertEquals(sessionType, dto.getSessionType());
    }

    @Test
    public void testLocationGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        String location = "Main Studio";
        
        dto.setLocation(location);
        assertEquals(location, dto.getLocation());
    }

    @Test
    public void testDescriptionGetterAndSetter() {
        GymSessionDTO dto = new GymSessionDTO();
        String description = "Beginner friendly yoga class";
        
        dto.setDescription(description);
        assertEquals(description, dto.getDescription());
    }

    @Test
    public void testEqualsAndHashCode() {
        GymSessionDTO dto1 = new GymSessionDTO();
        GymSessionDTO dto2 = new GymSessionDTO();
        
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        UUID trainerId = UUID.randomUUID();
        
        dto1.setId(id);
        dto1.setSessionDate(date);
        dto1.setStartTime(startTime);
        dto1.setEndTime(endTime);
        dto1.setCapacity(20);
        dto1.setReservedSpots(10);
        dto1.setTrainerId(trainerId);
        dto1.setSessionType("Fitness");
        dto1.setLocation("Gym 1");
        dto1.setDescription("Fitness session");
        
        dto2.setId(id);
        dto2.setSessionDate(date);
        dto2.setStartTime(startTime);
        dto2.setEndTime(endTime);
        dto2.setCapacity(20);
        dto2.setReservedSpots(10);
        dto2.setTrainerId(trainerId);
        dto2.setSessionType("Fitness");
        dto2.setLocation("Gym 1");
        dto2.setDescription("Fitness session");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Test inequality
        dto2.setCapacity(30);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEquals() {
        // Arrange
        GymSessionDTO dto1 = new GymSessionDTO();
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        
        dto1.setId(id);
        dto1.setSessionDate(date);
        dto1.setStartTime(startTime);
        dto1.setEndTime(endTime);
        dto1.setCapacity(25);
        dto1.setReservedSpots(15);
        dto1.setTrainerId(UUID.randomUUID());
        dto1.setSessionType("Yoga");
        dto1.setLocation("Main Studio");
        
        GymSessionDTO dto2 = new GymSessionDTO();
        dto2.setId(id);
        dto2.setSessionDate(date);
        dto2.setStartTime(startTime);
        dto2.setEndTime(endTime);
        dto2.setCapacity(25);
        dto2.setReservedSpots(15);
        dto2.setTrainerId(dto1.getTrainerId());
        dto2.setSessionType("Yoga");
        dto2.setLocation("Main Studio");
        
        // Act & Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1, dto1); // Reflexivity test
        assertNotEquals(null, dto1);
        assertNotEquals(new Object(), dto1);
        
        // Modify something and verify they're not equal
        GymSessionDTO dto3 = new GymSessionDTO();
        dto3.setId(UUID.randomUUID()); // Different ID
        dto3.setSessionDate(date);
        dto3.setStartTime(startTime);
        dto3.setEndTime(endTime);
        
        assertNotEquals(dto1, dto3);
    }
    
    @Test
    void testHashCode() {
        // Arrange
        GymSessionDTO dto1 = new GymSessionDTO();
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        
        dto1.setId(id);
        dto1.setSessionDate(date);
        dto1.setCapacity(25);
        
        GymSessionDTO dto2 = new GymSessionDTO();
        dto2.setId(id);
        dto2.setSessionDate(date);
        dto2.setCapacity(25);
        
        // Act & Assert
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        dto2.setCapacity(30);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    void testToString() {
        // Arrange
        GymSessionDTO dto = new GymSessionDTO();
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        String sessionType = "Pilates";
        
        dto.setId(id);
        dto.setSessionDate(date);
        dto.setSessionType(sessionType);
        
        // Act
        String toString = dto.toString();
        
        // Assert
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(date.toString()));
        assertTrue(toString.contains(sessionType));
    }
}