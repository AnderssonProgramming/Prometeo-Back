package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;




public class RoutineExerciseDTOTest {

    @Test
    public void testCreateInstance() {
        RoutineExerciseDTO dto = new RoutineExerciseDTO();
        assertNotNull(dto);
    }

    @Test
    public void testGettersAndSetters() {
        // Create test data
        UUID id = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        UUID baseExerciseId = UUID.randomUUID();
        int sets = 3;
        int repetitions = 12;
        int restTime = 60;
        int sequenceOrder = 1;

        // Create and populate DTO
        RoutineExerciseDTO dto = new RoutineExerciseDTO();
        dto.setId(id);
        dto.setRoutineId(routineId);
        dto.setBaseExerciseId(baseExerciseId);
        dto.setSets(sets);
        dto.setRepetitions(repetitions);
        dto.setRestTime(restTime);
        dto.setSequenceOrder(sequenceOrder);

        // Test getters
        assertEquals(id, dto.getId());
        assertEquals(routineId, dto.getRoutineId());
        assertEquals(baseExerciseId, dto.getBaseExerciseId());
        assertEquals(sets, dto.getSets());
        assertEquals(repetitions, dto.getRepetitions());
        assertEquals(restTime, dto.getRestTime());
        assertEquals(sequenceOrder, dto.getSequenceOrder());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two identical DTOs
        UUID id = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        UUID baseExerciseId = UUID.randomUUID();

        RoutineExerciseDTO dto1 = new RoutineExerciseDTO();
        dto1.setId(id);
        dto1.setRoutineId(routineId);
        dto1.setBaseExerciseId(baseExerciseId);
        dto1.setSets(3);
        dto1.setRepetitions(12);
        dto1.setRestTime(60);
        dto1.setSequenceOrder(1);

        RoutineExerciseDTO dto2 = new RoutineExerciseDTO();
        dto2.setId(id);
        dto2.setRoutineId(routineId);
        dto2.setBaseExerciseId(baseExerciseId);
        dto2.setSets(3);
        dto2.setRepetitions(12);
        dto2.setRestTime(60);
        dto2.setSequenceOrder(1);

        // Test equals and hashCode
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // Modify one field and test inequality
        dto2.setSets(4);
        assertNotEquals(dto1, dto2);
    }

    @Test
    public void testToString() {
        RoutineExerciseDTO dto = new RoutineExerciseDTO();
        dto.setId(UUID.randomUUID());
        dto.setSets(3);
        
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("sets=3"));
        assertTrue(toString.contains("id="));
    }
}