package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;



public class UserRoutineDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        UserRoutineDTO userRoutineDTO = new UserRoutineDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        LocalDate assignmentDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        boolean active = true;

        // Act
        userRoutineDTO.setId(id);
        userRoutineDTO.setUserId(userId);
        userRoutineDTO.setRoutineId(routineId);
        userRoutineDTO.setAssignmentDate(assignmentDate);
        userRoutineDTO.setEndDate(endDate);
        userRoutineDTO.setActive(active);

        // Assert
        assertEquals(id, userRoutineDTO.getId());
        assertEquals(userId, userRoutineDTO.getUserId());
        assertEquals(routineId, userRoutineDTO.getRoutineId());
        assertEquals(assignmentDate, userRoutineDTO.getAssignmentDate());
        assertEquals(endDate, userRoutineDTO.getEndDate());
        assertEquals(active, userRoutineDTO.isActive());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        LocalDate assignmentDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);

        UserRoutineDTO userRoutineDTO1 = new UserRoutineDTO();
        userRoutineDTO1.setId(id);
        userRoutineDTO1.setUserId(userId);
        userRoutineDTO1.setRoutineId(routineId);
        userRoutineDTO1.setAssignmentDate(assignmentDate);
        userRoutineDTO1.setEndDate(endDate);
        userRoutineDTO1.setActive(true);

        UserRoutineDTO userRoutineDTO2 = new UserRoutineDTO();
        userRoutineDTO2.setId(id);
        userRoutineDTO2.setUserId(userId);
        userRoutineDTO2.setRoutineId(routineId);
        userRoutineDTO2.setAssignmentDate(assignmentDate);
        userRoutineDTO2.setEndDate(endDate);
        userRoutineDTO2.setActive(true);

        UserRoutineDTO differentUserRoutineDTO = new UserRoutineDTO();
        differentUserRoutineDTO.setId(UUID.randomUUID());
        differentUserRoutineDTO.setUserId(UUID.randomUUID());
        differentUserRoutineDTO.setRoutineId(UUID.randomUUID());

        // Assert
        assertEquals(userRoutineDTO1, userRoutineDTO2);
        assertEquals(userRoutineDTO1.hashCode(), userRoutineDTO2.hashCode());
        assertNotEquals(userRoutineDTO1, differentUserRoutineDTO);
        assertNotEquals(userRoutineDTO1.hashCode(), differentUserRoutineDTO.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        UserRoutineDTO userRoutineDTO = new UserRoutineDTO();
        UUID id = UUID.randomUUID();
        userRoutineDTO.setId(id);

        // Assert
        String toStringResult = userRoutineDTO.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains(id.toString()));
    }
}