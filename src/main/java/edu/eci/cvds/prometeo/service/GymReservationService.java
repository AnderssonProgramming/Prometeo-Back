package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.ReservationDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing gym reservations
 */
public interface GymReservationService {
    
    /**
     * Get all reservations
     * @return List of all reservations
     */
    List<ReservationDTO> getAll();
    
    /**
     * Get reservations by user ID
     * @param userId User ID
     * @return List of user's reservations
     */
    List<ReservationDTO> getByUserId(UUID userId);
    
    /**
     * Get reservation by ID
     * @param id Reservation ID
     * @return Reservation if found
     */
    Optional<ReservationDTO> getById(UUID id);
    
    /**
     * Create a new reservation
     * @param dto Reservation data
     * @return Created reservation
     */
    ReservationDTO create(ReservationDTO dto);
    
    /**
     * Delete/cancel a reservation
     * @param id Reservation ID
     */
    void delete(UUID id);
    
    /**
     * Check gym availability
     * @param date Date to check
     * @param time Time to check
     * @return Availability information
     */
    Map<String, Object> getAvailability(LocalDate date, LocalTime time);
    
    /**
     * Join waitlist for a full session
     * @param userId User ID
     * @param sessionId Session ID
     * @return true if added successfully
     */
    boolean joinWaitlist(UUID userId, UUID sessionId);
    
    /**
     * Get waitlist status for a user
     * @param userId User ID
     * @param sessionId Session ID
     * @return Waitlist status information
     */
    Map<String, Object> getWaitlistStatus(UUID userId, UUID sessionId);
    
    /**
     * Get all sessions where user is in waitlist
     * @param userId User ID
     * @return List of waitlist sessions
     */
    List<Map<String, Object>> getUserWaitlists(UUID userId);
    
    /**
     * Leave a waitlist
     * @param userId User ID
     * @param sessionId Session ID
     * @return true if removed successfully
     */
    boolean leaveWaitlist(UUID userId, UUID sessionId);
}