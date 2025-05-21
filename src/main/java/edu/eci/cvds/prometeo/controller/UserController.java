package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.model.enums.ReportFormat;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.service.*;
import edu.eci.cvds.prometeo.dto.*;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private GymReservationService gymReservationService;

    // TODO: Move this logic to userservice layer
    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Autowired
    private BaseExerciseService baseExerciseService;

    @Autowired
    private GoalService goalService;

    @Autowired
    private ReportService reportService;

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
    
    @PostMapping("/create")
    @Operation(summary = "Create user from JWT", description = "Creates a new user using data from the JWT token")
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "User already exists")
    public ResponseEntity<User> createUser(HttpServletRequest request) {
        try {
            String institutionalId = (String) request.getAttribute("institutionalId");
            String username = (String) request.getAttribute("username");
            String name = (String) request.getAttribute("name");
            String role = (String) request.getAttribute("role");
    
            // Log extracted attributes
            System.out.println("🔍 Extracted attributes:");
            System.out.println("institutionalId = " + institutionalId);
            System.out.println("username = " + username);
            System.out.println("name = " + name);
            System.out.println("role = " + role);
    
            // Validate attributes
            if (institutionalId == null || name == null || role == null) {
                System.out.println("❌ Missing required attributes");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
    
            // Check if user already exists
            if (userService.userExistsByInstitutionalId(institutionalId)) {
                System.out.println("⚠️ User with institutionalId " + institutionalId + " already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
    
            // Create user
            UserDTO userDTO = new UserDTO();
            userDTO.setInstitutionalId(institutionalId);
            userDTO.setName(name);
            userDTO.setRole(role);
    
            User createdUser = userService.createUser(userDTO);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    
        } catch (Exception e) {
            System.out.println("❌ Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
        progress.setTrainerObservations(progressDTO.getTrainerObservations());

        PhysicalProgress savedProgress = userService.recordPhysicalMeasurement(userId, progress);
        return new ResponseEntity<>(savedProgress, HttpStatus.CREATED);
    }

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
    // -----------------------------------------------------
    // Routine management endpoints
    // -----------------------------------------------------

    @GetMapping("/{userId}/routines")
    @Operation(summary = "Get user routines", description = "Retrieves all routines assigned to a user")
    @ApiResponse(responseCode = "200", description = "Routines retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<Routine>> getUserRoutines(
            @Parameter(description = "User ID") @PathVariable UUID userId) {

        List<Routine> routines = userService.getUserRoutines(userId);
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/{userId}/routines/current")
    @Operation(summary = "Get current routine", description = "Retrieves the user's current active routine")
    @ApiResponse(responseCode = "200", description = "Routine retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No active routine found")
    public ResponseEntity<Routine> getCurrentRoutine(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        // TODO: Move this logic to userservice layer
        return routineRepository.findCurrentRoutineByUserId(userId)
                .map(routine -> ResponseEntity.ok(routine))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/routines/assign/{routineId}")
    @Operation(summary = "Assign routine to user", description = "Assigns an existing routine to a user")
    @ApiResponse(responseCode = "204", description = "Routine assigned successfully")
    @ApiResponse(responseCode = "404", description = "User or routine not found")
    public ResponseEntity<Void> assignRoutineToUser(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Routine ID") @PathVariable UUID routineId) {

        userService.assignRoutineToUser(userId, routineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/routines/custom")
    @Operation(summary = "Create custom routine", description = "Creates a custom routine for a user")
    @ApiResponse(responseCode = "201", description = "Routine created successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Routine> createCustomRoutine(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Routine data") @RequestBody RoutineDTO routineDTO) {

        // Convertir DTO a entidad
        Routine routine = new Routine();
        routine.setName(routineDTO.getName());
        routine.setDescription(routineDTO.getDescription());
        routine.setDifficulty(routineDTO.getDifficulty());
        routine.setGoal(routineDTO.getGoal());
        routine.setCreationDate(LocalDate.now());

        // Crear una lista vacía de ejercicios desde el principio
        routine.setExercises(new ArrayList<>());

        // Crear primero la rutina con la lista vacía
        Routine createdRoutine = userService.createCustomRoutine(userId, routine);

        // Ahora que la rutina tiene un ID, añadir los ejercicios uno por uno
        if (routineDTO.getExercises() != null && !routineDTO.getExercises().isEmpty()) {
            // Usar un enfoque de servicio para añadir cada ejercicio individualmente
            for (RoutineExerciseDTO exerciseDTO : routineDTO.getExercises()) {
                RoutineExercise exercise = new RoutineExercise();
                exercise.setBaseExerciseId(exerciseDTO.getBaseExerciseId());
                exercise.setRoutineId(createdRoutine.getId());
                exercise.setSets(exerciseDTO.getSets());
                exercise.setRepetitions(exerciseDTO.getRepetitions());
                exercise.setRestTime(exerciseDTO.getRestTime());
                exercise.setSequenceOrder(exerciseDTO.getSequenceOrder());

                // Añadir a la base de datos directamente sin pasar por la colección de la
                // rutina
                routineExerciseRepository.save(exercise);
            }
        }

        // Recargar la rutina para obtener todos los ejercicios asociados
        return new ResponseEntity<>(
                routineRepository.findById(createdRoutine.getId())
                        .orElseThrow(() -> new RuntimeException("Failed to find newly created routine")),
                HttpStatus.CREATED);
    }

    @PutMapping("/routines/{routineId}")
    @Operation(summary = "Update routine", description = "Updates an existing routine")
    @ApiResponse(responseCode = "200", description = "Routine updated successfully")
    @ApiResponse(responseCode = "404", description = "Routine not found")
    public ResponseEntity<Routine> updateRoutine(
            @Parameter(description = "Routine ID") @PathVariable UUID routineId,
            @Parameter(description = "Updated routine data") @RequestBody RoutineDTO routineDTO) {
        // TODO: Move this logic to userservice layer
        // Buscar la rutina existente
        Routine existingRoutine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));

        // Actualizar campos
        existingRoutine.setName(routineDTO.getName());
        existingRoutine.setDescription(routineDTO.getDescription());
        existingRoutine.setDifficulty(routineDTO.getDifficulty());
        existingRoutine.setGoal(routineDTO.getGoal());

        // Actualizar la rutina
        Routine updatedRoutine = userService.updateRoutine(routineId, existingRoutine);
        return ResponseEntity.ok(updatedRoutine);
    }

    @PostMapping("/{userId}/routines/{routineId}/progress")
    @Operation(summary = "Log routine progress", description = "Records progress for a routine session")
    @ApiResponse(responseCode = "204", description = "Progress logged successfully")
    @ApiResponse(responseCode = "404", description = "User or routine not found")
    public ResponseEntity<Void> logRoutineProgress(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Routine ID") @PathVariable UUID routineId,
            @Parameter(description = "Progress percentage") @RequestBody Map<String, Integer> progressData) {

        Integer completedPercentage = progressData.get("completed");
        if (completedPercentage == null) {
            completedPercentage = 100; // Valor por defecto si no se proporciona
        }

        userService.logRoutineProgress(userId, routineId, completedPercentage);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/recommended-routines")
    @Operation(summary = "Get recommended routines", description = "Retrieves personalized routine recommendations for a user")
    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<Routine>> getRecommendedRoutines(
            @Parameter(description = "User ID") @PathVariable UUID userId) {

        List<Routine> recommendations = userService.getRecommendedRoutines(userId);
        return ResponseEntity.ok(recommendations);
    }

    // -------------------------- Exercise crud ---------
    @GetMapping("/exercises")
    @Operation(summary = "Get all exercises", description = "Retrieves all base exercises in the system")
    @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
    public ResponseEntity<List<BaseExercise>> getAllExercises() {
        return ResponseEntity.ok(baseExerciseService.getAllExercises());
    }

    @GetMapping("/exercises/{id}")
    @Operation(summary = "Get exercise by ID", description = "Retrieves a specific exercise by its ID")
    @ApiResponse(responseCode = "200", description = "Exercise found")
    @ApiResponse(responseCode = "404", description = "Exercise not found")
    public ResponseEntity<BaseExercise> getExerciseById(
            @Parameter(description = "Exercise ID") @PathVariable UUID id) {

        return baseExerciseService.getExerciseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exercises/muscle-group/{muscleGroup}")
    @Operation(summary = "Get exercises by muscle group", description = "Retrieves exercises for a specific muscle group")
    @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully")
    public ResponseEntity<List<BaseExercise>> getExercisesByMuscleGroup(
            @Parameter(description = "Muscle group") @PathVariable String muscleGroup) {

        return ResponseEntity.ok(baseExerciseService.getExercisesByMuscleGroup(muscleGroup));
    }

    @GetMapping("/exercises/search")
    @Operation(summary = "Search exercises", description = "Searches exercises by name")
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<List<BaseExercise>> searchExercises(
            @Parameter(description = "Search term") @RequestParam String name) {

        return ResponseEntity.ok(baseExerciseService.searchExercisesByName(name));
    }

    @PostMapping("/exercises")
    @Operation(summary = "Create exercise", description = "Creates a new base exercise")
    @ApiResponse(responseCode = "201", description = "Exercise created successfully")
    public ResponseEntity<BaseExercise> createExercise(
            @Parameter(description = "Exercise data") @RequestBody BaseExerciseDTO exerciseDTO) {

        BaseExercise createdExercise = baseExerciseService.createExercise(exerciseDTO);
        return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
    }

    @PutMapping("/exercises/{id}")
    @Operation(summary = "Update exercise", description = "Updates an existing exercise")
    @ApiResponse(responseCode = "200", description = "Exercise updated successfully")
    @ApiResponse(responseCode = "404", description = "Exercise not found")
    public ResponseEntity<BaseExercise> updateExercise(
            @Parameter(description = "Exercise ID") @PathVariable UUID id,
            @Parameter(description = "Updated exercise data") @RequestBody BaseExerciseDTO exerciseDTO) {

        try {
            BaseExercise updatedExercise = baseExerciseService.updateExercise(id, exerciseDTO);
            return ResponseEntity.ok(updatedExercise);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/exercises/{id}")
    @Operation(summary = "Delete exercise", description = "Deletes an exercise (soft delete)")
    @ApiResponse(responseCode = "204", description = "Exercise deleted successfully")
    @ApiResponse(responseCode = "404", description = "Exercise not found")
    public ResponseEntity<Void> deleteExercise(
            @Parameter(description = "Exercise ID") @PathVariable UUID id) {

        try {
            baseExerciseService.deleteExercise(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -----------------------------------------------------
    // Gym reservations endpoints
    // -----------------------------------------------------
    // TODO: implementar bien modulo, configurar endpoint para gestion de sesiones
    // gym.

    @GetMapping("/gym/availability")
    @Operation(summary = "Get gym availability", description = "Retrieves gym availability for a specific date")
    @ApiResponse(responseCode = "200", description = "Availability information retrieved successfully")
    public ResponseEntity<List<Object>> getGymAvailability(
            @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Object> availableSlots = userService.getAvailableTimeSlots(date);
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping("/gym/availability/time")
    @Operation(summary = "Check availability for specific time", description = "Checks gym availability for a specific date and time")
    @ApiResponse(responseCode = "200", description = "Availability information retrieved successfully")
    public ResponseEntity<Map<String, Object>> checkAvailabilityForTime(
            @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Time to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        Map<String, Object> availability = gymReservationService.getAvailability(date, time);
        return ResponseEntity.ok(availability);
    }

    @PostMapping("/{userId}/reservations")
    @Operation(summary = "Create reservation", description = "Creates a new gym reservation")
    @ApiResponse(responseCode = "201", description = "Reservation created successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "400", description = "No available slots for the requested time")
    public ResponseEntity<Object> createReservation(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody ReservationDTO reservationDTO) {
        try {
            // Asegurarse de que el userId del path coincide con el del DTO
            reservationDTO.setUserId(userId);
            ReservationDTO created = gymReservationService.create(reservationDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("reservationId", created.getId());
            response.put("message", "Reserva creada exitosamente");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/reservations")
    @Operation(summary = "Get user reservations", description = "Retrieves all reservations for a user")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        List<ReservationDTO> reservations = gymReservationService.getByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{userId}/reservations/{reservationId}")
    @Operation(summary = "Get reservation details", description = "Retrieves details of a specific reservation")
    @ApiResponse(responseCode = "200", description = "Reservation details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<ReservationDTO> getReservationDetails(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Reservation ID") @PathVariable UUID reservationId) {

        Optional<ReservationDTO> reservation = gymReservationService.getById(reservationId);

        return reservation
                .filter(r -> r.getUserId().equals(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}/reservations/{reservationId}")
    @Operation(summary = "Cancel reservation", description = "Cancels an existing reservation")
    @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    @ApiResponse(responseCode = "403", description = "User not authorized to cancel this reservation")
    public ResponseEntity<Object> cancelReservation(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Reservation ID") @PathVariable UUID reservationId) {
        try {
            // Verifica primero si la reserva existe y pertenece al usuario
            Optional<ReservationDTO> reservation = gymReservationService.getById(reservationId);
            if (reservation.isEmpty() || !reservation.get().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            gymReservationService.delete(reservationId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Reserva cancelada exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/sessions/{sessionId}/waitlist")
    @Operation(summary = "Join waitlist", description = "Adds user to waitlist for a full session")
    @ApiResponse(responseCode = "200", description = "Added to waitlist successfully")
    @ApiResponse(responseCode = "404", description = "Session not found")
    public ResponseEntity<Object> joinWaitlist(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
        try {
            boolean added = gymReservationService.joinWaitlist(userId, sessionId);

            if (added) {
                Map<String, Object> status = gymReservationService.getWaitlistStatus(userId, sessionId);
                Map<String, Object> response = new HashMap<>();
                response.put("message",
                        "Has sido añadido a la lista de espera. Te notificaremos cuando haya cupo disponible.");
                response.put("status", status);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/sessions/{sessionId}/waitlist")
    @Operation(summary = "Get waitlist status", description = "Gets user's position in waitlist for a session")
    @ApiResponse(responseCode = "200", description = "Waitlist status retrieved successfully")
    public ResponseEntity<Map<String, Object>> getWaitlistStatus(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {

        Map<String, Object> status = gymReservationService.getWaitlistStatus(userId, sessionId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{userId}/waitlists")
    @Operation(summary = "Get all user waitlists", description = "Gets all sessions where user is in waitlist")
    @ApiResponse(responseCode = "200", description = "Waitlists retrieved successfully")
    public ResponseEntity<List<Map<String, Object>>> getUserWaitlists(
            @Parameter(description = "User ID") @PathVariable UUID userId) {

        List<Map<String, Object>> waitlists = gymReservationService.getUserWaitlists(userId);
        return ResponseEntity.ok(waitlists);
    }

    @DeleteMapping("/{userId}/sessions/{sessionId}/waitlist")
    @Operation(summary = "Leave waitlist", description = "Removes user from waitlist for a session")
    @ApiResponse(responseCode = "200", description = "Removed from waitlist successfully")
    @ApiResponse(responseCode = "404", description = "User not in waitlist or session not found")
    public ResponseEntity<Object> leaveWaitlist(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {

        boolean removed = gymReservationService.leaveWaitlist(userId, sessionId);

        if (removed) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Has sido removido de la lista de espera exitosamente");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // -----------------------------------------------------
    // Gym session management endpoints (trainers)
    // -----------------------------------------------------

    @Autowired
    private GymSessionService gymSessionService;

    @PostMapping("/trainer/sessions")
    @Operation(summary = "Create gym session", description = "Creates a new gym session for users to book")
    @ApiResponse(responseCode = "201", description = "Session created successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createSession(
            @RequestBody Map<String, Object> sessionData) {

        try {
            LocalDate date = LocalDate.parse((String) sessionData.get("date"));
            LocalTime startTime = LocalTime.parse((String) sessionData.get("startTime"));
            LocalTime endTime = LocalTime.parse((String) sessionData.get("endTime"));
            int capacity = (Integer) sessionData.get("capacity");
            UUID trainerId = UUID.fromString((String) sessionData.get("trainerId"));
            Optional<String> description = Optional.ofNullable((String) sessionData.get("description"));

            UUID sessionId = gymSessionService.createSession(
                    date, startTime, endTime, capacity, description, trainerId);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("message", "Sesión creada exitosamente");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/trainer/sessions/{sessionId}")
    @Operation(summary = "Update gym session", description = "Updates an existing gym session")
    @ApiResponse(responseCode = "200", description = "Session updated successfully")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Object> updateSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @RequestBody Map<String, Object> sessionData) {

        try {
            LocalDate date = LocalDate.parse((String) sessionData.get("date"));
            LocalTime startTime = LocalTime.parse((String) sessionData.get("startTime"));
            LocalTime endTime = LocalTime.parse((String) sessionData.get("endTime"));
            int capacity = (Integer) sessionData.get("capacity");
            UUID trainerId = UUID.fromString((String) sessionData.get("trainerId"));

            boolean updated = gymSessionService.updateSession(
                    sessionId, date, startTime, endTime, capacity, trainerId);

            if (updated) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Sesión actualizada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/trainer/sessions/{sessionId}")
    @Operation(summary = "Cancel gym session", description = "Cancels an existing gym session")
    @ApiResponse(responseCode = "200", description = "Session cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Session not found")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Object> cancelSession(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId,
            @RequestBody(required = false) Map<String, String> requestBody) {

        try {
            String reason = (requestBody != null) ? requestBody.get("reason") : null;
            UUID trainerId = UUID.fromString(requestBody != null ? requestBody.get("trainerId") : "");

            boolean cancelled = gymSessionService.cancelSession(sessionId, reason, trainerId);

            if (cancelled) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Sesión cancelada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/trainer/sessions")
    @Operation(summary = "Get sessions by date", description = "Retrieves all gym sessions for a specific date")
    @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getSessionsByDate(
            @Parameter(description = "Date to check") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Object> sessions = gymSessionService.getSessionsByDate(date);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/trainer/{trainerId}/sessions")
    @Operation(summary = "Get trainer's sessions", description = "Retrieves all sessions created by a specific trainer")
    @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getTrainerSessions(
            @Parameter(description = "Trainer ID") @PathVariable UUID trainerId) {

        List<Object> sessions = gymSessionService.getSessionsByTrainer(trainerId);
        System.out.println("🔍 Accessing /trainer/{trainerId}/sessions endpoint");
        System.out.println("🔍 Trainer ID: " + trainerId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/trainer/sessions/recurring")
    @Operation(summary = "Create recurring sessions", description = "Creates recurring gym sessions on specified days")
    @ApiResponse(responseCode = "201", description = "Recurring sessions created successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createRecurringSessions(
            @RequestBody Map<String, Object> recurringData) {

        try {
            int dayOfWeek = (Integer) recurringData.get("dayOfWeek"); // 1=Monday, 7=Sunday
            LocalTime startTime = LocalTime.parse((String) recurringData.get("startTime"));
            LocalTime endTime = LocalTime.parse((String) recurringData.get("endTime"));
            int capacity = (Integer) recurringData.get("capacity");
            LocalDate startDate = LocalDate.parse((String) recurringData.get("startDate"));
            LocalDate endDate = LocalDate.parse((String) recurringData.get("endDate"));
            UUID trainerId = UUID.fromString((String) recurringData.get("trainerId"));
            Optional<String> description = Optional.ofNullable((String) recurringData.get("description"));

            int sessionsCreated = gymSessionService.configureRecurringSessions(
                    dayOfWeek, startTime, endTime, capacity, description, trainerId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionsCreated", sessionsCreated);
            response.put("message", "Sesiones recurrentes creadas exitosamente");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/trainer/sessions/stats")
    @Operation(summary = "Get occupancy statistics", description = "Retrieves occupancy statistics for gym sessions")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<LocalDate, Integer>> getOccupancyStatistics(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<LocalDate, Integer> statistics = gymSessionService.getOccupancyStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    // -----------------------------------------------------
    // Slot and schedule management by trainers
    // -----------------------------------------------------
    @GetMapping("/trainer/sessions/{sessionId}/students")
    @Operation(summary = "Get registered students", description = "Retrieves all students registered for a specific session")
    @ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getRegisteredStudents(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {

        List<Map<String, Object>> students = gymSessionService.getRegisteredStudentsForSession(sessionId);
        return ResponseEntity.ok(students);
    }

    @PostMapping("/trainer/attendance")
    @Operation(summary = "Record student attendance", description = "Records attendance for a student at a gym session")
    @ApiResponse(responseCode = "200", description = "Attendance recorded successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> recordStudentAttendance(
            @RequestBody Map<String, Object> attendanceData) {

        UUID userId = UUID.fromString((String) attendanceData.get("userId"));
        UUID reservationId = UUID.fromString((String) attendanceData.get("reservationId"));
        LocalDateTime attendanceTime = attendanceData.containsKey("attendanceTime")
                ? LocalDateTime.parse((String) attendanceData.get("attendanceTime"))
                : LocalDateTime.now();

        boolean success = userService.recordGymAttendance(userId, reservationId, attendanceTime);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Asistencia registrada correctamente" : "No se pudo registrar la asistencia");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/trainer/{trainerId}/attendance/stats")
    @Operation(summary = "Get attendance statistics", description = "Retrieves attendance statistics for a trainer's sessions")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAttendanceStatistics(
            @Parameter(description = "Trainer ID") @PathVariable UUID trainerId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> statistics = gymSessionService.getTrainerAttendanceStatistics(trainerId, startDate,
                endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/gym/sessions/{sessionId}")
    @Operation(summary = "Get session by ID", description = "Retrieves details of a specific gym session")
    @ApiResponse(responseCode = "200", description = "Session found")
    @ApiResponse(responseCode = "404", description = "Session not found")
    public ResponseEntity<Object> getSessionById(
            @Parameter(description = "Session ID") @PathVariable UUID sessionId) {

        try {
            Object session = gymSessionService.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

     // -----------------------------------------------------
     // Reports and analysis endpoints
     // -----------------------------------------------------

    @GetMapping("/user-progress")
    @Operation(
            summary = "Generate user progress report",
            description = "Returns a report with the user's physical progress over time (e.g., weight and goals).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report generated successfully",
                            content = @Content(mediaType = "application/octet-stream")),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<byte[]> getUserProgressReport(
            @Parameter(name = "userId", description = "UUID of the user", required = true, in = ParameterIn.QUERY)
            @RequestParam UUID userId,

            @Parameter(name = "format", description = "Report format: PDF, XLSX, CSV, JSON", required = true, in = ParameterIn.QUERY)
            @RequestParam ReportFormat format
    ) {
        byte[] report = reportService.generateUserProgressReport(userId, format);
        return buildResponse(report, format, "user_progress_report");
    }

    @GetMapping("/gym-usage")
    @Operation(
            summary = "Generate gym usage report",
            description = "Returns statistics about gym usage (reservations, capacity, duration) for a given date range.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report generated successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<byte[]> getGymUsageReport(
            @Parameter(name = "startDate", description = "Start date in yyyy-MM-dd format", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(name = "endDate", description = "End date in yyyy-MM-dd format", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(name = "format", description = "Report format: PDF, XLSX, CSV, JSON", required = true, in = ParameterIn.QUERY)
            @RequestParam ReportFormat format
    ) {
        byte[] report = reportService.generateGymUsageReport(startDate, endDate, format);
        return buildResponse(report, format, "gym_usage_report");
    }

    @GetMapping("/attendance")
    @Operation(
            summary = "Generate attendance report",
            description = "Returns daily attendance statistics for the gym within a date range.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report generated successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<byte[]> getAttendanceReport(
            @Parameter(name = "startDate", description = "Start date in yyyy-MM-dd format", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(name = "endDate", description = "End date in yyyy-MM-dd format", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(name = "format", description = "Report format: PDF, XLSX, CSV, JSON", required = true, in = ParameterIn.QUERY)
            @RequestParam ReportFormat format
    ) {
        byte[] report = reportService.getAttendanceStatistics(startDate, endDate, format);
        return buildResponse(report, format, "attendance_report");
    }

    /**
     * Builds an HTTP response with appropriate headers for file download,
     * based on the specified report format.
     *
     * <p>This method sets the correct <code>Content-Type</code> and
     * <code>Content-Disposition</code> headers to allow clients to download
     * the report in the requested format (PDF, XLSX, CSV, JSON).</p>
     *
     * @param content the byte array representing the report content
     * @param format the format of the report (PDF, XLSX, CSV, JSON)
     * @param filenameBase the base name for the file (without extension)
     * @return a ResponseEntity with the file content and appropriate headers
     */
    private ResponseEntity<byte[]> buildResponse(byte[] content, ReportFormat format, String filenameBase) {
        String contentType;
        String extension;

        switch (format) {
            case PDF -> {
                contentType = "application/pdf";
                extension = ".pdf";
            }
            case XLSX -> {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                extension = ".xlsx";
            }
            case CSV -> {
                contentType = "text/csv";
                extension = ".csv";
            }
            case JSON -> {
                contentType = "application/json";
                extension = ".json";
            }
            default -> {
                contentType = "application/octet-stream";
                extension = "";
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filenameBase + extension)
                .build());

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    // // ------------------------------------------------------
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
