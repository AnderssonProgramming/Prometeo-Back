package edu.eci.cvds.prometeo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class RoutineTest {

    private Routine routine;
    private UUID routineId;
    private UUID trainerId;
    private LocalDate creationDate;
    private RoutineExercise exercise1;
    private RoutineExercise exercise2;

    @BeforeEach
    public void setUp() {
        routineId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        creationDate = LocalDate.now();
        
        routine = new Routine();
        routine.setId(routineId);
        routine.setName("Test Routine");
        routine.setDescription("Test Description");
        routine.setDifficulty("Intermediate");
        routine.setGoal("Weight Loss");
        routine.setTrainerId(trainerId);
        routine.setCreationDate(creationDate);
        
        // Create test exercise objects
        exercise1 = new RoutineExercise();
        exercise1.setId(UUID.randomUUID());
        exercise1.setSequenceOrder(1);
        
        exercise2 = new RoutineExercise();
        exercise2.setId(UUID.randomUUID());
        exercise2.setSequenceOrder(2);
    }
    
    @Test
    public void testGettersAndSetters() {
        assertEquals(routineId, routine.getId());
        assertEquals("Test Routine", routine.getName());
        assertEquals("Test Description", routine.getDescription());
        assertEquals("Intermediate", routine.getDifficulty());
        assertEquals("Weight Loss", routine.getGoal());
        assertEquals(trainerId, routine.getTrainerId());
        assertEquals(creationDate, routine.getCreationDate());
    }
    
    @Test
    public void testAddExercise() {
        routine.addExercise(exercise1);
        assertEquals(1, routine.getExercises().size());
        assertTrue(routine.getExercises().contains(exercise1));
        
        routine.addExercise(exercise2);
        assertEquals(2, routine.getExercises().size());
        assertTrue(routine.getExercises().contains(exercise2));
    }
    
    @Test
    public void testRemoveExercise() {
        routine.addExercise(exercise1);
        routine.addExercise(exercise2);
        
        assertEquals(2, routine.getExercises().size());
        
        routine.removeExercise(exercise1.getId());
        assertEquals(1, routine.getExercises().size());
        assertFalse(routine.getExercises().contains(exercise1));
        assertTrue(routine.getExercises().contains(exercise2));
    }
    
    @Test
    public void testUpdateExerciseOrder() {
        routine.addExercise(exercise1);
        assertEquals(1, exercise1.getSequenceOrder());
        
        int newOrder = 5;
        routine.updateExerciseOrder(exercise1.getId(), newOrder);
        assertEquals(newOrder, exercise1.getSequenceOrder());
    }
    
    @Test
    public void testUpdateExerciseOrderWithNonExistentId() {
        routine.addExercise(exercise1);
        int originalOrder = exercise1.getSequenceOrder();
        
        // Should not throw exception and should not modify existing exercises
        routine.updateExerciseOrder(UUID.randomUUID(), 10);
        assertEquals(originalOrder, exercise1.getSequenceOrder());
    }
    
    @Test
    public void testSetExercises() {
        List<RoutineExercise> newExercises = new ArrayList<>();
        newExercises.add(exercise1);
        
        routine.setExercises(newExercises);
        
        assertEquals(1, routine.getExercises().size());
        assertTrue(routine.getExercises().contains(exercise1));
    }
    
    @Test
    public void testIsAppropriateFor() {
        PhysicalProgress progress = new PhysicalProgress();
        assertTrue(routine.isAppropriateFor(progress));
    }
    
    @Test
    public void testAllArgsConstructor() {
        List<RoutineExercise> exercises = new ArrayList<>();
        exercises.add(exercise1);
        
        Routine constructedRoutine = new Routine(
            routineId,
            "Test Routine",
            "Test Description",
            "Intermediate",
            "Weight Loss",
            trainerId,
            creationDate,
            exercises
        );
        
        assertEquals(routineId, constructedRoutine.getId());
        assertEquals("Test Routine", constructedRoutine.getName());
        assertEquals("Test Description", constructedRoutine.getDescription());
        assertEquals("Intermediate", constructedRoutine.getDifficulty());
        assertEquals("Weight Loss", constructedRoutine.getGoal());
        assertEquals(trainerId, constructedRoutine.getTrainerId());
        assertEquals(creationDate, constructedRoutine.getCreationDate());
        assertEquals(1, constructedRoutine.getExercises().size());
        assertTrue(constructedRoutine.getExercises().contains(exercise1));
    }
    
    @Test
    public void testNoArgsConstructor() {
        Routine emptyRoutine = new Routine();
        assertNotNull(emptyRoutine);
        assertNotNull(emptyRoutine.getExercises());
        assertTrue(emptyRoutine.getExercises().isEmpty());
    }
}