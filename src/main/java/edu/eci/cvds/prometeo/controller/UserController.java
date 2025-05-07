package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.service.*;
import edu.eci.cvds.prometeo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for managing user-related operations in the Prometeo application.
 * 
 * This controller provides a comprehensive API for managing all user-related functionality including:
 * - User profile management: Retrieving and updating user profiles
 * - Physical tracking: Recording and monitoring physical measurements and progress
 * - Goals management: Creating, updating, and tracking fitness goals
 * - Routines: Assigning, creating, and tracking workout routines
 * - Reservations: Managing gym and equipment reservations
 * - Recommendations: Providing personalized routine and class recommendations
 * - Reports: Generating various user activity and progress reports
 * 
 * The controller includes endpoints for regular users as well as specialized endpoints
 * for trainers and administrators with appropriate authorization checks.
 * 
 * All endpoints follow RESTful design principles and include comprehensive
 * OpenAPI documentation for API consumers.
 * 
 * @see UserService
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Controller", description = "API for managing user profiles, physical tracking, goals, routines, and reservations")
public class UserController {
    
    @Autowired
    private UserService userService;

    // -----------------------------------------------------
    // User profile endpoints
    // -----------------------------------------------------
    
    // @GetMapping("/{id}")
    // @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    // @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class)))
    // @ApiResponse(responseCode = "404", description = "User not found")
    // public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable Long id);

    // @GetMapping("/profile/{id}")
    // @Operation(summary = "Get user profile", description = "Retrieves a user's profile information")
    // @ApiResponse(responseCode = "200", description = "Profile found", content = @Content(schema = @Schema(implementation = UserProfileDTO.class)))
    // @ApiResponse(responseCode = "404", description = "Profile not found")
    // public ResponseEntity<UserProfileDTO> getUserProfile(@Parameter(description = "User ID") @PathVariable Long id);

    // @PutMapping("/{id}/profile")
    // @Operation(summary = "Update user profile", description = "Updates a user's profile information")
    // @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    // @ApiResponse(responseCode = "404", description = "User not found")
    // public ResponseEntity<UserProfileDTO> updateUserProfile(
    //         @Parameter(description = "User ID") @PathVariable Long id,
    //         @Parameter(description = "Profile data") @RequestBody UserProfileUpdateDTO profileDTO);

    // // -----------------------------------------------------
    // // Physical tracking endpoints
    // // -----------------------------------------------------
    
    // @PostMapping("/{userId}/physical-records")
    // @Operation(summary = "Create physical record", description = "Creates a new physical measurement record for a user")
    // @ApiResponse(responseCode = "201", description = "Record created successfully")
    // @ApiResponse(responseCode = "404", description = "User not found")
    // public ResponseEntity<PhysicalRecord> createPhysicalRecord(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Physical record data") @RequestBody PhysicalRecordDTO recordDTO);

    // @GetMapping("/{userId}/physical-records")
    // @Operation(summary = "Get user physical records", description = "Retrieves all physical records for a user")
    // @ApiResponse(responseCode = "200", description = "Records retrieved successfully")
    // public ResponseEntity<List<PhysicalRecord>> getUserPhysicalRecords(@Parameter(description = "User ID") @PathVariable Long userId);

    // @GetMapping("/{userId}/physical-records/latest")
    // @Operation(summary = "Get latest physical record", description = "Retrieves the most recent physical record for a user")
    // @ApiResponse(responseCode = "200", description = "Latest record retrieved")
    // @ApiResponse(responseCode = "404", description = "No records found")
    // public ResponseEntity<PhysicalRecord> getLatestPhysicalRecord(@Parameter(description = "User ID") @PathVariable Long userId);

    // @GetMapping("/{userId}/physical-records/progress")
    // @Operation(summary = "Get physical progress", description = "Generates a progress report between two dates")
    // public ResponseEntity<ProgressReportDTO> getPhysicalProgress(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    // @PostMapping("/{userId}/physical-records/{recordId}/photos")
    // @Operation(summary = "Upload progress photo", description = "Uploads a photo associated with a physical record")
    // @ApiResponse(responseCode = "201", description = "Photo uploaded successfully")
    // public ResponseEntity<PhysicalRecordPhoto> uploadProgressPhoto(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Record ID") @PathVariable Long recordId,
    //         @Parameter(description = "Photo file") @RequestParam("file") MultipartFile file,
    //         @Parameter(description = "Photo type") @RequestParam("type") String photoType);
    
    // @PostMapping("/{userId}/medical-notes")
    // @Operation(summary = "Add medical note", description = "Adds a medical note to a user's profile")
    // @ApiResponse(responseCode = "201", description = "Note added successfully")
    // public ResponseEntity<MedicalNote> addMedicalNote(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Medical note data") @RequestBody MedicalNoteDTO noteDTO);
    
    // @GetMapping("/{userId}/medical-notes")
    // @Operation(summary = "Get user medical notes", description = "Retrieves all medical notes for a user")
    // @ApiResponse(responseCode = "200", description = "Notes retrieved successfully")
    // public ResponseEntity<List<MedicalNote>> getUserMedicalNotes(@Parameter(description = "User ID") @PathVariable Long userId);

    // // -----------------------------------------------------
    // // Goals endpoints
    // // -----------------------------------------------------
    
    // @PostMapping("/{userId}/goals")
    // @Operation(summary = "Create goal", description = "Creates a new fitness goal for a user")
    // @ApiResponse(responseCode = "201", description = "Goal created successfully")
    // public ResponseEntity<Goal> createGoal(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Goal data") @RequestBody GoalDTO goalDTO);

    // @GetMapping("/{userId}/goals")
    // @Operation(summary = "Get user goals", description = "Retrieves all goals for a user")
    // @ApiResponse(responseCode = "200", description = "Goals retrieved successfully")
    // public ResponseEntity<List<Goal>> getUserGoals(@Parameter(description = "User ID") @PathVariable Long userId);

    // @PutMapping("/{userId}/goals/{goalId}")
    // @Operation(summary = "Update goal", description = "Updates an existing goal")
    // @ApiResponse(responseCode = "200", description = "Goal updated successfully")
    // public ResponseEntity<Goal> updateGoal(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Goal ID") @PathVariable Long goalId,
    //         @Parameter(description = "Updated goal data") @RequestBody GoalDTO goalDTO);
    
    // @DeleteMapping("/{userId}/goals/{goalId}")
    // @Operation(summary = "Delete goal", description = "Deletes a goal")
    // @ApiResponse(responseCode = "200", description = "Goal deleted successfully")
    // public ResponseEntity<Void> deleteGoal(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Goal ID") @PathVariable Long goalId);
    
    // @GetMapping("/{userId}/goals/progress")
    // @Operation(summary = "Get goals progress", description = "Retrieves progress for all user goals")
    // @ApiResponse(responseCode = "200", description = "Progress retrieved successfully")
    // public ResponseEntity<List<GoalProgressDTO>> getUserGoalsProgress(@Parameter(description = "User ID") @PathVariable Long userId);

    // // -----------------------------------------------------
    // // Routines endpoints
    // // -----------------------------------------------------
    
    // @GetMapping("/{userId}/routines")
    // @Operation(summary = "Get user routines", description = "Retrieves all routines for a user")
    // public ResponseEntity<List<Routine>> getUserRoutines(@Parameter(description = "User ID") @PathVariable Long userId);

    // @GetMapping("/{userId}/routines/current")
    // @Operation(summary = "Get current routine", description = "Retrieves the user's current active routine")
    // public ResponseEntity<Routine> getCurrentRoutine(@Parameter(description = "User ID") @PathVariable Long userId);

    // @PostMapping("/{userId}/routines/assign/{routineId}")
    // @Operation(summary = "Assign routine to user", description = "Assigns an existing routine to a user")
    // public ResponseEntity<Void> assignRoutineToUser(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Routine ID") @PathVariable Long routineId);

    // @GetMapping("/routines/public")
    // @Operation(summary = "Get public routines", description = "Retrieves publicly available routines with optional filters")
    // public ResponseEntity<List<Routine>> getPublicRoutines(
    //         @Parameter(description = "Category filter") @RequestParam(required = false) String category,
    //         @Parameter(description = "Difficulty filter") @RequestParam(required = false) String difficulty);
    
    // @PostMapping("/{userId}/routines/custom")
    // @Operation(summary = "Create custom routine", description = "Creates a custom routine for a user")
    // @PreAuthorize("hasRole('TRAINER') or @securityService.isResourceOwner(#userId)")
    // public ResponseEntity<Routine> createCustomRoutine(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Routine data") @RequestBody RoutineDTO routineDTO);
    
    // @PutMapping("/{userId}/routines/{routineId}")
    // @Operation(summary = "Update routine", description = "Updates an existing routine")
    // @PreAuthorize("hasRole('TRAINER') or @securityService.isResourceOwner(#userId)")
    // public ResponseEntity<Routine> updateRoutine(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Routine ID") @PathVariable Long routineId,
    //         @Parameter(description = "Updated routine data") @RequestBody RoutineDTO routineDTO);
    
    // @GetMapping("/routines/{routineId}/details")
    // @Operation(summary = "Get routine details", description = "Retrieves detailed information about a routine")
    // public ResponseEntity<RoutineDetailDTO> getRoutineDetails(@Parameter(description = "Routine ID") @PathVariable Long routineId);
    
    // @PostMapping("/{userId}/routines/progress")
    // @Operation(summary = "Log routine progress", description = "Records progress for a routine session")
    // public ResponseEntity<RoutineProgress> logRoutineProgress(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Progress data") @RequestBody RoutineProgressDTO progressDTO);

    // // -----------------------------------------------------
    // -----------------------------------------------------
    // Gym reservations endpoints - FR4
    // -----------------------------------------------------
    
    @GetMapping("/gym/availability")
    @Operation(summary = "Get gym availability", description = "Retrieves gym session availability for a specific date")
    @ApiResponse(responseCode = "200", description = "Availability retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No availability found")
    public ResponseEntity<List<GymSessionDTO>> getGymAvailability(
            @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<GymSessionDTO> availableSessions = userService.getAvailableTimeSlots(date);
        return ResponseEntity.ok(availableSessions);
    }

    @PostMapping("/{userId}/reservations")
    @Operation(summary = "Create gym session reservation", description = "Creates a new gym session reservation")
    @ApiResponse(responseCode = "201", description = "Reservation created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reservation data")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Reservation conflict")
    
    public ResponseEntity<UUID> createReservation(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody ReservationRequestDTO reservationDTO) {
        
        UUID reservationId = userService.createGymReservation(
            userId,
            reservationDTO.getDate(),
            reservationDTO.getStartTime(),
            reservationDTO.getEndTime(),
            Optional.ofNullable(reservationDTO.getEquipmentIds())
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationId);
    }

    @DeleteMapping("/{userId}/reservations/{reservationId}")
    @Operation(summary = "Cancel reservation", description = "Cancels an existing gym reservation")
    @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid cancellation data")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    @ApiResponse(responseCode = "409", description = "Reservation cannot be cancelled")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Boolean> cancelReservation(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Reservation ID") @PathVariable UUID reservationId,
            @RequestBody(required = false) CancellationRequestDTO cancellationDTO) {
        
        Optional<String> reason = cancellationDTO != null ? 
                                  Optional.ofNullable(cancellationDTO.getReason()) : 
                                  Optional.empty();
                                  
        boolean cancelled = userService.cancelGymReservation(reservationId, userId, reason);
        return ResponseEntity.ok(cancelled);
    }
    
    @GetMapping("/{userId}/reservations/upcoming")
    @Operation(summary = "Get upcoming reservations", description = "Retrieves upcoming gym reservations for a user")
    @ApiResponse(responseCode = "200", description = "Upcoming reservations retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<ReservationDTO>> getUpcomingReservations(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        
        List<ReservationDTO> reservations = userService.getUpcomingReservations(userId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/{userId}/reservations/history")
    @Operation(summary = "Get reservation history", description = "Retrieves historical gym reservations for a user")
    @ApiResponse(responseCode = "200", description = "Reservation history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<ReservationDTO>> getReservationHistory(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<ReservationDTO> history = userService.getReservationHistory(
            userId, 
            Optional.ofNullable(startDate), 
            Optional.ofNullable(endDate)
        );
        
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/{userId}/waitlist/{sessionId}")
    @Operation(summary = "Join waitlist", description = "Adds a user to the waitlist for a full session")
    @ApiResponse(responseCode = "200", description = "User added to waitlist successfully")
    @ApiResponse(responseCode = "400", description = "Invalid waitlist data")
    @ApiResponse(responseCode = "404", description = "User or session not found")
    @ApiResponse(responseCode = "409", description = "User already on waitlist")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Boolean> joinWaitlist(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        
        boolean added = userService.joinWaitlist(userId, sessionId);
        return ResponseEntity.ok(added);
    }
    
    // -----------------------------------------------------
    // Trainer-specific gym management endpoints - FR5
    // -----------------------------------------------------
    
    @PostMapping("/trainer/sessions")
    @Operation(summary = "Create gym session", description = "Creates a new gym session with specified capacity")
    @ApiResponse(responseCode = "201", description = "Session created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid session data")
    @ApiResponse(responseCode = "404", description = "Trainer not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<UUID> createGymSession(@RequestBody SessionRequestDTO sessionDTO) {
        UUID sessionId = userService.createGymSession(
            sessionDTO.getDate(),
            sessionDTO.getStartTime(),
            sessionDTO.getEndTime(),
            sessionDTO.getCapacity(),
            Optional.ofNullable(sessionDTO.getDescription()),
            sessionDTO.getTrainerId()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionId);
    }
    
    @PutMapping("/trainer/sessions/{sessionId}")
    @Operation(summary = "Update gym session", description = "Updates an existing gym session details")
    @ApiResponse(responseCode = "200", description = "Session updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid session data")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> updateGymSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @RequestBody SessionRequestDTO sessionDTO) {
        
        boolean updated = userService.updateGymSession(
            sessionId,
            sessionDTO.getDate(),
            sessionDTO.getStartTime(),
            sessionDTO.getEndTime(),
            sessionDTO.getCapacity(),
            sessionDTO.getTrainerId()
        );
        
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/trainer/sessions/{sessionId}")
    @Operation(summary = "Cancel gym session", description = "Cancels a scheduled gym session")
    @ApiResponse(responseCode = "200", description = "Session cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid cancellation data")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @ApiResponse(responseCode = "409", description = "Session cannot be cancelled")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> cancelGymSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @RequestBody CancellationRequestDTO cancellationDTO) {
        
        boolean cancelled = userService.cancelGymSession(
            sessionId,
            cancellationDTO.getReason(),
            cancellationDTO.getTrainerId()
        );
        
        return ResponseEntity.ok(cancelled);
    }
    
    @GetMapping("/trainer/{trainerId}/sessions")
    @Operation(summary = "Get trainer's sessions", description = "Retrieves all gym sessions created by a trainer")
    @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Trainer not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<GymSession>> getTrainerSessions(
            @Parameter(description = "Trainer ID") @PathVariable UUID trainerId) {
        
        List<GymSession> sessions = userService.getSessionsByTrainer(trainerId);
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/trainer/sessions/{sessionId}/users")
    @Operation(summary = "Get session attendees", description = "Retrieves all users registered for a specific gym session")
    @ApiResponse(responseCode = "200", description = "Attendees retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getSessionAttendees(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        
        List<User> attendees = userService.getRegisteredUsersForSession(sessionId);
        return ResponseEntity.ok(attendees);
    }
    
    @PostMapping("/trainer/attendance/{reservationId}")
    @Operation(summary = "Record attendance", description = "Records user attendance for a gym session")
    @ApiResponse(responseCode = "200", description = "Attendance recorded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid attendance data")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    @ApiResponse(responseCode = "409", description = "Attendance already recorded")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> recordAttendance(
            @Parameter(description = "Reservation ID") @PathVariable UUID reservationId,
            @RequestBody AttendanceRequestDTO attendanceDTO) {
        
        boolean recorded = userService.recordGymAttendance(
            reservationId,
            attendanceDTO.isAttended(),
            attendanceDTO.getTrainerId()
        );
        
        return ResponseEntity.ok(recorded);
    }
    
    @GetMapping("/trainer/{trainerId}/stats")
    @Operation(summary = "Get session statistics", description = "Retrieves statistics about gym session occupancy")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Trainer not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSessionStats(
            @Parameter(description = "Trainer ID") @PathVariable UUID trainerId,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> stats = userService.getSessionOccupancyStats(
            trainerId,
            Optional.ofNullable(startDate),
            Optional.ofNullable(endDate)
        );
        
        return ResponseEntity.ok(stats);
    }

    // // -----------------------------------------------------
    // // Equipment reservations endpoints
    // // -----------------------------------------------------
    
    // @GetMapping("/gym/equipment")
    // @Operation(summary = "Get available equipment", description = "Retrieves available equipment for a specific time")
    // public ResponseEntity<List<Equipment>> getAvailableEquipment(
    //         @Parameter(description = "Date and time to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime);

    // @PostMapping("/{userId}/reservations/{reservationId}/equipment")
    // @Operation(summary = "Reserve equipment", description = "Reserves equipment for a gym session")
    // public ResponseEntity<EquipmentReservation> reserveEquipment(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Reservation ID") @PathVariable Long reservationId,
    //         @Parameter(description = "Equipment reservation data") @RequestBody EquipmentReservationDTO equipmentDTO);
    
    // @DeleteMapping("/{userId}/reservations/{reservationId}/equipment/{equipmentReservationId}")
    // @Operation(summary = "Cancel equipment reservation", description = "Cancels an equipment reservation")
    // public ResponseEntity<Void> cancelEquipmentReservation(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Reservation ID") @PathVariable Long reservationId,
    //         @Parameter(description = "Equipment reservation ID") @PathVariable Long equipmentReservationId);

    // // -----------------------------------------------------
    // // Recommendations endpoints
    // // -----------------------------------------------------
    
    // @GetMapping("/{userId}/recommended-routines")
    // @Operation(summary = "Get recommended routines", description = "Retrieves personalized routine recommendations for a user")
    // public ResponseEntity<List<Routine>> getRecommendedRoutines(@Parameter(description = "User ID") @PathVariable Long userId);
    
    // @GetMapping("/{userId}/recommended-classes")
    // @Operation(summary = "Get recommended classes", description = "Retrieves personalized class recommendations for a user")
    // public ResponseEntity<List<ClassRecommendationDTO>> getRecommendedClasses(@Parameter(description = "User ID") @PathVariable Long userId);

    // // -----------------------------------------------------
    // // Reports and analysis endpoints
    // // -----------------------------------------------------
    
    // @GetMapping("/{userId}/reports/attendance")
    // @Operation(summary = "Get attendance report", description = "Generates an attendance report for a user")
    // public ResponseEntity<AttendanceReportDTO> getUserAttendanceReport(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    // @GetMapping("/{userId}/reports/physical-evolution")
    // @Operation(summary = "Get physical evolution report", description = "Generates a physical evolution report for a user")
    // public ResponseEntity<PhysicalEvolutionReportDTO> getUserPhysicalEvolutionReport(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    // @GetMapping("/{userId}/reports/routine-compliance")
    // @Operation(summary = "Get routine compliance report", description = "Generates a routine compliance report for a user")
    // public ResponseEntity<RoutineComplianceReportDTO> getUserRoutineComplianceReport(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    // // -----------------------------------------------------
    // // Admin/Trainer specific endpoints
    // // -----------------------------------------------------
    
    // @PostMapping("/gym/capacity")
    // @Operation(summary = "Configure gym capacity", description = "Sets capacity limits for the gym")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<Void> configureGymCapacity(@Parameter(description = "Capacity configuration") @RequestBody GymCapacityDTO capacityDTO);
    
    // @PostMapping("/gym/block-timeslot")
    // @Operation(summary = "Block gym timeslot", description = "Blocks a timeslot from being reserved")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<Void> blockGymTimeslot(@Parameter(description = "Block configuration") @RequestBody BlockTimeslotDTO blockDTO);
    
    // @GetMapping("/admin/gym/usage-stats")
    // @Operation(summary = "Get gym usage statistics", description = "Retrieves statistics about gym usage")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<GymUsageStatsDTO> getGymUsageStatistics(
    //         @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    // @GetMapping("/trainer/assigned-users")
    // @Operation(summary = "Get trainer's assigned users", description = "Retrieves users assigned to the current trainer")
    // @PreAuthorize("hasRole('TRAINER')")
    // public ResponseEntity<List<UserProfileDTO>> getTrainerAssignedUsers();
    
    // @PostMapping("/trainer/{trainerId}/assign-user/{userId}")
    // @Operation(summary = "Assign user to trainer", description = "Assigns a user to a specific trainer")
    // @PreAuthorize("hasRole('ADMIN') or @securityService.isResourceOwner(#trainerId)")
    // public ResponseEntity<Void> assignUserToTrainer(
    //         @Parameter(description = "Trainer ID") @PathVariable Long trainerId,
    //         @Parameter(description = "User ID") @PathVariable Long userId);
}
