package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.dto.BaseExerciseDTO;
import edu.eci.cvds.prometeo.model.BaseExercise;
import edu.eci.cvds.prometeo.repository.BaseExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






@ExtendWith(MockitoExtension.class)
public class BaseExerciseServiceImplTest {

    @Mock
    private BaseExerciseRepository baseExerciseRepository;

    @InjectMocks
    private BaseExerciseServiceImpl baseExerciseService;

    private UUID exerciseId;
    private BaseExerciseDTO exerciseDTO;
    private BaseExercise exercise;

    @BeforeEach
    void setUp() {
        exerciseId = UUID.randomUUID();
        
        exerciseDTO = new BaseExerciseDTO();
        exerciseDTO.setName("Bench Press");
        exerciseDTO.setDescription("Chest exercise");
        exerciseDTO.setMuscleGroup("Chest");
        exerciseDTO.setEquipment("Barbell");
        exerciseDTO.setVideoUrl("http://example.com/video");
        exerciseDTO.setImageUrl("http://example.com/image");

        exercise = new BaseExercise();
        exercise.setId(exerciseId);
        exercise.setName("Bench Press");
        exercise.setDescription("Chest exercise");
        exercise.setMuscleGroup("Chest");
        exercise.setEquipment("Barbell");
        exercise.setVideoUrl("http://example.com/video");
        exercise.setImageUrl("http://example.com/image");
    }

    @Test
    void testCreateExercise() {
        when(baseExerciseRepository.save(any(BaseExercise.class))).thenReturn(exercise);

        BaseExercise result = baseExerciseService.createExercise(exerciseDTO);

        assertNotNull(result);
        assertEquals(exerciseId, result.getId());
        assertEquals(exerciseDTO.getName(), result.getName());
        assertEquals(exerciseDTO.getDescription(), result.getDescription());
        assertEquals(exerciseDTO.getMuscleGroup(), result.getMuscleGroup());
        assertEquals(exerciseDTO.getEquipment(), result.getEquipment());
        assertEquals(exerciseDTO.getVideoUrl(), result.getVideoUrl());
        assertEquals(exerciseDTO.getImageUrl(), result.getImageUrl());
        
        verify(baseExerciseRepository).save(any(BaseExercise.class));
    }

    @Test
    void testGetAllExercises() {
        List<BaseExercise> exercises = Arrays.asList(exercise);
        when(baseExerciseRepository.findByDeletedAtIsNull()).thenReturn(exercises);

        List<BaseExercise> result = baseExerciseService.getAllExercises();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exerciseId, result.get(0).getId());
        verify(baseExerciseRepository).findByDeletedAtIsNull();
    }

    @Test
    void testGetExercisesByMuscleGroup() {
        List<BaseExercise> exercises = Arrays.asList(exercise);
        when(baseExerciseRepository.findByMuscleGroup("Chest")).thenReturn(exercises);

        List<BaseExercise> result = baseExerciseService.getExercisesByMuscleGroup("Chest");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chest", result.get(0).getMuscleGroup());
        verify(baseExerciseRepository).findByMuscleGroup("Chest");
    }

    @Test
    void testGetExerciseById() {
        when(baseExerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        Optional<BaseExercise> result = baseExerciseService.getExerciseById(exerciseId);

        assertTrue(result.isPresent());
        assertEquals(exerciseId, result.get().getId());
        verify(baseExerciseRepository).findById(exerciseId);
    }

    @Test
    void testUpdateExercise() {
        when(baseExerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(baseExerciseRepository.save(any(BaseExercise.class))).thenReturn(exercise);

        // Update the DTO with new values
        exerciseDTO.setName("Updated Bench Press");
        exerciseDTO.setDescription("Updated chest exercise");

        BaseExercise result = baseExerciseService.updateExercise(exerciseId, exerciseDTO);

        assertNotNull(result);
        assertEquals(exerciseDTO.getName(), result.getName());
        assertEquals(exerciseDTO.getDescription(), result.getDescription());
        verify(baseExerciseRepository).findById(exerciseId);
        verify(baseExerciseRepository).save(any(BaseExercise.class));
    }

    @Test
    void testUpdateExerciseNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(baseExerciseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> baseExerciseService.updateExercise(nonExistentId, exerciseDTO));
        verify(baseExerciseRepository).findById(nonExistentId);
        verify(baseExerciseRepository, never()).save(any(BaseExercise.class));
    }

    @Test
    void testDeleteExercise() {
        when(baseExerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(baseExerciseRepository.save(any(BaseExercise.class))).thenReturn(exercise);

        baseExerciseService.deleteExercise(exerciseId);

        assertNotNull(exercise.getDeletedAt());
        verify(baseExerciseRepository).findById(exerciseId);
        verify(baseExerciseRepository).save(exercise);
    }

    @Test
    void testDeleteExerciseNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(baseExerciseRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> baseExerciseService.deleteExercise(nonExistentId));
        verify(baseExerciseRepository).findById(nonExistentId);
        verify(baseExerciseRepository, never()).save(any(BaseExercise.class));
    }

    @Test
    void testSearchExercisesByName() {
        List<BaseExercise> exercises = Arrays.asList(exercise);
        when(baseExerciseRepository.findByNameContainingIgnoreCase("Bench")).thenReturn(exercises);

        List<BaseExercise> result = baseExerciseService.searchExercisesByName("Bench");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bench Press", result.get(0).getName());
        verify(baseExerciseRepository).findByNameContainingIgnoreCase("Bench");
    }
}