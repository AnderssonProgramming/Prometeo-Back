package edu.eci.cvds.prometeo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing gym reservations
 */
public interface GymReservationService {
    
    /**
     * Makes a new reservation for a gym session
     * 
     * @param userId User making the reservation
     * @param date Date of the reservation
     * @param startTime Start time of the reservation
     * @param endTime End time of the reservation
     * @param equipmentIds Optional list of equipment IDs to reserve
     * @return The ID of the created reservation
     */
    UUID makeReservation(UUID userId, LocalDate date, LocalTime startTime, LocalTime endTime, Optional<List<UUID>> equipmentIds);
    
    /**
     * Cancels an existing reservation
     * 
     * @param reservationId ID of the reservation to cancel
     * @param userId ID of the user attempting to cancel
     * @param reason Optional reason for cancellation
     * @return true if successfully cancelled
     */
    boolean cancelReservation(UUID reservationId, UUID userId, Optional<String> reason);
    
    /**
     * Gets upcoming reservations for a user
     * 
     * @param userId ID of the user
     * @return List of upcoming reservation details
     */
    List<Object> getUpcomingReservations(UUID userId);
    
    /**
     * Gets reservation history for a user within a date range
     * 
     * @param userId ID of the user
     * @param startDate Optional start date for the range
     * @param endDate Optional end date for the range
     * @return List of past reservation details
     */
    List<Object> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Updates the time of an existing reservation
     * 
     * @param reservationId ID of the reservation to update
     * @param newDate New date for the reservation
     * @param newStartTime New start time
     * @param newEndTime New end time
     * @param userId ID of the user attempting to update
     * @return true if successfully updated
     */
    boolean updateReservationTime(UUID reservationId, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, UUID userId);
    
    /**
     * Checks if a time slot is available for reservation
     * 
     * @param date Date to check
     * @param startTime Start time to check
     * @param endTime End time to check
     * @return true if the time slot is available
     */
    boolean checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * Gets available time slots for a specific date
     * 
     * @param date Date to check
     * @return List of available time slots with details
     */
    List<Object> getAvailableTimeSlots(LocalDate date);
    
    /**
     * Records attendance for a reservation
     * 
     * @param reservationId ID of the reservation
     * @param attended Whether the user attended
     * @param trainerId ID of the trainer recording attendance
     * @return true if successfully recorded
     */
    boolean recordAttendance(UUID reservationId, boolean attended, UUID trainerId);
}