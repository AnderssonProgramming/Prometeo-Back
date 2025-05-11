package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.service.*;
import edu.eci.cvds.prometeo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Autowired
    private BaseExerciseService baseExerciseService;

    // -----------------------------------------------------
    // User profile endpoints
    // -----------------------------------------------------

    @GetMapping
    @Operation(summary = "Get users with optional filters", description = "Retrieves users by ID, institutional ID, or role. If no filters are provided, returns all users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUsers(
            @Parameter(description = "User ID") @RequestParam(required = false) String id,
            @Parameter(description = "Institutional ID") @RequestParam(required = false) String institutionalId,
            @Parameter(description = "Role name") @RequestParam(required = false) String role) {

        if (id != null) {
            try {
                User user = userService.getUserById(id);
                return ResponseEntity.ok(user);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
            }
        }

        if (institutionalId != null) {
            try {
                User user = userService.getUserByInstitutionalId(institutionalId);
                return ResponseEntity.ok(user);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with institutional ID: " + institutionalId);
            }
        }

        if (role != null) {
            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        }

        return ResponseEntity.ok(userService.getAllUsers());
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
        progress.setTrainerObservations(progressDTO.getTrainerObservations());

        PhysicalProgress savedProgress = userService.recordPhysicalMeasurement(userId, progress);
        return new ResponseEntity<>(savedProgress, HttpStatus.CREATED);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{userId}/physical-progress")
    @Operation(summary = "Get user physical progress", description = "Retrieves physical measurement history, latest measurement, or metrics. Also supports trainer view.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Data not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden for trainer access")
    })
    public ResponseEntity<?> getUserPhysicalProgress(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Trainer ID (if accessing as trainer)") @RequestParam(required = false) UUID trainerId,
            @Parameter(description = "Start date for historical range") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for historical range") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "If true, only return the latest measurement") @RequestParam(required = false, defaultValue = "false") boolean latest,
            @Parameter(description = "If true, return metrics instead of raw data") @RequestParam(required = false, defaultValue = "false") boolean metrics,
            @Parameter(description = "Number of months to calculate metrics over") @RequestParam(required = false, defaultValue = "6") int months) {

        if (latest) {
            return userService.getLatestPhysicalMeasurement(userId)
                    .map(ResponseEntity::ok)
                    .orElseGet((Supplier<? extends ResponseEntity<PhysicalProgress>>) ResponseEntity.status(HttpStatus.NOT_FOUND).body("No measurements found for user"));
        }

        if (metrics) {
            Map<String, Double> progressMetrics = userService.calculatePhysicalProgressMetrics(userId, months);
            return ResponseEntity.ok(progressMetrics);
        }

        // If trainerId is provided, apply trainer-access validation (assumed handled in the service)
        if (trainerId != null) {
            // Validate trainer-user relationship inside service
            List<PhysicalProgress> progressList = userService.getTrainerViewOfUserProgress(
                    trainerId,
                    userId,
                    Optional.ofNullable(startDate),
                    Optional.ofNullable(endDate));
            return ResponseEntity.ok(progressList);
        }

        // Default: get full history for user
        List<PhysicalProgress> history = userService.getPhysicalMeasurementHistory(
                userId,
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate));

        return ResponseEntity.ok(history);
    }


   @PutMapping("/physical-progress")
    @Operation(summary = "Update physical progress", description = "Updates physical measurements or sets a goal based on parameters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    public ResponseEntity<PhysicalProgress> updatePhysicalProgress(
            @RequestParam(required = false) UUID progressId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "false") boolean goal,
            @RequestBody Map<String, Object> body) {

        if (goal) {
            if (userId == null || !body.containsKey("goal")) {
                return ResponseEntity.badRequest().build();
            }
            String goalValue = body.get("goal").toString();
            PhysicalProgress updatedProgress = userService.setPhysicalGoal(userId, goalValue);
            return ResponseEntity.ok(updatedProgress);
        }

        if (progressId == null) {
            return ResponseEntity.badRequest().build();
        }

        // Convert body into BodyMeasurementsDTO manually
        BodyMeasurementsDTO dto = new BodyMeasurementsDTO();
        dto.setHeight((Double) body.getOrDefault("height", 0.0));
        dto.setChestCircumference((Double) body.getOrDefault("chestCircumference", 0.0));
        dto.setWaistCircumference((Double) body.getOrDefault("waistCircumference", 0.0));
        dto.setHipCircumference((Double) body.getOrDefault("hipCircumference", 0.0));
        dto.setBicepsCircumference((Double) body.getOrDefault("bicepsCircumference", 0.0));
        dto.setThighCircumference((Double) body.getOrDefault("thighCircumference", 0.0));

        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(dto.getHeight());
        measurements.setChestCircumference(dto.getChestCircumference());
        measurements.setWaistCircumference(dto.getWaistCircumference());
        measurements.setHipCircumference(dto.getHipCircumference());
        measurements.setBicepsCircumference(dto.getBicepsCircumference());
        measurements.setThighCircumference(dto.getThighCircumference());

        PhysicalProgress updatedProgress = userService.updatePhysicalMeasurement(progressId, measurements);
        return ResponseEntity.ok(updatedProgress);
    }




    // -----------------------------------------------------
    // Routine management endpoints
    // -----------------------------------------------------

    @GetMapping("/{userId}/routines")
    @Operation(
        summary = "Get user routines with optional filters",
        description = "Retrieves all routines, the current routine, or recommended routines for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Routines retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Routine(s) not found")
    })
    public ResponseEntity<?> getUserRoutinesFiltered(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "false") boolean current,
            @RequestParam(required = false, defaultValue = "false") boolean recommended) {

        if (current) {
            return routineRepository.findCurrentRoutineByUserId(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (recommended) {
            List<Routine> recommendedRoutines = userService.getRecommendedRoutines(userId);
            return ResponseEntity.ok(recommendedRoutines);
        }

        List<Routine> routines = userService.getUserRoutines(userId);
        return ResponseEntity.ok(routines);
    }


    @PostMapping("/{userId}/routines")
    @Operation(
        summary = "Routine operations: assign, create custom, or log progress",
        description = "Assigns a routine, creates a custom routine, or logs progress for a routine session"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Custom routine created successfully"),
        @ApiResponse(responseCode = "204", description = "Routine assigned or progress logged successfully"),
        @ApiResponse(responseCode = "404", description = "User or routine not found")
    })
    public ResponseEntity<?> handleRoutineActions(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false) UUID routineId,
            @RequestParam(required = false) String action,
            @RequestBody(required = false) Map<String, Object> body) {

        if ("assign".equalsIgnoreCase(action) && routineId != null) {
            userService.assignRoutineToUser(userId, routineId);
            return ResponseEntity.noContent().build();
        }

        if ("log".equalsIgnoreCase(action) && routineId != null) {
            Integer completed = Optional.ofNullable((Integer) body.get("completed")).orElse(100);
            userService.logRoutineProgress(userId, routineId, completed);
            return ResponseEntity.noContent().build();
        }

        if ("create".equalsIgnoreCase(action)) {
            RoutineDTO routineDTO = new ObjectMapper().convertValue(body, RoutineDTO.class);

            Routine routine = new Routine();
            routine.setName(routineDTO.getName());
            routine.setDescription(routineDTO.getDescription());
            routine.setDifficulty(routineDTO.getDifficulty());
            routine.setGoal(routineDTO.getGoal());
            routine.setCreationDate(LocalDate.now());
            routine.setExercises(new ArrayList<>());

            Routine createdRoutine = userService.createCustomRoutine(userId, routine);

            if (routineDTO.getExercises() != null && !routineDTO.getExercises().isEmpty()) {
                for (RoutineExerciseDTO exerciseDTO : routineDTO.getExercises()) {
                    RoutineExercise exercise = new RoutineExercise();
                    exercise.setBaseExerciseId(exerciseDTO.getBaseExerciseId());
                    exercise.setRoutineId(createdRoutine.getId());
                    exercise.setSets(exerciseDTO.getSets());
                    exercise.setRepetitions(exerciseDTO.getRepetitions());
                    exercise.setRestTime(exerciseDTO.getRestTime());
                    exercise.setSequenceOrder(exerciseDTO.getSequenceOrder());
                    routineExerciseRepository.save(exercise);
                }
            }

            Routine completeRoutine = routineRepository.findById(createdRoutine.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to find newly created routine"));
            return new ResponseEntity<>(completeRoutine, HttpStatus.CREATED);
        }

        return ResponseEntity.badRequest().body("Invalid action or missing parameters.");
    }



    @PutMapping("/routines/{routineId}")
    @Operation(summary = "Update routine", description = "Updates an existing routine")
    @ApiResponse(responseCode = "200", description = "Routine updated successfully")
    @ApiResponse(responseCode = "404", description = "Routine not found")
    public ResponseEntity<Routine> updateRoutine(
            @Parameter(description = "Routine ID") @PathVariable UUID routineId,
            @Parameter(description = "Updated routine data") @RequestBody RoutineDTO routineDTO) {
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


    // --------------------------  Exercise crud ---------
    @GetMapping("/exercises")
    @Operation(
        summary = "Retrieve exercises",
        description = "Gets all exercises, or filters by ID, muscle group, or search term"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    public ResponseEntity<?> getExercises(
            @Parameter(description = "Exercise ID") @RequestParam(required = false) UUID id,
            @Parameter(description = "Muscle group") @RequestParam(required = false) String muscleGroup,
            @Parameter(description = "Search by name") @RequestParam(required = false) String name) {

        if (id != null) {
            return baseExerciseService.getExerciseById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (muscleGroup != null) {
            return ResponseEntity.ok(baseExerciseService.getExercisesByMuscleGroup(muscleGroup));
        }

        if (name != null) {
            return ResponseEntity.ok(baseExerciseService.searchExercisesByName(name));
        }

        return ResponseEntity.ok(baseExerciseService.getAllExercises());
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
    // TODO: implementar bien modulo, configurar endpoint para gestion de sesiones gym.

    @GetMapping("/gym/availability")
    @Operation(summary = "Get gym availability", description = "Retrieves gym availability for a specific date or date-time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability information retrieved successfully")
    })
    public ResponseEntity<?> getGymAvailability(
            @Parameter(description = "Date to check")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Time to check", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        if (time != null) {
            Map<String, Object> availability = gymReservationService.getAvailability(date, time);
            return ResponseEntity.ok(availability);
        }

        List<Object> availableSlots = userService.getAvailableTimeSlots(date);
        return ResponseEntity.ok(availableSlots);
    }


    @GetMapping("/{userId}/reservations")
    @Operation(summary = "User reservations", description = "Retrieves all reservations or details of a specific one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<?> getUserReservations(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Reservation ID", required = false) @RequestParam(required = false) UUID reservationId) {

        if (reservationId != null) {
            Optional<ReservationDTO> reservation = gymReservationService.getById(reservationId);
            return reservation
                    .filter(r -> r.getUserId().equals(userId))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        List<ReservationDTO> reservations = gymReservationService.getByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{userId}/waitlist")
    @Operation(summary = "Waitlist status", description = "Gets either a specific waitlist status or all user's waitlists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waitlist information retrieved successfully")
    })
    public ResponseEntity<?> getWaitlistStatus(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Session ID", required = false) @RequestParam(required = false) UUID sessionId) {

        if (sessionId != null) {
            Map<String, Object> status = gymReservationService.getWaitlistStatus(userId, sessionId);
            return ResponseEntity.ok(status);
        }

        List<Map<String, Object>> waitlists = gymReservationService.getUserWaitlists(userId);
        return ResponseEntity.ok(waitlists);
    }


    @PostMapping("/{userId}/reservations")
    @Operation(summary = "Create reservation or join waitlist", description = "Creates a new gym reservation or adds user to waitlist if session is full")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "200", description = "User added to waitlist successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or no available slots"),
        @ApiResponse(responseCode = "404", description = "User or session not found")
    })
    public ResponseEntity<Object> reserveOrWaitlist(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false) UUID sessionId,
            @RequestBody(required = false) ReservationDTO reservationDTO) {

        try {
            // Caso: Lista de espera (sin ReservationDTO, solo sessionId)
            if (sessionId != null && reservationDTO == null) {
                boolean added = gymReservationService.joinWaitlist(userId, sessionId);
                if (added) {
                    Map<String, Object> status = gymReservationService.getWaitlistStatus(userId, sessionId);
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Has sido añadido a la lista de espera. Te notificaremos cuando haya cupo disponible.");
                    response.put("status", status);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("error", "No se pudo unir a la lista de espera."));
                }
            }

            // Caso: Creación de reserva
            if (reservationDTO != null) {
                reservationDTO.setUserId(userId);
                ReservationDTO created = gymReservationService.create(reservationDTO);
                Map<String, Object> response = new HashMap<>();
                response.put("reservationId", created.getId());
                response.put("message", "Reserva creada exitosamente");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }

            // Ningún caso válido
            return ResponseEntity.badRequest().body(Map.of("error", "Solicitud inválida. Se requiere reservationDTO o sessionId."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{userId}/reservations")
    @Operation(
        summary = "Cancel reservation or leave waitlist",
        description = "Cancels a reservation or removes user from waitlist, based on provided parameters"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled or removed from waitlist successfully"),
        @ApiResponse(responseCode = "403", description = "User not authorized to cancel this reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation or waitlist entry not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<Object> cancelOrLeaveWaitlist(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestParam(required = false) UUID reservationId,
            @RequestParam(required = false) UUID sessionId) {

        try {
            // Cancelación de reserva
            if (reservationId != null && sessionId == null) {
                Optional<ReservationDTO> reservation = gymReservationService.getById(reservationId);
                if (reservation.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Reserva no encontrada"));
                }
                if (!reservation.get().getUserId().equals(userId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No autorizado para cancelar esta reserva"));
                }

                gymReservationService.delete(reservationId);
                return ResponseEntity.ok(Map.of("message", "Reserva cancelada exitosamente"));
            }

            // Salida de lista de espera
            if (sessionId != null && reservationId == null) {
                boolean removed = gymReservationService.leaveWaitlist(userId, sessionId);
                if (removed) {
                    return ResponseEntity.ok(Map.of("message", "Has sido removido de la lista de espera exitosamente"));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No estás en la lista de espera o la sesión no existe"));
                }
            }

            // Ningún caso válido
            return ResponseEntity.badRequest().body(Map.of("error", "Solicitud inválida. Proporcione reservationId o sessionId."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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
