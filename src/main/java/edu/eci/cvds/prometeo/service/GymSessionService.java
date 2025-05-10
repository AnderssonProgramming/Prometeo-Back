package edu.eci.cvds.prometeo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing gym sessions and time slots
 * Note: This service requires GymSession and TimeSlot entities that don't appear in the provided code.
 * Implementation would need to create these entities.
 */
public interface GymSessionService {
    
    /**
     * Creates a new gym session
     * @param date Date of the session
     * @param startTime Start time
     * @param endTime End time
     * @param capacity Maximum capacity
     * @param description Optional description
     * @param trainerId ID of the trainer creating the session
     * @return ID of the created session
     */
    UUID createSession(LocalDate date, LocalTime startTime, LocalTime endTime, 
                      int capacity, Optional<String> description, UUID trainerId);
    
    /**
     * Updates an existing gym session
     * @param sessionId ID of the session
     * @param date New date
     * @param startTime New start time
     * @param endTime New end time
     * @param capacity New capacity
     * @param trainerId ID of the trainer making the update
     * @return true if successfully updated
     */
    boolean updateSession(UUID sessionId, LocalDate date, LocalTime startTime, 
                         LocalTime endTime, int capacity, UUID trainerId);
    
    /**
     * Cancels a gym session
     * @param sessionId ID of the session
     * @param reason Reason for cancellation
     * @param trainerId ID of the trainer canceling the session
     * @return true if successfully canceled
     */
    boolean cancelSession(UUID sessionId, String reason, UUID trainerId);
    
    /**
     * Gets all sessions for a specific date
     * @param date Date to query
     * @return List of sessions on that date
     */
    List<Object> getSessionsByDate(LocalDate date);
    
    /**
     * Gets sessions created by a specific trainer
     * @param trainerId ID of the trainer
     * @return List of sessions by the trainer
     */
    List<Object> getSessionsByTrainer(UUID trainerId);
    
    /**
     * Gets available time slots for a date
     * @param date Date to check
     * @return List of available time slots
     */
    List<Map<String, Object>> getAvailableTimeSlots(LocalDate date);
    
    /**
     * Configures recurring sessions
     * @param dayOfWeek Day of the week (1-7, where 1 is Monday)
     * @param startTime Start time
     * @param endTime End time
     * @param capacity Maximum capacity
     * @param description Optional description
     * @param trainerId ID of the trainer
     * @param startDate Start date for recurrence
     * @param endDate End date for recurrence
     * @return Number of sessions created
     */
    int configureRecurringSessions(int dayOfWeek, LocalTime startTime, LocalTime endTime,
                                 int capacity, Optional<String> description, UUID trainerId,
                                 LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets occupancy statistics for the gym
     * @param startDate Start date
     * @param endDate End date
     * @return Map of dates to occupancy percentages
     */
    Map<LocalDate, Integer> getOccupancyStatistics(LocalDate startDate, LocalDate endDate);
}