package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.BaseExercise;
import edu.eci.cvds.prometeo.dto.BaseExerciseDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing base exercises in the system
 */
public interface BaseExerciseService {
    
    /**
     * Creates a new base exercise
     * @param exerciseDTO Data for the new exercise
     * @return The created exercise
     */
    BaseExercise createExercise(BaseExerciseDTO exerciseDTO);
    
    /**
     * Retrieves all base exercises
     * @return List of all exercises
     */
    List<BaseExercise> getAllExercises();
    
    /**
     * Retrieves exercises by muscle group
     * @param muscleGroup The muscle group to filter by
     * @return List of exercises for that muscle group
     */
    List<BaseExercise> getExercisesByMuscleGroup(String muscleGroup);
    
    /**
     * Retrieves a specific exercise by ID
     * @param id The exercise ID
     * @return The exercise if found
     */
    Optional<BaseExercise> getExerciseById(UUID id);
    
    /**
     * Updates an existing exercise
     * @param id The exercise ID
     * @param exerciseDTO The updated data
     * @return The updated exercise
     */
    BaseExercise updateExercise(UUID id, BaseExerciseDTO exerciseDTO);
    
    /**
     * Deletes an exercise (soft delete)
     * @param id The exercise ID
     */
    void deleteExercise(UUID id);
    
    /**
     * Searches exercises by name
     * @param name The search term
     * @return List of matching exercises
     */
    List<BaseExercise> searchExercisesByName(String name);
}