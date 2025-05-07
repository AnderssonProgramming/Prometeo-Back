package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.service.GymReservationService;
import edu.eci.cvds.prometeo.service.GymSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/gym/sessions")
public class GymSessionController {
    
    private final GymSessionService gymSessionService;
    private final GymReservationService gymReservationService;
    
    @Autowired
    public GymSessionController(
            GymSessionService gymSessionService,
            GymReservationService gymReservationService) {
        this.gymSessionService = gymSessionService;
        this.gymReservationService = gymReservationService;
    }
    
    // Get available sessions by date (FR4)
    @GetMapping("/available")
    public ResponseEntity<List<GymSession>> getAvailableSessions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(gymReservationService.getAvailableSessionsByDate(date));
    }
    
    // Get sessions by date (FR4, FR5)
    @GetMapping("/by-date")
    public ResponseEntity<List<GymSession>> getSessionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(gymSessionService.getSessionsByDate(date));
    }
    
    // Get session by ID
    @GetMapping("/{sessionId}")
    public ResponseEntity<GymSession> getSessionById(@PathVariable UUID sessionId) {
        return gymSessionService.getSessionById(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get sessions by trainer (FR5)
    @GetMapping("/by-trainer/{trainerId}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<GymSession>> getSessionsByTrainer(@PathVariable UUID trainerId) {
        return ResponseEntity.ok(gymSessionService.getSessionsByTrainer(trainerId));
    }
    
    // Create a new session (FR5)
    @PostMapping
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<UUID> createSession(@RequestBody Map<String, Object> request) {
        LocalDate date = LocalDate.parse((String) request.get("date"));
        LocalTime startTime = LocalTime.parse((String) request.get("startTime"));
        LocalTime endTime = LocalTime.parse((String) request.get("endTime"));
        int capacity = (Integer) request.get("capacity");
        UUID trainerId = UUID.fromString((String) request.get("trainerId"));
        String description = request.get("description") != null ? (String) request.get("description") : null;
        
        UUID sessionId = gymSessionService.createSession(
                date, startTime, endTime, capacity, Optional.ofNullable(description), trainerId);
                
        return ResponseEntity.ok(sessionId);
    }
    
    // Update an existing session (FR5)
    @PutMapping("/{sessionId}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> updateSession(
            @PathVariable UUID sessionId, @RequestBody Map<String, Object> request) {
        
        LocalDate date = LocalDate.parse((String) request.get("date"));
        LocalTime startTime = LocalTime.parse((String) request.get("startTime"));
        LocalTime endTime = LocalTime.parse((String) request.get("endTime"));
        int capacity = (Integer) request.get("capacity");
        UUID trainerId = UUID.fromString((String) request.get("trainerId"));
        
        boolean updated = gymSessionService.updateSession(
                sessionId, date, startTime, endTime, capacity, trainerId);
                
        return ResponseEntity.ok(updated);
    }
    
    // Cancel a session (FR5)
    @PostMapping("/{sessionId}/cancel")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> cancelSession(
            @PathVariable UUID sessionId, @RequestBody Map<String, Object> request) {
        
        String reason = (String) request.get("reason");
        UUID trainerId = UUID.fromString((String) request.get("trainerId"));
        
        boolean cancelled = gymSessionService.cancelSession(sessionId, reason, trainerId);
        return ResponseEntity.ok(cancelled);
    }
    
    // Get registered users for a session (FR5)
    @GetMapping("/{sessionId}/users")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getRegisteredUsers(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(gymSessionService.getRegisteredUsersForSession(sessionId));
    }
    
    // Get reservations for a session (FR5)
    @GetMapping("/{sessionId}/reservations")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getSessionReservations(
            @PathVariable UUID sessionId,
            @RequestParam(required = false) ReservationStatus status) {
        
        return ResponseEntity.ok(
            gymReservationService.getSessionReservations(sessionId, Optional.ofNullable(status)));
    }
    
    // Mark attendance (FR5)
    @PostMapping("/{sessionId}/attendance")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> markAttendance(
            @PathVariable UUID sessionId, @RequestBody Map<String, Object> request) {
        
        UUID userId = UUID.fromString((String) request.get("userId"));
        boolean attended = (Boolean) request.get("attended");
        UUID trainerId = UUID.fromString((String) request.get("trainerId"));
        
        boolean recorded = gymSessionService.markAttendance(sessionId, userId, attended, trainerId);
        return ResponseEntity.ok(recorded);
    }
    
    // Get waitlist for a session (FR5)
    @GetMapping("/{sessionId}/waitlist")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getSessionWaitlist(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(gymReservationService.getWaitlistForSession(sessionId));
    }
    
    // Get occupancy statistics (FR5)
    @GetMapping("/stats/occupancy")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOccupancyStats(
            @RequestParam UUID trainerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(
            gymSessionService.getSessionOccupancyStats(trainerId, Optional.ofNullable(startDate), Optional.ofNullable(endDate)));
    }
    
    // Get attendance statistics for a specific session (FR5)
    @GetMapping("/{sessionId}/stats/attendance")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAttendanceStats(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(gymSessionService.getAttendanceStatsForSession(sessionId));
    }
}