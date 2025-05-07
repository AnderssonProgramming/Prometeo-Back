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

    @Autowired
    private GoalService goalService;

    // -----------------------------------------------------
    // User profile endpoints
    // -----------------------------------------------------
    
@GetMapping("/{id}")
@Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
@ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class)))
@ApiResponse(responseCode = "404", description = "User not found")
public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable UUID id) {
    return ResponseEntity.ok(userService.getUserById(id));
}

@GetMapping("/by-institutional-id/{institutionalId}")
@Operation(summary = "Get user by institutional ID", description = "Retrieves a user by their institutional identifier")
@ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class)))
@ApiResponse(responseCode = "404", description = "User not found")
public ResponseEntity<User> getUserByInstitutionalId(
        @Parameter(description = "Institutional ID") @PathVariable String institutionalId) {
    return ResponseEntity.ok(userService.getUserByInstitutionalId(institutionalId));
}

@GetMapping
@Operation(summary = "Get all users", description = "Retrieves all users in the system")
@ApiResponse(responseCode = "200", description = "Users retrieved successfully")
public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
}

@GetMapping("/by-role/{role}")
@Operation(summary = "Get users by role", description = "Retrieves all users with a specific role")
@ApiResponse(responseCode = "200", description = "Users retrieved successfully")
public ResponseEntity<List<User>> getUsersByRole(
        @Parameter(description = "Role name") @PathVariable String role) {
    return ResponseEntity.ok(userService.getUsersByRole(role));
}

@PutMapping("/{id}")
@Operation(summary = "Update user", description = "Updates a user's basic information")
@ApiResponse(responseCode = "200", description = "User updated successfully")
@ApiResponse(responseCode = "404", description = "User not found")
public ResponseEntity<User> updateUser(
        @Parameter(description = "User ID") @PathVariable UUID id,
        @Parameter(description = "User data") @RequestBody UserDTO userDTO) {
    return ResponseEntity.ok(userService.updateUser(id, userDTO));
}

@PostMapping
@Operation(summary = "Create user", description = "Creates a new user in the system")
@ApiResponse(responseCode = "201", description = "User created successfully", 
    content = @Content(schema = @Schema(implementation = User.class)))
public ResponseEntity<User> createUser(
        @Parameter(description = "User data") @RequestBody UserDTO userDTO) {
    User createdUser = userService.createUser(userDTO);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
}

@DeleteMapping("/{id}")
@Operation(summary = "Delete user", description = "Deletes a user from the system")
@ApiResponse(responseCode = "200", description = "User deleted successfully")
@ApiResponse(responseCode = "404", description = "User not found")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<User> deleteUser(
        @Parameter(description = "User ID") @PathVariable UUID id) {
    return ResponseEntity.ok(userService.deleteUser(id));
}

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

    @PostMapping("/{userId}/goals")
    @Operation(summary = "Create goal", description = "Creates a new fitness goal for a user")
    @ApiResponse(responseCode = "201", description = "Goal created successfully")
    public ResponseEntity<String> createGoal(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Goal data") @RequestBody List<String> goals) {
        try {
            goalService.addUserGoal(userId, goals);
            return ResponseEntity.ok("Goals updated and recommendations refreshed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/goals")
    @Operation(summary = "Get user goals", description = "Retrieves all goals for a user")
    @ApiResponse(responseCode = "200", description = "Goals retrieved successfully")
    public ResponseEntity<List<Goal>> getUserGoals(@Parameter(description = "User ID") @PathVariable UUID userId) {
        List<Goal> goals = goalService.getGoalsByUser(userId);
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{userId}/goals/{goalId}")
    @Operation(summary = "Update goal", description = "Updates an existing goal")
    @ApiResponse(responseCode = "200", description = "Goal updated successfully")
    public ResponseEntity<String> updateGoal(
            @Parameter(description = "Map of Goal IDs and updated text") @RequestBody Map<UUID, String> updatedGoals) {
        try {
            goalService.updateUserGoal(updatedGoals);
            return ResponseEntity.ok("Goal updated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/goals/{goalId}")
    @Operation(summary = "Delete goal", description = "Deletes a goal")
    @ApiResponse(responseCode = "200", description = "Goal deleted successfully")
    public ResponseEntity<String> deleteGoal(
            @Parameter(description = "Goal ID") @PathVariable UUID goalId) {
        try {
            goalService.deleteGoal(goalId);
            return ResponseEntity.ok("Goal deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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
    // // Gym reservations endpoints
    // // -----------------------------------------------------
    
    // @GetMapping("/gym/availability")
    // @Operation(summary = "Get gym availability", description = "Retrieves gym availability for a specific date")
    // public ResponseEntity<Map<String, Integer>> getGymAvailability(
    //         @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    // @PostMapping("/{userId}/reservations")
    // @Operation(summary = "Create reservation", description = "Creates a new gym reservation")
    // public ResponseEntity<Reservation> createReservation(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Reservation data") @RequestBody ReservationDTO reservationDTO);

    // @GetMapping("/{userId}/reservations")
    // @Operation(summary = "Get user reservations", description = "Retrieves all reservations for a user")
    // public ResponseEntity<List<Reservation>> getUserReservations(@Parameter(description = "User ID") @PathVariable Long userId);

    // @DeleteMapping("/{userId}/reservations/{reservationId}")
    // @Operation(summary = "Cancel reservation", description = "Cancels an existing reservation")
    // public ResponseEntity<Void> cancelReservation(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Reservation ID") @PathVariable Long reservationId);
    
    // @GetMapping("/{userId}/reservations/upcoming")
    // @Operation(summary = "Get upcoming reservations", description = "Retrieves upcoming reservations for a user")
    // public ResponseEntity<List<Reservation>> getUpcomingReservations(@Parameter(description = "User ID") @PathVariable Long userId);
    
    // @GetMapping("/{userId}/reservations/history")
    // @Operation(summary = "Get reservation history", description = "Retrieves historical reservations for a user")
    // public ResponseEntity<List<Reservation>> getReservationHistory(
    //         @Parameter(description = "User ID") @PathVariable Long userId,
    //         @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

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
