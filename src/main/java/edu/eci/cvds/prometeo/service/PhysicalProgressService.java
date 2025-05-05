package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.model.BodyMeasurements;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing physical progress tracking of users
 */
public interface PhysicalProgressService {
    
    /**
     * Records a new physical measurement for a user
     * @param userId ID of the user
     * @param physicalProgress Data containing physical measurements
     * @return The created record with its assigned ID
     */
    PhysicalProgress recordMeasurement(UUID userId, PhysicalProgress physicalProgress);
    
    /**
     * Retrieves the complete history of measurements for a user
     * @param userId ID of the user
     * @param startDate Optional start date for filtering
     * @param endDate Optional end date for filtering
     * @return List of physical progress records
     */
    List<PhysicalProgress> getMeasurementHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Gets the latest physical progress record for a user
     * @param userId ID of the user
     * @return Optional containing the latest record if found
     */
    Optional<PhysicalProgress> getLatestMeasurement(UUID userId);
    
    /**
     * Updates an existing physical measurement
     * @param progressId ID of the record to update
     * @param measurements New measurement data
     * @return The updated record
     */
    PhysicalProgress updateMeasurement(UUID progressId, BodyMeasurements measurements);
    
    /**
     * Sets a physical goal for a user
     * @param userId ID of the user
     * @param goal Description of the goal
     * @return Updated physical progress with the goal
     */
    PhysicalProgress setGoal(UUID userId, String goal);
    
    /**
     * Records medical observations for a user
     * @param userId ID of the user
     * @param observation Text of the observation
     * @param trainerId ID of the trainer making the observation
     * @return Updated physical progress with the observation
     */
    PhysicalProgress recordObservation(UUID userId, String observation, UUID trainerId);
    
    /**
     * Gets a physical progress record by ID
     * @param progressId ID of the record
     * @return Optional containing the record if found
     */
    Optional<PhysicalProgress> getProgressById(UUID progressId);
    
    /**
     * Calculates progress metrics for a user
     * @param userId ID of the user
     * @param months Number of months to analyze
     * @return Map of metric names to values
     */
    java.util.Map<String, Double> calculateProgressMetrics(UUID userId, int months);
}