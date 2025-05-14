package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;





public class RoutineExerciseTest {

    @Test
    public void testDefaultConstructor() {
        RoutineExercise routineExercise = new RoutineExercise();
        assertNotNull(routineExercise);
        assertEquals(0, routineExercise.getSets());
        assertEquals(0, routineExercise.getRepetitions());
        assertEquals(0, routineExercise.getRestTime());
        assertEquals(0, routineExercise.getSequenceOrder());
    }

    @Test
    public void testAllArgsConstructor() {
        UUID routineId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        int sets = 3;
        int repetitions = 12;
        int restTime = 60;
        int sequenceOrder = 1;

        RoutineExercise routineExercise = new RoutineExercise(routineId, exerciseId, sets, repetitions, restTime, sequenceOrder);
        
        assertEquals(routineId, routineExercise.getRoutineId());
        assertEquals(exerciseId, routineExercise.getBaseExerciseId());
        assertEquals(sets, routineExercise.getSets());
        assertEquals(repetitions, routineExercise.getRepetitions());
        assertEquals(restTime, routineExercise.getRestTime());
        assertEquals(sequenceOrder, routineExercise.getSequenceOrder());
    }

    @Test
    public void testUpdateConfiguration() {
        RoutineExercise routineExercise = new RoutineExercise();
        int sets = 4;
        int repetitions = 15;
        int restTime = 45;
        
        routineExercise.updateConfiguration(sets, repetitions, restTime);
        
        assertEquals(sets, routineExercise.getSets());
        assertEquals(repetitions, routineExercise.getRepetitions());
        assertEquals(restTime, routineExercise.getRestTime());
    }

    @Test
    public void testGettersAndSetters() {
        RoutineExercise routineExercise = new RoutineExercise();
        
        UUID routineId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        int sets = 5;
        int repetitions = 10;
        int restTime = 30;
        int sequenceOrder = 2;
        
        routineExercise.setRoutineId(routineId);
        routineExercise.setBaseExerciseId(exerciseId);
        routineExercise.setSets(sets);
        routineExercise.setRepetitions(repetitions);
        routineExercise.setRestTime(restTime);
        routineExercise.setSequenceOrder(sequenceOrder);
        
        assertEquals(routineId, routineExercise.getRoutineId());
        assertEquals(exerciseId, routineExercise.getBaseExerciseId());
        assertEquals(sets, routineExercise.getSets());
        assertEquals(repetitions, routineExercise.getRepetitions());
        assertEquals(restTime, routineExercise.getRestTime());
        assertEquals(sequenceOrder, routineExercise.getSequenceOrder());
    }
}