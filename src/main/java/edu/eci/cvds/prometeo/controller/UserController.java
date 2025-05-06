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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PhysicalProgressService physicalProgressService;
    
    @Autowired
    private RoutineService routineService;
    
    @Autowired
    private GymReservationService reservationService;

    @Autowired
    private ReportService reportService;

    // -----------------------------------------------------
    // User profile endpoints
    // -----------------------------------------------------
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long id,
            @RequestBody UserProfileUpdateDTO profileDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(id, profileDTO));
    }

    // -----------------------------------------------------
    // Physical tracking endpoints
    // -----------------------------------------------------
    
    @PostMapping("/{userId}/physical-records")
    public ResponseEntity<PhysicalRecord> createPhysicalRecord(
            @PathVariable Long userId,
            @RequestBody PhysicalRecordDTO recordDTO) {
        return new ResponseEntity<>(
                physicalProgressService.createRecord(userId, recordDTO),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/physical-records")
    public ResponseEntity<List<PhysicalRecord>> getUserPhysicalRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalProgressService.getUserRecords(userId));
    }

    @GetMapping("/{userId}/physical-records/latest")
    public ResponseEntity<PhysicalRecord> getLatestPhysicalRecord(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalProgressService.getLatestRecord(userId));
    }

    @GetMapping("/{userId}/physical-records/progress")
    public ResponseEntity<ProgressReportDTO> getPhysicalProgress(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(physicalProgressService.generateProgressReport(userId, startDate, endDate));
    }
    
    @PostMapping("/{userId}/physical-records/{recordId}/photos")
    public ResponseEntity<PhysicalRecordPhoto> uploadProgressPhoto(
            @PathVariable Long userId,
            @PathVariable Long recordId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String photoType) {
        return new ResponseEntity<>(
                physicalProgressService.addPhotoToRecord(userId, recordId, file, photoType),
                HttpStatus.CREATED);
    }
    
    @PostMapping("/{userId}/medical-notes")
    public ResponseEntity<MedicalNote> addMedicalNote(
            @PathVariable Long userId,
            @RequestBody MedicalNoteDTO noteDTO) {
        return new ResponseEntity<>(
                physicalProgressService.addMedicalNote(userId, noteDTO),
                HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}/medical-notes")
    public ResponseEntity<List<MedicalNote>> getUserMedicalNotes(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalProgressService.getUserMedicalNotes(userId));
    }

    // -----------------------------------------------------
    // Goals endpoints
    // -----------------------------------------------------
    
    @PostMapping("/{userId}/goals")
    public ResponseEntity<Goal> createGoal(
            @PathVariable Long userId,
            @RequestBody GoalDTO goalDTO) {
        return new ResponseEntity<>(
                physicalProgressService.createGoal(userId, goalDTO),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/goals")
    public ResponseEntity<List<Goal>> getUserGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalProgressService.getUserGoals(userId));
    }

    @PutMapping("/{userId}/goals/{goalId}")
    public ResponseEntity<Goal> updateGoal(
            @PathVariable Long userId,
            @PathVariable Long goalId,
            @RequestBody GoalDTO goalDTO) {
        return ResponseEntity.ok(physicalProgressService.updateGoal(userId, goalId, goalDTO));
    }
    
    @DeleteMapping("/{userId}/goals/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long userId,
            @PathVariable Long goalId) {
        physicalProgressService.deleteGoal(userId, goalId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/goals/progress")
    public ResponseEntity<List<GoalProgressDTO>> getUserGoalsProgress(@PathVariable Long userId) {
        return ResponseEntity.ok(physicalProgressService.getUserGoalsProgress(userId));
    }

    // -----------------------------------------------------
    // Routines endpoints
    // -----------------------------------------------------
    
    @GetMapping("/{userId}/routines")
    public ResponseEntity<List<Routine>> getUserRoutines(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.getUserRoutines(userId));
    }

    @GetMapping("/{userId}/routines/current")
    public ResponseEntity<Routine> getCurrentRoutine(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.getCurrentRoutine(userId));
    }

    @PostMapping("/{userId}/routines/assign/{routineId}")
    public ResponseEntity<Void> assignRoutineToUser(
            @PathVariable Long userId,
            @PathVariable Long routineId) {
        routineService.assignRoutineToUser(userId, routineId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/routines/public")
    public ResponseEntity<List<Routine>> getPublicRoutines(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(routineService.getPublicRoutines(category, difficulty));
    }
    
    @PostMapping("/{userId}/routines/custom")
    @PreAuthorize("hasRole('TRAINER') or @securityService.isResourceOwner(#userId)")
    public ResponseEntity<Routine> createCustomRoutine(
            @PathVariable Long userId,
            @RequestBody RoutineDTO routineDTO) {
        return new ResponseEntity<>(
                routineService.createCustomRoutine(userId, routineDTO),
                HttpStatus.CREATED);
    }
    
    @PutMapping("/{userId}/routines/{routineId}")
    @PreAuthorize("hasRole('TRAINER') or @securityService.isResourceOwner(#userId)")
    public ResponseEntity<Routine> updateRoutine(
            @PathVariable Long userId,
            @PathVariable Long routineId,
            @RequestBody RoutineDTO routineDTO) {
        return ResponseEntity.ok(routineService.updateRoutine(userId, routineId, routineDTO));
    }
    
    @GetMapping("/routines/{routineId}/details")
    public ResponseEntity<RoutineDetailDTO> getRoutineDetails(@PathVariable Long routineId) {
        return ResponseEntity.ok(routineService.getRoutineDetails(routineId));
    }
    
    @PostMapping("/{userId}/routines/progress")
    public ResponseEntity<RoutineProgress> logRoutineProgress(
            @PathVariable Long userId,
            @RequestBody RoutineProgressDTO progressDTO) {
        return new ResponseEntity<>(
                routineService.logRoutineProgress(userId, progressDTO),
                HttpStatus.CREATED);
    }

    // -----------------------------------------------------
    // Gym reservations endpoints
    // -----------------------------------------------------
    
    @GetMapping("/gym/availability")
    public ResponseEntity<Map<String, Integer>> getGymAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reservationService.getAvailabilityByDate(date));
    }

    @PostMapping("/{userId}/reservations")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable Long userId,
            @RequestBody ReservationDTO reservationDTO) {
        return new ResponseEntity<>(
                reservationService.createReservation(userId, reservationDTO),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/reservations")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUserReservations(userId));
    }

    @DeleteMapping("/{userId}/reservations/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long userId,
            @PathVariable Long reservationId) {
        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/reservations/upcoming")
    public ResponseEntity<List<Reservation>> getUpcomingReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUpcomingReservations(userId));
    }
    
    @GetMapping("/{userId}/reservations/history")
    public ResponseEntity<List<Reservation>> getReservationHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reservationService.getReservationHistory(userId, startDate, endDate));
    }

    // -----------------------------------------------------
    // Equipment reservations endpoints
    // -----------------------------------------------------
    
    @GetMapping("/gym/equipment")
    public ResponseEntity<List<Equipment>> getAvailableEquipment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return ResponseEntity.ok(reservationService.getAvailableEquipment(dateTime));
    }

    @PostMapping("/{userId}/reservations/{reservationId}/equipment")
    public ResponseEntity<EquipmentReservation> reserveEquipment(
            @PathVariable Long userId,
            @PathVariable Long reservationId,
            @RequestBody EquipmentReservationDTO equipmentDTO) {
        return new ResponseEntity<>(
                reservationService.reserveEquipment(userId, reservationId, equipmentDTO),
                HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{userId}/reservations/{reservationId}/equipment/{equipmentReservationId}")
    public ResponseEntity<Void> cancelEquipmentReservation(
            @PathVariable Long userId,
            @PathVariable Long reservationId,
            @PathVariable Long equipmentReservationId) {
        reservationService.cancelEquipmentReservation(userId, reservationId, equipmentReservationId);
        return ResponseEntity.ok().build();
    }

    // -----------------------------------------------------
    // Recommendations endpoints
    // -----------------------------------------------------
    
    @GetMapping("/{userId}/recommended-routines")
    public ResponseEntity<List<Routine>> getRecommendedRoutines(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.getRecommendedRoutines(userId));
    }
    
    @GetMapping("/{userId}/recommended-classes")
    public ResponseEntity<List<ClassRecommendationDTO>> getRecommendedClasses(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.getRecommendedClasses(userId));
    }

    // -----------------------------------------------------
    // Reports and analysis endpoints
    // -----------------------------------------------------
    
    @GetMapping("/{userId}/reports/attendance")
    public ResponseEntity<AttendanceReportDTO> getUserAttendanceReport(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateAttendanceReport(userId, startDate, endDate));
    }
    
    @GetMapping("/{userId}/reports/physical-evolution")
    public ResponseEntity<PhysicalEvolutionReportDTO> getUserPhysicalEvolutionReport(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generatePhysicalEvolutionReport(userId, startDate, endDate));
    }
    
    @GetMapping("/{userId}/reports/routine-compliance")
    public ResponseEntity<RoutineComplianceReportDTO> getUserRoutineComplianceReport(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateRoutineComplianceReport(userId, startDate, endDate));
    }
    
    // -----------------------------------------------------
    // Admin/Trainer specific endpoints
    // -----------------------------------------------------
    
    @PostMapping("/gym/capacity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<Void> configureGymCapacity(@RequestBody GymCapacityDTO capacityDTO) {
        reservationService.configureGymCapacity(capacityDTO);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/gym/block-timeslot")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<Void> blockGymTimeslot(@RequestBody BlockTimeslotDTO blockDTO) {
        reservationService.blockGymTimeslot(blockDTO);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/admin/gym/usage-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public ResponseEntity<GymUsageStatsDTO> getGymUsageStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateGymUsageStatistics(startDate, endDate));
    }
    
    @GetMapping("/trainer/assigned-users")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<UserProfileDTO>> getTrainerAssignedUsers() {
        return ResponseEntity.ok(userService.getTrainerAssignedUsers());
    }
    
    @PostMapping("/trainer/{trainerId}/assign-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isResourceOwner(#trainerId)")
    public ResponseEntity<Void> assignUserToTrainer(
            @PathVariable Long trainerId,
            @PathVariable Long userId) {
        userService.assignUserToTrainer(userId, trainerId);
        return ResponseEntity.ok().build();
    }
}
