package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;




public class GoalDTOTest {

    @Test
    public void testGoalDTOGettersAndSetters() {
        // Create test data
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        String goalText = "Complete project by end of month";
        boolean active = true;
        
        // Create DTO instance
        GoalDTO goalDTO = new GoalDTO();
        
        // Set values
        goalDTO.setUserId(userId);
        goalDTO.setGoalId(goalId);
        goalDTO.setGoal(goalText);
        goalDTO.setActive(active);
        
        // Assert values using getters
        assertEquals(userId, goalDTO.getUserId());
        assertEquals(goalId, goalDTO.getGoalId());
        assertEquals(goalText, goalDTO.getGoal());
        assertTrue(goalDTO.isActive());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two identical DTOs
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        
        GoalDTO goalDTO1 = new GoalDTO();
        goalDTO1.setUserId(userId);
        goalDTO1.setGoalId(goalId);
        goalDTO1.setGoal("Test goal");
        goalDTO1.setActive(true);
        
        GoalDTO goalDTO2 = new GoalDTO();
        goalDTO2.setUserId(userId);
        goalDTO2.setGoalId(goalId);
        goalDTO2.setGoal("Test goal");
        goalDTO2.setActive(true);
        
        // Assert equals and hashCode
        assertEquals(goalDTO1, goalDTO2);
        assertEquals(goalDTO1.hashCode(), goalDTO2.hashCode());
        
        // Modify one DTO
        goalDTO2.setGoal("Different goal");
        
        // Verify they are no longer equal
        assertNotEquals(goalDTO1, goalDTO2);
    }

    @Test
    public void testToString() {
        // Create DTO with known values
        GoalDTO goalDTO = new GoalDTO();
        UUID userId = UUID.fromString("a7c86c78-952c-4a98-b762-6b5d387aab55");
        UUID goalId = UUID.fromString("b9d23f80-f3d1-49f4-b18a-32a354c86f77");
        goalDTO.setUserId(userId);
        goalDTO.setGoalId(goalId);
        goalDTO.setGoal("Test goal");
        goalDTO.setActive(false);
        
        // Verify toString contains important field data
        String toString = goalDTO.toString();
        assertTrue(toString.contains(userId.toString()));
        assertTrue(toString.contains(goalId.toString()));
        assertTrue(toString.contains("Test goal"));
        assertTrue(toString.contains("false"));
    }
}