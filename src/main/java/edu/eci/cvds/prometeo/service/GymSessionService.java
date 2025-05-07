package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GymSessionService {
    
    // Create a new gym session (FR5)
    UUID createSession(LocalDate date, LocalTime startTime, LocalTime endTime, 
                      int capacity, Optional<String> description, UUID trainerId);
    
    // Update an existing session (FR5)
    boolean updateSession(UUID sessionId, LocalDate date, LocalTime startTime, 
                         LocalTime endTime, int capacity, UUID trainerId);
    
    // Cancel a session (FR5)
    boolean cancelSession(UUID sessionId, String reason, UUID trainerId);
    
    // Get sessions by date (FR4 & FR5)
    List<GymSession> getSessionsByDate(LocalDate date);
    
    // Get sessions by trainer (FR5)
    List<GymSession> getSessionsByTrainer(UUID trainerId);
    
    // Get session by id
    Optional<GymSession> getSessionById(UUID sessionId);
    
    // Get all users registered for a session (FR5)
    List<User> getRegisteredUsersForSession(UUID sessionId);
    
    // Mark attendance for users in a session (FR5)
    boolean markAttendance(UUID sessionId, UUID userId, boolean attended, UUID trainerId);
    
    // Get session occupancy statistics (FR5)
    Map<String, Object> getSessionOccupancyStats(UUID trainerId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    // Get attendance statistics for a specific session (FR5)
    Map<String, Object> getAttendanceStatsForSession(UUID sessionId);
}