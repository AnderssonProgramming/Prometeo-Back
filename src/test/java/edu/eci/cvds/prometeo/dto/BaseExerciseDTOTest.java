package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;





public class BaseExerciseDTOTest {

    @Test
    public void testGettersAndSetters() {
        // Arrange
        BaseExerciseDTO dto = new BaseExerciseDTO();
        UUID id = UUID.randomUUID();
        String name = "Push-up";
        String description = "Basic bodyweight exercise";
        String muscleGroup = "Chest";
        String equipment = "None";
        String videoUrl = "https://example.com/video";
        String imageUrl = "https://example.com/image";
        
        // Act
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setMuscleGroup(muscleGroup);
        dto.setEquipment(equipment);
        dto.setVideoUrl(videoUrl);
        dto.setImageUrl(imageUrl);
        
        // Assert
        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(muscleGroup, dto.getMuscleGroup());
        assertEquals(equipment, dto.getEquipment());
        assertEquals(videoUrl, dto.getVideoUrl());
        assertEquals(imageUrl, dto.getImageUrl());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        BaseExerciseDTO dto1 = new BaseExerciseDTO();
        BaseExerciseDTO dto2 = new BaseExerciseDTO();
        
        UUID id = UUID.randomUUID();
        dto1.setId(id);
        dto1.setName("Squat");
        dto1.setDescription("Lower body exercise");
        dto1.setMuscleGroup("Legs");
        dto1.setEquipment("None");
        dto1.setVideoUrl("https://example.com/squat-video");
        dto1.setImageUrl("https://example.com/squat-image");
        
        dto2.setId(id);
        dto2.setName("Squat");
        dto2.setDescription("Lower body exercise");
        dto2.setMuscleGroup("Legs");
        dto2.setEquipment("None");
        dto2.setVideoUrl("https://example.com/squat-video");
        dto2.setImageUrl("https://example.com/squat-image");
        
        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Modify one field to test inequality
        dto2.setName("Different Exercise");
        assertNotEquals(dto1, dto2);
    }
    
    @Test
    public void testToString() {
        // Arrange
        BaseExerciseDTO dto = new BaseExerciseDTO();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setName("Deadlift");
        
        // Act
        String toStringResult = dto.toString();
        
        // Assert
        assertTrue(toStringResult.contains("Deadlift"));
        assertTrue(toStringResult.contains(id.toString()));
    }
}