package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.service.GymReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/gym/reservations")
public class GymReservationController {
    
    private final GymReservationService gymReservationService;
    
    @Autowired
    public GymReservationController(GymReservationService gymReservationService) {
        this.gymReservationService = gymReservationService;
    }
    
    // Make a reservation (FR4)
    @PostMapping
    @Operation(summary = "Make a reservation")
    @ApiResponse(responseCode = "200", description = "Reservation made successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Session or equipment not found")
    @ApiResponse(responseCode = "409", description = "Session already reserved")
    @ApiResponse(responseCode = "500", description = "Internal server error")

    public ResponseEntity<UUID> makeReservation(@RequestBody Map<String, Object> request) {
        UUID userId = UUID.fromString((String) request.get("userId"));
        UUID sessionId = UUID.fromString((String) request.get("sessionId"));
        
        List<UUID> equipmentIds = null;
        if (request.get("equipmentIds") != null) {
            equipmentIds = ((List<String>) request.get("equipmentIds")).stream()
                .map(UUID::fromString)
                .toList();
        }
        
        UUID reservationId = gymReservationService.makeReservation(
                userId, sessionId, Optional.ofNullable(equipmentIds));
                
        return ResponseEntity.ok(reservationId);
    }
    
    // Cancel a reservation (FR4)
    @PostMapping("/{reservationId}/cancel")
    @Operation(summary = "Cancel a reservation")
    @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    @ApiResponse(responseCode = "409", description = "Reservation cannot be cancelled")
    @ApiResponse(responseCode = "500", description = "Internal server error")

    public ResponseEntity<Boolean> cancelReservation(
            @PathVariable UUID reservationId, @RequestBody Map<String, Object> request) {
        
        UUID userId = UUID.fromString((String) request.get("userId"));
        String reason = request.get("reason") != null ? (String) request.get("reason") : null;
        
        boolean cancelled = gymReservationService.cancelReservation(
                reservationId, userId, Optional.ofNullable(reason));
                
        return ResponseEntity.ok(cancelled);
    }
    
    // Get upcoming reservations for user (FR4)
    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming reservations for user")
    @ApiResponse(responseCode = "200", description = "Upcoming reservations retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<ReservationDTO>> getUpcomingReservations(@RequestParam UUID userId) {
        return ResponseEntity.ok(gymReservationService.getUpcomingReservations(userId));
    }
    
    // Get reservation history (FR4)
    @GetMapping("/history")
    @Operation(summary = "Get reservation history for user")
    @ApiResponse(responseCode = "200", description = "Reservation history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<ReservationDTO>> getReservationHistory(
            @RequestParam UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return ResponseEntity.ok(
            gymReservationService.getReservationHistory(userId, Optional.ofNullable(startDate), Optional.ofNullable(endDate)));
    }
    
    // Get reservation by ID
    @GetMapping("/{reservationId}")
    @Operation(summary = "Get reservation by ID")
    @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable UUID reservationId) {
        return gymReservationService.getReservationById(reservationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Check session availability (FR4)
    @GetMapping("/check-availability")
    @Operation(summary = "Check session availability")
    @ApiResponse(responseCode = "200", description = "Session availability checked successfully")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Boolean> checkAvailability(@RequestParam UUID sessionId) {
        return ResponseEntity.ok(gymReservationService.checkSessionAvailability(sessionId));
    }
    
    // Add to waitlist (FR4)
    @PostMapping("/waitlist")
    @Operation(summary = "Add to waitlist")
    @ApiResponse(responseCode = "200", description = "Added to waitlist successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @ApiResponse(responseCode = "409", description = "Already on waitlist")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Boolean> addToWaitlist(@RequestBody Map<String, Object> request) {
        UUID userId = UUID.fromString((String) request.get("userId"));
        UUID sessionId = UUID.fromString((String) request.get("sessionId"));
        
        boolean added = gymReservationService.addToWaitlist(userId, sessionId);
        return ResponseEntity.ok(added);
    }
}