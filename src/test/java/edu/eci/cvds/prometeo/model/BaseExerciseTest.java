package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class BaseExerciseTest {

    @Test
    public void testDefaultConstructor() {
        BaseExercise exercise = new BaseExercise();
        assertNull(exercise.getName());
        assertNull(exercise.getDescription());
        assertNull(exercise.getMuscleGroup());
        assertNull(exercise.getEquipment());
        assertNull(exercise.getVideoUrl());
        assertNull(exercise.getImageUrl());
    }

    @Test
    public void testAllArgsConstructor() {
        BaseExercise exercise = new BaseExercise(
            "Push-up",
            "Standard push-up exercise",
            "Chest",
            "None",
            "http://example.com/pushup.mp4",
            "http://example.com/pushup.jpg"
        );
        
        assertEquals("Push-up", exercise.getName());
        assertEquals("Standard push-up exercise", exercise.getDescription());
        assertEquals("Chest", exercise.getMuscleGroup());
        assertEquals("None", exercise.getEquipment());
        assertEquals("http://example.com/pushup.mp4", exercise.getVideoUrl());
        assertEquals("http://example.com/pushup.jpg", exercise.getImageUrl());
    }

    @Test
    public void testGettersAndSetters() {
        BaseExercise exercise = new BaseExercise();
        
        exercise.setName("Squat");
        assertEquals("Squat", exercise.getName());
        
        exercise.setDescription("Standard squat exercise");
        assertEquals("Standard squat exercise", exercise.getDescription());
        
        exercise.setMuscleGroup("Legs");
        assertEquals("Legs", exercise.getMuscleGroup());
        
        exercise.setEquipment("Barbell");
        assertEquals("Barbell", exercise.getEquipment());
        
        exercise.setVideoUrl("http://example.com/squat.mp4");
        assertEquals("http://example.com/squat.mp4", exercise.getVideoUrl());
        
        exercise.setImageUrl("http://example.com/squat.jpg");
        assertEquals("http://example.com/squat.jpg", exercise.getImageUrl());
    }

    @Test
    public void testRequiresEquipment() {
        BaseExercise exercise = new BaseExercise();
        
        // When equipment is null
        exercise.setEquipment(null);
        assertFalse(exercise.requiresEquipment());
        
        // When equipment is empty
        exercise.setEquipment("");
        assertFalse(exercise.requiresEquipment());
        
        // When equipment is "none" (case insensitive)
        exercise.setEquipment("None");
        assertFalse(exercise.requiresEquipment());
        
        exercise.setEquipment("NONE");
        assertFalse(exercise.requiresEquipment());
        
        exercise.setEquipment("none");
        assertFalse(exercise.requiresEquipment());
        
        // When equipment has a valid value
        exercise.setEquipment("Dumbbells");
        assertTrue(exercise.requiresEquipment());
        
        exercise.setEquipment("Barbell");
        assertTrue(exercise.requiresEquipment());
    }
}