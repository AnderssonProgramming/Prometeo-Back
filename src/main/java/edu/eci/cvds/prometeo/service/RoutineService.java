package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.RoutineExercise;
import edu.eci.cvds.prometeo.model.UserRoutine;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing workout routines
 */
public interface RoutineService {
    
    /**
     * Creates a new routine in the system
     * @param routine Data for the routine to create
     * @param trainerId ID of the trainer creating the routine (null for system routines)
     * @return The created routine with its assigned ID
     */
    Routine createRoutine(Routine routine, Optional<UUID> trainerId);
    
    /**
     * Retrieves all routines from the catalog based on filters
     * @param goal Optional filter by goal
     * @param difficulty Optional filter by difficulty level
     * @return List of routines that meet the criteria
     */
    List<Routine> getRoutines(Optional<String> goal, Optional<String> difficulty);
    
    /**
     * Retrieves routines created by a specific trainer
     * @param trainerId ID of the trainer
     * @return List of routines created by the trainer
     */
    List<Routine> getRoutinesByTrainer(UUID trainerId);
    
    /**
     * Assigns a routine to a specific user
     * @param routineId ID of the routine
     * @param userId ID of the user
     * @param trainerId ID of the trainer making the assignment
     * @param startDate Optional start date
     * @param endDate Optional end date
     * @return The created user routine assignment
     */
    UserRoutine assignRoutineToUser(UUID routineId, UUID userId, UUID trainerId, 
                                   Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Retrieves the routines currently assigned to a user
     * @param userId ID of the user
     * @param activeOnly Filter for only active assignments
     * @return List of assigned routines
     */
    List<Routine> getUserRoutines(UUID userId, boolean activeOnly);
    
    /**
     * Updates an existing routine
     * @param routineId ID of the routine
     * @param routine New data for the routine
     * @param trainerId ID of the trainer making the modification
     * @return The updated routine
     */
    Routine updateRoutine(UUID routineId, Routine routine, UUID trainerId);
    
    /**
     * Adds an exercise to a routine
     * @param routineId ID of the routine
     * @param exercise Data for the exercise
     * @return The added exercise with its ID and position in the routine
     */
    RoutineExercise addExerciseToRoutine(UUID routineId, RoutineExercise exercise);
    
    /**
     * Removes an exercise from a routine
     * @param routineId ID of the routine
     * @param exerciseId ID of the exercise to remove
     * @return true if successfully removed
     */
    boolean removeExerciseFromRoutine(UUID routineId, UUID exerciseId);
    
    /**
     * Gets a routine by its ID
     * @param routineId ID of the routine
     * @return Optional containing the routine if found
     */
    Optional<Routine> getRoutineById(UUID routineId);
    
    /**
     * Deactivates a user's routine assignment
     * @param userId ID of the user
     * @param routineId ID of the routine
     * @return true if successfully deactivated
     */
    boolean deactivateUserRoutine(UUID userId, UUID routineId);
}