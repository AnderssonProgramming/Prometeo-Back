package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;





public class RoutineDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        RoutineDTO routine = new RoutineDTO();
        UUID id = UUID.randomUUID();
        String name = "Test Routine";
        String description = "Test Description";
        String difficulty = "Intermediate";
        String goal = "Build Muscle";
        UUID trainerId = UUID.randomUUID();
        LocalDate creationDate = LocalDate.now();
        List<RoutineExerciseDTO> exercises = new ArrayList<>();
        
        // Act
        routine.setId(id);
        routine.setName(name);
        routine.setDescription(description);
        routine.setDifficulty(difficulty);
        routine.setGoal(goal);
        routine.setTrainerId(trainerId);
        routine.setCreationDate(creationDate);
        routine.setExercises(exercises);
        
        // Assert
        assertEquals(id, routine.getId());
        assertEquals(name, routine.getName());
        assertEquals(description, routine.getDescription());
        assertEquals(difficulty, routine.getDifficulty());
        assertEquals(goal, routine.getGoal());
        assertEquals(trainerId, routine.getTrainerId());
        assertEquals(creationDate, routine.getCreationDate());
        assertEquals(exercises, routine.getExercises());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID sharedId = UUID.randomUUID();
        LocalDate sharedDate = LocalDate.now();
        
        RoutineDTO routine1 = new RoutineDTO();
        routine1.setId(sharedId);
        routine1.setName("Test Routine");
        routine1.setDescription("Test Description");
        routine1.setCreationDate(sharedDate);
        
        RoutineDTO routine2 = new RoutineDTO();
        routine2.setId(sharedId);
        routine2.setName("Test Routine");
        routine2.setDescription("Test Description");
        routine2.setCreationDate(sharedDate);
        
        RoutineDTO routine3 = new RoutineDTO();
        routine3.setId(UUID.randomUUID());
        routine3.setName("Different Routine");
        
        // Assert - testing equals() behavior from Lombok @Data
        assertEquals(routine1, routine2);
        assertNotEquals(routine1, routine3);
        assertNotEquals(routine1, null);
        assertNotEquals(routine1, new Object());
        
        // Assert - testing hashCode() behavior from Lombok @Data
        assertEquals(routine1.hashCode(), routine2.hashCode());
        assertNotEquals(routine1.hashCode(), routine3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        RoutineDTO routine = new RoutineDTO();
        routine.setId(UUID.randomUUID());
        routine.setName("Test Routine");
        
        // Act
        String toString = routine.toString();
        
        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Test Routine"));
        assertTrue(toString.contains(routine.getId().toString()));
    }
    
    @Test
    public void testExercisesList() {
        // Arrange
        RoutineDTO routine = new RoutineDTO();
        List<RoutineExerciseDTO> exercises = new ArrayList<>();
        RoutineExerciseDTO exercise1 = new RoutineExerciseDTO();
        RoutineExerciseDTO exercise2 = new RoutineExerciseDTO();
        exercises.add(exercise1);
        exercises.add(exercise2);
        
        // Act
        routine.setExercises(exercises);
        
        // Assert
        assertEquals(2, routine.getExercises().size());
        assertTrue(routine.getExercises().contains(exercise1));
        assertTrue(routine.getExercises().contains(exercise2));
    }
}