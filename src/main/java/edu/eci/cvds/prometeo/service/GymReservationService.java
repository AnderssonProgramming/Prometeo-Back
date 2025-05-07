package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GymReservationService {
    
    // Make reservation (FR4)
    UUID makeReservation(UUID userId, UUID sessionId, Optional<List<UUID>> equipmentIds);
    
    // Cancel reservation (FR4)
    boolean cancelReservation(UUID reservationId, UUID userId, Optional<String> reason);
    
    // Get upcoming reservations for a user (FR4)
    List<ReservationDTO> getUpcomingReservations(UUID userId);
    
    // Get reservation history (FR4)
    List<ReservationDTO> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    // Check if a session has availability (FR4)
    boolean checkSessionAvailability(UUID sessionId);
    
    // Get a specific reservation by ID
    Optional<ReservationDTO> getReservationById(UUID reservationId);
    
    // Record attendance for a reservation (FR5)
    boolean recordAttendance(UUID reservationId, boolean attended, UUID trainerId);
    
    // Add user to waitlist for a session (FR4)
    boolean addToWaitlist(UUID userId, UUID sessionId);
    
    // Get waitlist for a session (FR5)
    List<Map<String, Object>> getWaitlistForSession(UUID sessionId);
    
    // Notify next person on waitlist (FR4)
    boolean notifyNextWaitlistedUser(UUID sessionId);
    
    // Get all reservations for a session (FR5)
    List<ReservationDTO> getSessionReservations(UUID sessionId, Optional<ReservationStatus> status);
    
    // Get available sessions by date (FR4)
    List<GymSession> getAvailableSessionsByDate(LocalDate date);
    
    // Request notification for waitlist (FR4)
    boolean requestWaitlistNotification(UUID userId, UUID sessionId);
}