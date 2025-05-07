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
 * REST Controller for managing user-related operations in the Prometeo
 * application.
 * 
 * This controller provides a comprehensive API for managing all user-related
 * functionality including:
 * - User profile management: Retrieving and updating user profiles
 * - Physical tracking: Recording and monitoring physical measurements and
 * progress
 * - Goals management: Creating, updating, and tracking fitness goals
 * - Routines: Assigning, creating, and tracking workout routines
 * - Reservations: Managing gym and equipment reservations
 * - Recommendations: Providing personalized routine and class recommendations
 * - Reports: Generating various user activity and progress reports
 * 
 * The controller includes endpoints for regular users as well as specialized
 * endpoints
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

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById(@Parameter(description = "User ID") @PathVariable String id) {
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
            @Parameter(description = "User ID") @PathVariable String id,
            @Parameter(description = "User data") @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user in the system")
    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = User.class)))
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
            @Parameter(description = "User ID") @PathVariable String InstitutionalId) {
        return ResponseEntity.ok(userService.deleteUser(InstitutionalId));
    }

    // // -----------------------------------------------------
    // // Physical tracking endpoints
    // // -----------------------------------------------------

    @PostMapping("/{userId}/physical-progress")

    @Operation(summary = "Record physical measurement", description = "Records a new physical measurement for a user")
    @ApiResponse(responseCode = "201", description = "Measurement recorded successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<PhysicalProgress> recordPhysicalMeasurement(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody PhysicalProgressDTO progressDTO) {

        // Convertir DTO a entidad
        PhysicalProgress progress = new PhysicalProgress();
        progress.setWeight(new Weight(progressDTO.getWeight().getValue(), Weight.WeightUnit.KG));

        // Crear BodyMeasurements
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(progressDTO.getMeasurements().getHeight());
        measurements.setChestCircumference(progressDTO.getMeasurements().getChestCircumference());
        measurements.setWaistCircumference(progressDTO.getMeasurements().getWaistCircumference());
        measurements.setHipCircumference(progressDTO.getMeasurements().getHipCircumference());
        measurements.setBicepsCircumference(progressDTO.getMeasurements().getBicepsCircumference());
        measurements.setThighCircumference(progressDTO.getMeasurements().getThighCircumference());

        progress.setMeasurements(measurements);
        progress.setPhysicalGoal(progressDTO.getPhysicalGoal());

        PhysicalProgress savedProgress = userService.recordPhysicalMeasurement(userId, progress);
        return new ResponseEntity<>(savedProgress, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/physical-progress")
    @Operation(summary = "Get physical measurement history", description = "Retrieves physical measurement history for a user")
    @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<PhysicalProgress>> getPhysicalMeasurementHistory(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<PhysicalProgress> history = userService.getPhysicalMeasurementHistory(
                userId,
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate));

        return ResponseEntity.ok(history);
    }

    @GetMapping("/{userId}/physical-progress/latest")
    @Operation(summary = "Get latest physical measurement", description = "Retrieves the most recent physical measurement for a user")
    @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No measurements found")
    public ResponseEntity<PhysicalProgress> getLatestPhysicalMeasurement(
            @Parameter(description = "User ID") @PathVariable UUID userId) {

        return userService.getLatestPhysicalMeasurement(userId)
                .map(progress -> ResponseEntity.ok(progress))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/physical-progress/{progressId}/measurements")
    @Operation(summary = "Update physical measurements", description = "Updates body measurements for an existing progress record")
    @ApiResponse(responseCode = "200", description = "Measurements updated successfully")
    @ApiResponse(responseCode = "404", description = "Progress record not found")
    public ResponseEntity<PhysicalProgress> updatePhysicalMeasurements(
            @Parameter(description = "Progress ID") @PathVariable UUID progressId,
            @RequestBody BodyMeasurementsDTO measurementsDTO) {

        // Convertir DTO a entidad
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(measurementsDTO.getHeight());
        measurements.setChestCircumference(measurementsDTO.getChestCircumference());
        measurements.setWaistCircumference(measurementsDTO.getWaistCircumference());
        measurements.setHipCircumference(measurementsDTO.getHipCircumference());
        measurements.setBicepsCircumference(measurementsDTO.getBicepsCircumference());
        measurements.setThighCircumference(measurementsDTO.getThighCircumference());

        PhysicalProgress updatedProgress = userService.updatePhysicalMeasurement(progressId, measurements);
        return ResponseEntity.ok(updatedProgress);
    }

    @PutMapping("/{userId}/physical-progress/goal")
    @Operation(summary = "Set physical goal", description = "Sets a physical goal for a user")
    @ApiResponse(responseCode = "200", description = "Goal set successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<PhysicalProgress> setPhysicalGoal(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody Map<String, String> body) {

        String goal = body.get("goal");
        PhysicalProgress updatedProgress = userService.setPhysicalGoal(userId, goal);
        return ResponseEntity.ok(updatedProgress);
    }

    @GetMapping("/{userId}/physical-progress/metrics")
    @Operation(summary = "Get progress metrics", description = "Calculates progress metrics over a specified period")
    @ApiResponse(responseCode = "200", description = "Metrics calculated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Map<String, Double>> getPhysicalProgressMetrics(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(defaultValue = "6") int months) {

        Map<String, Double> metrics = userService.calculatePhysicalProgressMetrics(userId, months);
        return ResponseEntity.ok(metrics);
    }

    // Para entrenadores
    @GetMapping("/trainer/{trainerId}/users/{userId}/physical-progress")
    @Operation(summary = "Get user's physical progress (for trainers)", description = "Allows trainers to view physical progress of their assigned users")
    @ApiResponse(responseCode = "200", description = "Progress retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User not assigned to this trainer")
    @ApiResponse(responseCode = "404", description = "User or trainer not found")
    public ResponseEntity<List<PhysicalProgress>> getTraineePhysicalProgress(
            @Parameter(description = "Trainer ID") @PathVariable UUID trainerId,
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Aquí deberías validar que el usuario está asignado al entrenador
        // Esta lógica debe implementarse en el servicio

        List<PhysicalProgress> history = userService.getPhysicalMeasurementHistory(
                userId,
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate));

        return ResponseEntity.ok(history);
    }
    // // -----------------------------------------------------
    // // Goals endpoints
    // // -----------------------------------------------------

    // @PostMapping("/{userId}/goals")
    // @Operation(summary = "Create goal", description = "Creates a new fitness goal
    // for a user")
    // @ApiResponse(responseCode = "201", description = "Goal created successfully")
    // public ResponseEntity<Goal> createGoal(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Goal data") @RequestBody GoalDTO goalDTO);

    // @GetMapping("/{userId}/goals")
    // @Operation(summary = "Get user goals", description = "Retrieves all goals for
    // a user")
    // @ApiResponse(responseCode = "200", description = "Goals retrieved
    // successfully")
    // public ResponseEntity<List<Goal>> getUserGoals(@Parameter(description = "User
    // ID") @PathVariable Long userId);

    // @PutMapping("/{userId}/goals/{goalId}")
    // @Operation(summary = "Update goal", description = "Updates an existing goal")
    // @ApiResponse(responseCode = "200", description = "Goal updated successfully")
    // public ResponseEntity<Goal> updateGoal(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Goal ID") @PathVariable Long goalId,
    // @Parameter(description = "Updated goal data") @RequestBody GoalDTO goalDTO);

    // @DeleteMapping("/{userId}/goals/{goalId}")
    // @Operation(summary = "Delete goal", description = "Deletes a goal")
    // @ApiResponse(responseCode = "200", description = "Goal deleted successfully")
    // public ResponseEntity<Void> deleteGoal(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Goal ID") @PathVariable Long goalId);

    // @GetMapping("/{userId}/goals/progress")
    // @Operation(summary = "Get goals progress", description = "Retrieves progress
    // for all user goals")
    // @ApiResponse(responseCode = "200", description = "Progress retrieved
    // successfully")
    // public ResponseEntity<List<GoalProgressDTO>>
    // getUserGoalsProgress(@Parameter(description = "User ID") @PathVariable Long
    // userId);

    // // -----------------------------------------------------
    // // Routines endpoints
    // // -----------------------------------------------------

    // @GetMapping("/{userId}/routines")
    // @Operation(summary = "Get user routines", description = "Retrieves all
    // routines for a user")
    // public ResponseEntity<List<Routine>> getUserRoutines(@Parameter(description =
    // "User ID") @PathVariable Long userId);

    // @GetMapping("/{userId}/routines/current")
    // @Operation(summary = "Get current routine", description = "Retrieves the
    // user's current active routine")
    // public ResponseEntity<Routine> getCurrentRoutine(@Parameter(description =
    // "User ID") @PathVariable Long userId);

    // @PostMapping("/{userId}/routines/assign/{routineId}")
    // @Operation(summary = "Assign routine to user", description = "Assigns an
    // existing routine to a user")
    // public ResponseEntity<Void> assignRoutineToUser(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Routine ID") @PathVariable Long routineId);

    // @GetMapping("/routines/public")
    // @Operation(summary = "Get public routines", description = "Retrieves publicly
    // available routines with optional filters")
    // public ResponseEntity<List<Routine>> getPublicRoutines(
    // @Parameter(description = "Category filter") @RequestParam(required = false)
    // String category,
    // @Parameter(description = "Difficulty filter") @RequestParam(required = false)
    // String difficulty);

    // @PostMapping("/{userId}/routines/custom")
    // @Operation(summary = "Create custom routine", description = "Creates a custom
    // routine for a user")
    // @PreAuthorize("hasRole('TRAINER') or
    // @securityService.isResourceOwner(#userId)")
    // public ResponseEntity<Routine> createCustomRoutine(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Routine data") @RequestBody RoutineDTO routineDTO);

    // @PutMapping("/{userId}/routines/{routineId}")
    // @Operation(summary = "Update routine", description = "Updates an existing
    // routine")
    // @PreAuthorize("hasRole('TRAINER') or
    // @securityService.isResourceOwner(#userId)")
    // public ResponseEntity<Routine> updateRoutine(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Routine ID") @PathVariable Long routineId,
    // @Parameter(description = "Updated routine data") @RequestBody RoutineDTO
    // routineDTO);

    // @GetMapping("/routines/{routineId}/details")
    // @Operation(summary = "Get routine details", description = "Retrieves detailed
    // information about a routine")
    // public ResponseEntity<RoutineDetailDTO>
    // getRoutineDetails(@Parameter(description = "Routine ID") @PathVariable Long
    // routineId);

    // @PostMapping("/{userId}/routines/progress")
    // @Operation(summary = "Log routine progress", description = "Records progress
    // for a routine session")
    // public ResponseEntity<RoutineProgress> logRoutineProgress(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Progress data") @RequestBody RoutineProgressDTO
    // progressDTO);

    // // -----------------------------------------------------
    // // Gym reservations endpoints
    // // -----------------------------------------------------

    // @GetMapping("/gym/availability")
    // @Operation(summary = "Get gym availability", description = "Retrieves gym
    // availability for a specific date")
    // public ResponseEntity<Map<String, Integer>> getGymAvailability(
    // @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso =
    // DateTimeFormat.ISO.DATE) LocalDate date);

    // @PostMapping("/{userId}/reservations")
    // @Operation(summary = "Create reservation", description = "Creates a new gym
    // reservation")
    // public ResponseEntity<Reservation> createReservation(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Reservation data") @RequestBody ReservationDTO
    // reservationDTO);

    // @GetMapping("/{userId}/reservations")
    // @Operation(summary = "Get user reservations", description = "Retrieves all
    // reservations for a user")
    // public ResponseEntity<List<Reservation>>
    // getUserReservations(@Parameter(description = "User ID") @PathVariable Long
    // userId);

    // @DeleteMapping("/{userId}/reservations/{reservationId}")
    // @Operation(summary = "Cancel reservation", description = "Cancels an existing
    // reservation")
    // public ResponseEntity<Void> cancelReservation(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Reservation ID") @PathVariable Long reservationId);

    // @GetMapping("/{userId}/reservations/upcoming")
    // @Operation(summary = "Get upcoming reservations", description = "Retrieves
    // upcoming reservations for a user")
    // public ResponseEntity<List<Reservation>>
    // getUpcomingReservations(@Parameter(description = "User ID") @PathVariable
    // Long userId);

    // @GetMapping("/{userId}/reservations/history")
    // @Operation(summary = "Get reservation history", description = "Retrieves
    // historical reservations for a user")
    // public ResponseEntity<List<Reservation>> getReservationHistory(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Start date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    // @Parameter(description = "End date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    // // -----------------------------------------------------
    // // Equipment reservations endpoints
    // // -----------------------------------------------------

    // @GetMapping("/gym/equipment")
    // @Operation(summary = "Get available equipment", description = "Retrieves
    // available equipment for a specific time")
    // public ResponseEntity<List<Equipment>> getAvailableEquipment(
    // @Parameter(description = "Date and time to check") @RequestParam
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime);

    // @PostMapping("/{userId}/reservations/{reservationId}/equipment")
    // @Operation(summary = "Reserve equipment", description = "Reserves equipment
    // for a gym session")
    // public ResponseEntity<EquipmentReservation> reserveEquipment(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Reservation ID") @PathVariable Long reservationId,
    // @Parameter(description = "Equipment reservation data") @RequestBody
    // EquipmentReservationDTO equipmentDTO);

    // @DeleteMapping("/{userId}/reservations/{reservationId}/equipment/{equipmentReservationId}")
    // @Operation(summary = "Cancel equipment reservation", description = "Cancels
    // an equipment reservation")
    // public ResponseEntity<Void> cancelEquipmentReservation(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Reservation ID") @PathVariable Long reservationId,
    // @Parameter(description = "Equipment reservation ID") @PathVariable Long
    // equipmentReservationId);

    // // -----------------------------------------------------
    // // Recommendations endpoints
    // // -----------------------------------------------------

    // @GetMapping("/{userId}/recommended-routines")
    // @Operation(summary = "Get recommended routines", description = "Retrieves
    // personalized routine recommendations for a user")
    // public ResponseEntity<List<Routine>>
    // getRecommendedRoutines(@Parameter(description = "User ID") @PathVariable Long
    // userId);

    // @GetMapping("/{userId}/recommended-classes")
    // @Operation(summary = "Get recommended classes", description = "Retrieves
    // personalized class recommendations for a user")
    // public ResponseEntity<List<ClassRecommendationDTO>>
    // getRecommendedClasses(@Parameter(description = "User ID") @PathVariable Long
    // userId);

    // // -----------------------------------------------------
    // // Reports and analysis endpoints
    // // -----------------------------------------------------

    // @GetMapping("/{userId}/reports/attendance")
    // @Operation(summary = "Get attendance report", description = "Generates an
    // attendance report for a user")
    // public ResponseEntity<AttendanceReportDTO> getUserAttendanceReport(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Start date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    // @Parameter(description = "End date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    // @GetMapping("/{userId}/reports/physical-evolution")
    // @Operation(summary = "Get physical evolution report", description =
    // "Generates a physical evolution report for a user")
    // public ResponseEntity<PhysicalEvolutionReportDTO>
    // getUserPhysicalEvolutionReport(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Start date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    // @Parameter(description = "End date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    // @GetMapping("/{userId}/reports/routine-compliance")
    // @Operation(summary = "Get routine compliance report", description =
    // "Generates a routine compliance report for a user")
    // public ResponseEntity<RoutineComplianceReportDTO>
    // getUserRoutineComplianceReport(
    // @Parameter(description = "User ID") @PathVariable Long userId,
    // @Parameter(description = "Start date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    // @Parameter(description = "End date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    // // -----------------------------------------------------
    // // Admin/Trainer specific endpoints
    // // -----------------------------------------------------

    // @PostMapping("/gym/capacity")
    // @Operation(summary = "Configure gym capacity", description = "Sets capacity
    // limits for the gym")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<Void> configureGymCapacity(@Parameter(description =
    // "Capacity configuration") @RequestBody GymCapacityDTO capacityDTO);

    // @PostMapping("/gym/block-timeslot")
    // @Operation(summary = "Block gym timeslot", description = "Blocks a timeslot
    // from being reserved")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<Void> blockGymTimeslot(@Parameter(description = "Block
    // configuration") @RequestBody BlockTimeslotDTO blockDTO);

    // @GetMapping("/admin/gym/usage-stats")
    // @Operation(summary = "Get gym usage statistics", description = "Retrieves
    // statistics about gym usage")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    // public ResponseEntity<GymUsageStatsDTO> getGymUsageStatistics(
    // @Parameter(description = "Start date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    // @Parameter(description = "End date") @RequestParam(required = false)
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    // @GetMapping("/trainer/assigned-users")
    // @Operation(summary = "Get trainer's assigned users", description = "Retrieves
    // users assigned to the current trainer")
    // @PreAuthorize("hasRole('TRAINER')")
    // public ResponseEntity<List<UserProfileDTO>> getTrainerAssignedUsers();

    // @PostMapping("/trainer/{trainerId}/assign-user/{userId}")
    // @Operation(summary = "Assign user to trainer", description = "Assigns a user
    // to a specific trainer")
    // @PreAuthorize("hasRole('ADMIN') or
    // @securityService.isResourceOwner(#trainerId)")
    // public ResponseEntity<Void> assignUserToTrainer(
    // @Parameter(description = "Trainer ID") @PathVariable Long trainerId,
    // @Parameter(description = "User ID") @PathVariable Long userId);
}
