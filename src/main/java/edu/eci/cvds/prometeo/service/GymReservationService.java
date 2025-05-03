package edu.eci.cvds.prometeo.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing gym reservations
 * Note: This service requires a Reservation entity that doesn't appear in the provided code.
 * Implementation would need to create this entity.
 */
public interface GymReservationService {
    
    /**
     * Makes a reservation at the gym
     * @param userId ID of the user
     * @param date Date of the reservation
     * @param startTime Start time
     * @param endTime End time
     * @param equipmentIds Optional IDs of equipment to reserve
     * @return ID of the created reservation
     */
    UUID makeReservation(UUID userId, LocalDate date, LocalTime startTime, LocalTime endTime, Optional<List<UUID>> equipmentIds);
    
    /**
     * Cancels an existing reservation
     * @param reservationId ID of the reservation
     * @param userId ID of the user canceling
     * @param reason Optional reason for cancellation
     * @return true if successfully canceled
     */
    boolean cancelReservation(UUID reservationId, UUID userId, Optional<String> reason);
    
    /**
     * Gets upcoming reservations for a user
     * @param userId ID of the user
     * @return List of pending reservations
     */
    List<Object> getUpcomingReservations(UUID userId);
    
    /**
     * Gets the reservation history for a user
     * @param userId ID of the user
     * @param startDate Optional start date for filtering
     * @param endDate Optional end date for filtering
     * @return List of historical reservations
     */
    List<Object> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Updates the time of a reservation
     * @param reservationId ID of the reservation
     * @param newDate New date
     * @param newStartTime New start time
     * @param newEndTime New end time
     * @param userId ID of the user making the update
     * @return true if successfully updated
     */
    boolean updateReservationTime(UUID reservationId, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, UUID userId);
    
    /**
     * Checks availability for a specific date and time range
     * @param date Date to check
     * @param startTime Start time
     * @param endTime End time
     * @return true if the slot is available
     */
    boolean checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * Gets available time slots for a date
     * @param date Date to check
     * @return List of available time slots
     */
    List<Object> getAvailableTimeSlots(LocalDate date);
    
    /**
     * Records attendance for a reservation
     * @param reservationId ID of the reservation
     * @param attended Whether the user attended
     * @param trainerId ID of the trainer recording attendance
     * @return true if successfully recorded
     */
    boolean recordAttendance(UUID reservationId, boolean attended, UUID trainerId);
}