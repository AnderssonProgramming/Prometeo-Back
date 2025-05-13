package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.model.BodyMeasurements;
import edu.eci.cvds.prometeo.repository.PhysicalProgressRepository;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;






public class PhysicalProgressServiceImplTest {

    @Mock
    private PhysicalProgressRepository physicalProgressRepository;

    @InjectMocks
    private PhysicalProgressServiceImpl physicalProgressService;

    private UUID userId;
    private UUID progressId;
    private UUID trainerId;
    private PhysicalProgress testProgress;
    private PhysicalProgress olderProgress;
    private BodyMeasurements testMeasurements;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        userId = UUID.randomUUID();
        progressId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        
        testProgress = mock(PhysicalProgress.class);
        when(testProgress.getId()).thenReturn(progressId);
        when(testProgress.getUserId()).thenReturn(userId);
        when(testProgress.getRecordDate()).thenReturn(LocalDate.now());
        
        olderProgress = mock(PhysicalProgress.class);
        when(olderProgress.getId()).thenReturn(UUID.randomUUID());
        when(olderProgress.getUserId()).thenReturn(userId);
        when(olderProgress.getRecordDate()).thenReturn(LocalDate.now().minusDays(10));
        
        testMeasurements = mock(BodyMeasurements.class);
    }
    
    @Test
    public void testRecordMeasurement() {
        PhysicalProgress inputProgress = mock(PhysicalProgress.class);
        when(physicalProgressRepository.save(any(PhysicalProgress.class))).thenReturn(testProgress);
        
        PhysicalProgress result = physicalProgressService.recordMeasurement(userId, inputProgress);
        
        verify(inputProgress).setUserId(userId);
        verify(inputProgress).setRecordDate(any(LocalDate.class));
        verify(physicalProgressRepository).save(inputProgress);
        assertEquals(testProgress, result);
    }
    
    @Test
    public void testGetMeasurementHistoryNoDateFilters() {
        List<PhysicalProgress> progressList = Arrays.asList(testProgress, olderProgress);
        when(physicalProgressRepository.findByUserId(userId)).thenReturn(progressList);
        
        List<PhysicalProgress> result = physicalProgressService.getMeasurementHistory(
                userId, Optional.empty(), Optional.empty());
        
        assertEquals(2, result.size());
        verify(physicalProgressRepository).findByUserId(userId);
    }
    
    @Test
    public void testGetMeasurementHistoryWithStartDate() {
        List<PhysicalProgress> progressList = Arrays.asList(testProgress, olderProgress);
        when(physicalProgressRepository.findByUserId(userId)).thenReturn(progressList);
        
        LocalDate startDate = LocalDate.now().minusDays(5);
        List<PhysicalProgress> result = physicalProgressService.getMeasurementHistory(
                userId, Optional.of(startDate), Optional.empty());
        
        verify(physicalProgressRepository).findByUserId(userId);
        assertEquals(1, result.size());
    }
    
    @Test
    public void testGetLatestMeasurement() {
        List<PhysicalProgress> progressList = Arrays.asList(testProgress, olderProgress);
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(progressList);
        
        Optional<PhysicalProgress> result = physicalProgressService.getLatestMeasurement(userId);
        
        assertTrue(result.isPresent());
        assertEquals(testProgress, result.get());
    }
    
    @Test
    public void testGetLatestMeasurementEmpty() {
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(Collections.emptyList());
        
        Optional<PhysicalProgress> result = physicalProgressService.getLatestMeasurement(userId);
        
        assertFalse(result.isPresent());
    }
    
    @Test
    public void testUpdateMeasurement() {
        when(physicalProgressRepository.findById(progressId)).thenReturn(Optional.of(testProgress));
        when(physicalProgressRepository.save(testProgress)).thenReturn(testProgress);
        
        PhysicalProgress result = physicalProgressService.updateMeasurement(progressId, testMeasurements);
        
        verify(testProgress).updateMeasurements(testMeasurements);
        verify(physicalProgressRepository).save(testProgress);
        assertEquals(testProgress, result);
    }
    
    @Test
    public void testUpdateMeasurementNotFound() {
        when(physicalProgressRepository.findById(progressId)).thenReturn(Optional.empty());
        
        physicalProgressService.updateMeasurement(progressId, testMeasurements);
    }
    
    @Test
    public void testSetGoal() {
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(
                Arrays.asList(testProgress));
        when(physicalProgressRepository.save(testProgress)).thenReturn(testProgress);
        
        String goal = "Lose 5kg in 2 months";
        PhysicalProgress result = physicalProgressService.setGoal(userId, goal);
        
        verify(testProgress).updateGoal(goal);
        verify(physicalProgressRepository).save(testProgress);
        assertEquals(testProgress, result);
    }
    
    @Test
    public void testSetGoalNoProgressFound() {
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(Collections.emptyList());
        
        physicalProgressService.setGoal(userId, "New Goal");
    }
    
    @Test
    public void testRecordObservation() {
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(
                Arrays.asList(testProgress));
        when(physicalProgressRepository.save(testProgress)).thenReturn(testProgress);
        
        String observation = "Good progress on weight training";
        PhysicalProgress result = physicalProgressService.recordObservation(userId, observation, trainerId);
        
        verify(testProgress).addObservation(observation);
        verify(physicalProgressRepository).save(testProgress);
        assertEquals(testProgress, result);
    }
    
    @Test
    public void testRecordObservationNoProgressFound() {
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(Collections.emptyList());
        
        physicalProgressService.recordObservation(userId, "Observation", trainerId);
    }
    
    @Test
    public void testGetProgressById() {
        when(physicalProgressRepository.findById(progressId)).thenReturn(Optional.of(testProgress));
        
        Optional<PhysicalProgress> result = physicalProgressService.getProgressById(progressId);
        
        assertTrue(result.isPresent());
        assertEquals(testProgress, result.get());
    }
    
    @Test
    public void testCalculateProgressMetrics() {
        // Create test progress entries with weight
        PhysicalProgress latest = mock(PhysicalProgress.class);
        when(latest.getRecordDate()).thenReturn(LocalDate.now());
        
        PhysicalProgress oldest = mock(PhysicalProgress.class);
        when(oldest.getRecordDate()).thenReturn(LocalDate.now().minusMonths(3));
        
        // Mock the weight measurements
        Object latestWeight = mock(Object.class);
        when(latestWeight.toString()).thenReturn("80.0");
        
        Object oldestWeight = mock(Object.class);
        when(oldestWeight.toString()).thenReturn("85.0");
        
        // Mock getValue method if your Measurement class has it
        try {
            when(latestWeight.getClass().getMethod("getValue").invoke(latestWeight)).thenReturn(80.0);
            when(oldestWeight.getClass().getMethod("getValue").invoke(oldestWeight)).thenReturn(85.0);
        } catch (Exception e) {
            // This is just to handle reflection errors in test setup
        }
        
        List<PhysicalProgress> history = Arrays.asList(latest, oldest);
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(history);
        
        Map<String, Double> metrics = physicalProgressService.calculateProgressMetrics(userId, 6);
        
        // This test might need adjustments based on your actual Measurement implementation
        // The verification should check that the repository was called correctly
        verify(physicalProgressRepository).findByUserIdOrderByRecordDateDesc(userId);
    }
    
    @Test
    public void testCalculateProgressMetricsInsufficientData() {
        List<PhysicalProgress> history = Collections.singletonList(testProgress);
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(history);
        
        Map<String, Double> metrics = physicalProgressService.calculateProgressMetrics(userId, 6);
        
        assertTrue(metrics.isEmpty());
    }
}