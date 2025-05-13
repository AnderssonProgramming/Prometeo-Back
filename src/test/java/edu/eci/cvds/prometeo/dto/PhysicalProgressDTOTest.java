package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;




public class PhysicalProgressDTOTest {

    @Test
    public void testIdGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

    @Test
    public void testUserIdGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        UUID userId = UUID.randomUUID();
        dto.setUserId(userId);
        assertEquals(userId, dto.getUserId());
    }

    @Test
    public void testRecordDateGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        LocalDate date = LocalDate.now();
        dto.setRecordDate(date);
        assertEquals(date, dto.getRecordDate());
    }

    @Test
    public void testWeightGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        WeightDTO weight = new WeightDTO();
        dto.setWeight(weight);
        assertEquals(weight, dto.getWeight());
    }

    @Test
    public void testMeasurementsGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        BodyMeasurementsDTO measurements = new BodyMeasurementsDTO();
        dto.setMeasurements(measurements);
        assertEquals(measurements, dto.getMeasurements());
    }

    @Test
    public void testPhysicalGoalGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        String goal = "Build more muscle";
        dto.setPhysicalGoal(goal);
        assertEquals(goal, dto.getPhysicalGoal());
    }

    @Test
    public void testTrainerObservationsGetterAndSetter() {
        PhysicalProgressDTO dto = new PhysicalProgressDTO();
        String observations = "Making good progress";
        dto.setTrainerObservations(observations);
        assertEquals(observations, dto.getTrainerObservations());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        PhysicalProgressDTO dto1 = new PhysicalProgressDTO();
        PhysicalProgressDTO dto2 = new PhysicalProgressDTO();
        
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        WeightDTO weight = new WeightDTO();
        BodyMeasurementsDTO measurements = new BodyMeasurementsDTO();
        String goal = "Lose weight";
        String observations = "Good progress";
        
        // Set same values to both
        dto1.setId(id);
        dto1.setUserId(userId);
        dto1.setRecordDate(date);
        dto1.setWeight(weight);
        dto1.setMeasurements(measurements);
        dto1.setPhysicalGoal(goal);
        dto1.setTrainerObservations(observations);
        
        dto2.setId(id);
        dto2.setUserId(userId);
        dto2.setRecordDate(date);
        dto2.setWeight(weight);
        dto2.setMeasurements(measurements);
        dto2.setPhysicalGoal(goal);
        dto2.setTrainerObservations(observations);
        
        // Test equality
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Test inequality
        dto2.setPhysicalGoal("Build muscle");
        assertNotEquals(dto1, dto2);
    }
}