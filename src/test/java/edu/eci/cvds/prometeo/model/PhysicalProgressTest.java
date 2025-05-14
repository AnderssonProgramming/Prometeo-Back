package edu.eci.cvds.prometeo.model;


import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class PhysicalProgressTest {
    
    @Test
    public void testNoArgsConstructor() {
        PhysicalProgress progress = new PhysicalProgress();
        assertNotNull(progress);
    }
    
    @Test
    public void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        LocalDate recordDate = LocalDate.now();
        Routine routine = new Routine();
        Weight weight = new Weight(70.5, Weight.WeightUnit.KG);
        BodyMeasurements measurements = new BodyMeasurements();
        String goal = "Lose weight";
        String observations = "Making good progress";
        
        PhysicalProgress progress = new PhysicalProgress(userId, recordDate, routine, weight, measurements, goal, observations);
        
        assertEquals(userId, progress.getUserId());
        assertEquals(recordDate, progress.getRecordDate());
        assertEquals(routine, progress.getActiveRoutine());
        assertEquals(weight, progress.getWeight());
        assertEquals(measurements, progress.getMeasurements());
        assertEquals(goal, progress.getPhysicalGoal());
        assertEquals(observations, progress.getTrainerObservations());
    }
    
    @Test
    public void testGettersAndSetters() {
        PhysicalProgress progress = new PhysicalProgress();
        
        UUID userId = UUID.randomUUID();
        LocalDate recordDate = LocalDate.now();
        Routine routine = new Routine();
        Weight weight = new Weight(70.5, Weight.WeightUnit.KG);
        BodyMeasurements measurements = new BodyMeasurements();
        String goal = "Lose weight";
        String observations = "Making good progress";
        
        progress.setUserId(userId);
        progress.setRecordDate(recordDate);
        progress.setActiveRoutine(routine);
        progress.setWeight(weight);
        progress.setMeasurements(measurements);
        progress.setPhysicalGoal(goal);
        progress.setTrainerObservations(observations);
        
        assertEquals(userId, progress.getUserId());
        assertEquals(recordDate, progress.getRecordDate());
        assertEquals(routine, progress.getActiveRoutine());
        assertEquals(weight, progress.getWeight());
        assertEquals(measurements, progress.getMeasurements());
        assertEquals(goal, progress.getPhysicalGoal());
        assertEquals(observations, progress.getTrainerObservations());
    }
    
    @Test
    public void testUpdateWeightWhenWeightIsNull() {
        PhysicalProgress progress = new PhysicalProgress();
        assertNull(progress.getWeight());
        
        double weightValue = 75.5;
        progress.updateWeight(weightValue);
        
        assertNotNull(progress.getWeight());
        assertEquals(weightValue, progress.getWeight().getValue(), 0.001);
        assertEquals(Weight.WeightUnit.KG, progress.getWeight().getUnit());
    }
    
    @Test
    public void testUpdateWeightWhenWeightExists() {
        PhysicalProgress progress = new PhysicalProgress();
        progress.setWeight(new Weight(70.0, Weight.WeightUnit.KG));
        
        double newWeightValue = 72.5;
        progress.updateWeight(newWeightValue);
        
        assertEquals(newWeightValue, progress.getWeight().getValue(), 0.001);
        assertEquals(Weight.WeightUnit.KG, progress.getWeight().getUnit());
    }
    
    @Test
    public void testUpdateMeasurements() {
        PhysicalProgress progress = new PhysicalProgress();
        BodyMeasurements measurements = new BodyMeasurements();
        
        progress.updateMeasurements(measurements);
        
        assertEquals(measurements, progress.getMeasurements());
    }
    
    @Test
    public void testUpdateGoal() {
        PhysicalProgress progress = new PhysicalProgress();
        String goal = "Build muscle";
        
        progress.updateGoal(goal);
        
        assertEquals(goal, progress.getPhysicalGoal());
    }
    
    @Test
    public void testAddObservation() {
        PhysicalProgress progress = new PhysicalProgress();
        String observation = "Client is adhering to routine";
        
        progress.addObservation(observation);
        
        assertEquals(observation, progress.getTrainerObservations());
    }
}