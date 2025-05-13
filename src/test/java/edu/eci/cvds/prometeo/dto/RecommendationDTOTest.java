package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;





public class RecommendationDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        RecommendationDTO recommendationDTO = new RecommendationDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        boolean active = true;

        // Act
        recommendationDTO.setId(id);
        recommendationDTO.setUserId(userId);
        recommendationDTO.setRoutineId(routineId);
        recommendationDTO.setActive(active);

        // Assert
        assertEquals(id, recommendationDTO.getId());
        assertEquals(userId, recommendationDTO.getUserId());
        assertEquals(routineId, recommendationDTO.getRoutineId());
        assertTrue(recommendationDTO.isActive());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        boolean active = true;

        RecommendationDTO dto1 = new RecommendationDTO();
        dto1.setId(id);
        dto1.setUserId(userId);
        dto1.setRoutineId(routineId);
        dto1.setActive(active);

        RecommendationDTO dto2 = new RecommendationDTO();
        dto2.setId(id);
        dto2.setUserId(userId);
        dto2.setRoutineId(routineId);
        dto2.setActive(active);

        RecommendationDTO dto3 = new RecommendationDTO();
        dto3.setId(UUID.randomUUID());
        dto3.setUserId(userId);
        dto3.setRoutineId(routineId);
        dto3.setActive(active);

        // Act & Assert
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        RecommendationDTO recommendationDTO = new RecommendationDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        boolean active = true;

        recommendationDTO.setId(id);
        recommendationDTO.setUserId(userId);
        recommendationDTO.setRoutineId(routineId);
        recommendationDTO.setActive(active);

        // Act
        String toString = recommendationDTO.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(userId.toString()));
        assertTrue(toString.contains(routineId.toString()));
        assertTrue(toString.contains(String.valueOf(active)));
    }
}