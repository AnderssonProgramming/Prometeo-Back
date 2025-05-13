package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.*;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class UserControllerTest {

    @Mock
    private UserService userService;
    
    @Mock
    private GymReservationService gymReservationService;
    
    @Mock
    private RoutineRepository routineRepository;
    
    @Mock
    private RoutineExerciseRepository routineExerciseRepository;
    
    @Mock
    private BaseExerciseService baseExerciseService;
    
    @Mock
    private GoalService goalService;
    
    @Mock
    private GymSessionService gymSessionService;
    
    @InjectMocks
    private UserController userController;
    
    private User testUser;
    private UUID userId;
    private UserDTO userDTO;
    
    @BeforeEach
    public void setup() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test user");
        
        userDTO = new UserDTO();
        userDTO.setName("Test user");
    }
    
    // User profile endpoint tests
    
    @Test
    public void testGetUserById() {
        when(userService.getUserById(anyString())).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.getUserById("1");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).getUserById("1");
    }
    
    @Test
    public void testGetUserByInstitutionalId() {
        when(userService.getUserByInstitutionalId(anyString())).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.getUserByInstitutionalId("A12345");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).getUserByInstitutionalId("A12345");
    }
    
    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);
        
        ResponseEntity<List<User>> response = userController.getAllUsers();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService).getAllUsers();
    }
    
    @Test
    public void testGetUsersByRole() {
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByRole(anyString())).thenReturn(users);
        
        ResponseEntity<List<User>> response = userController.getUsersByRole("STUDENT");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService).getUsersByRole("STUDENT");
    }
    
    @Test
    public void testCreateUser() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.createUser(userDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).createUser(userDTO);
    }
    
    @Test
    public void testUpdateUser() {
        when(userService.updateUser(anyString(), any(UserDTO.class))).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.updateUser("1", userDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).updateUser("1", userDTO);
    }
    
    @Test
    public void testDeleteUser() {
        when(userService.deleteUser(anyString())).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.deleteUser("1");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).deleteUser("1");
    }
    
    // Physical tracking endpoint tests
    
    @Test
    public void testRecordPhysicalMeasurement() {
        PhysicalProgress progress = new PhysicalProgress();
        PhysicalProgressDTO progressDTO = new PhysicalProgressDTO();
        
        WeightDTO weightDTO = new WeightDTO();
        weightDTO.setValue(70.5);
        progressDTO.setWeight(weightDTO);
        
        BodyMeasurementsDTO measurementsDTO = new BodyMeasurementsDTO();
        measurementsDTO.setHeight(180.0);
        measurementsDTO.setChestCircumference(90.0);
        progressDTO.setMeasurements(measurementsDTO);
        
        when(userService.recordPhysicalMeasurement(any(UUID.class), any(PhysicalProgress.class))).thenReturn(progress);
        
        ResponseEntity<PhysicalProgress> response = userController.recordPhysicalMeasurement(userId, progressDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(progress, response.getBody());
        verify(userService).recordPhysicalMeasurement(eq(userId), any(PhysicalProgress.class));
    }
    
    @Test
    public void testGetPhysicalMeasurementHistory() {
        List<PhysicalProgress> history = new ArrayList<>();
        when(userService.getPhysicalMeasurementHistory(any(UUID.class), any(), any())).thenReturn(history);
        
        ResponseEntity<List<PhysicalProgress>> response = userController.getPhysicalMeasurementHistory(
                userId, LocalDate.now().minusDays(30), LocalDate.now());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(history, response.getBody());
        verify(userService).getPhysicalMeasurementHistory(eq(userId), any(), any());
    }
    
    @Test
    public void testGetLatestPhysicalMeasurement() {
        PhysicalProgress progress = new PhysicalProgress();
        when(userService.getLatestPhysicalMeasurement(any(UUID.class))).thenReturn(Optional.of(progress));
        
        ResponseEntity<PhysicalProgress> response = userController.getLatestPhysicalMeasurement(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(progress, response.getBody());
        verify(userService).getLatestPhysicalMeasurement(userId);
    }
    
    @Test
    public void testGetLatestPhysicalMeasurement_NotFound() {
        when(userService.getLatestPhysicalMeasurement(any(UUID.class))).thenReturn(Optional.empty());
        
        ResponseEntity<PhysicalProgress> response = userController.getLatestPhysicalMeasurement(userId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getLatestPhysicalMeasurement(userId);
    }
    
    // Goals endpoint tests
    
    @Test
    public void testCreateGoal() {
        List<String> goals = Arrays.asList("Lose weight", "Build muscle");
        
        ResponseEntity<String> response = userController.createGoal(userId, goals);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goals updated and recommendations refreshed.", response.getBody());
        verify(goalService).addUserGoal(userId, goals);
    }
    
    @Test
    public void testGetUserGoals() {
        List<Goal> goals = new ArrayList<>();
        when(goalService.getGoalsByUser(any(UUID.class))).thenReturn(goals);
        
        ResponseEntity<List<Goal>> response = userController.getUserGoals(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(goals, response.getBody());
        verify(goalService).getGoalsByUser(userId);
    }
    
    // Routines endpoint tests
    
    @Test
    public void testGetUserRoutines() {
        List<Routine> routines = new ArrayList<>();
        when(userService.getUserRoutines(any(UUID.class))).thenReturn(routines);
        
        ResponseEntity<List<Routine>> response = userController.getUserRoutines(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(routines, response.getBody());
        verify(userService).getUserRoutines(userId);
    }
    
    @Test
    public void testGetCurrentRoutine() {
        Routine routine = new Routine();
        when(routineRepository.findCurrentRoutineByUserId(any(UUID.class))).thenReturn(Optional.of(routine));
        
        ResponseEntity<Routine> response = userController.getCurrentRoutine(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(routine, response.getBody());
        verify(routineRepository).findCurrentRoutineByUserId(userId);
    }
    
    @Test
    public void testAssignRoutineToUser() {
        UUID routineId = UUID.randomUUID();
        doNothing().when(userService).assignRoutineToUser(any(UUID.class), any(UUID.class));
        
        ResponseEntity<Void> response = userController.assignRoutineToUser(userId, routineId);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).assignRoutineToUser(userId, routineId);
    }
    
    // Exercise endpoint tests
    
    @Test
    public void testGetAllExercises() {
        List<BaseExercise> exercises = new ArrayList<>();
        when(baseExerciseService.getAllExercises()).thenReturn(exercises);
        
        ResponseEntity<List<BaseExercise>> response = userController.getAllExercises();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exercises, response.getBody());
        verify(baseExerciseService).getAllExercises();
    }
    
    @Test
    public void testGetExerciseById() {
        UUID exerciseId = UUID.randomUUID();
        BaseExercise exercise = new BaseExercise();
        when(baseExerciseService.getExerciseById(any(UUID.class))).thenReturn(Optional.of(exercise));
        
        ResponseEntity<BaseExercise> response = userController.getExerciseById(exerciseId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exercise, response.getBody());
        verify(baseExerciseService).getExerciseById(exerciseId);
    }
    
    // Gym reservation endpoint tests
    
    @Test
    public void testGetGymAvailability() {
        List<Object> availableSlots = new ArrayList<>();
        when(userService.getAvailableTimeSlots(any(LocalDate.class))).thenReturn(availableSlots);
        
        ResponseEntity<List<Object>> response = userController.getGymAvailability(LocalDate.now());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availableSlots, response.getBody());
        verify(userService).getAvailableTimeSlots(any(LocalDate.class));
    }
    
    @Test
    public void testCreateReservation() {
        UUID reservationId = UUID.randomUUID();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservationId);
        
        when(gymReservationService.create(any(ReservationDTO.class))).thenReturn(reservationDTO);
        
        ResponseEntity<Object> response = userController.createReservation(userId, reservationDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) response.getBody();
        assertEquals(reservationId, responseMap.get("reservationId"));
        verify(gymReservationService).create(any(ReservationDTO.class));
    }
    
    // Gym session endpoint tests
    
    @Test
    public void testCreateSession() {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("date", LocalDate.now().toString());
        sessionData.put("startTime", LocalTime.of(9, 0).toString());
        sessionData.put("endTime", LocalTime.of(10, 0).toString());
        sessionData.put("capacity", 10);
        sessionData.put("trainerId", userId.toString());
        sessionData.put("description", "Test session");
        
        UUID sessionId = UUID.randomUUID();
        when(gymSessionService.createSession(any(), any(), any(), anyInt(), any(), any())).thenReturn(sessionId);
        
        ResponseEntity<Map<String, Object>> response = userController.createSession(sessionData);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sessionId, response.getBody().get("sessionId"));
        verify(gymSessionService).createSession(any(), any(), any(), anyInt(), any(), any());
    }

    @Test
    public void testUpdateGoal() {
        Map<UUID, String> updatedGoals = new HashMap<>();
        UUID goalId = UUID.randomUUID();
        updatedGoals.put(goalId, "New goal text");
        
        doNothing().when(goalService).updateUserGoal(any(Map.class));
        
        ResponseEntity<String> response = userController.updateGoal(updatedGoals);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal updated.", response.getBody());
        verify(goalService).updateUserGoal(updatedGoals);
    }

    @Test
    public void testDeleteGoal() {
        UUID goalId = UUID.randomUUID();
        doNothing().when(goalService).deleteGoal(any(UUID.class));
        
        ResponseEntity<String> response = userController.deleteGoal(goalId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal deleted.", response.getBody());
        verify(goalService).deleteGoal(goalId);
    }

    @Test
    public void testUpdatePhysicalMeasurements() {
        UUID progressId = UUID.randomUUID();
        BodyMeasurementsDTO measurementsDTO = new BodyMeasurementsDTO();
        measurementsDTO.setHeight(185.0);
        measurementsDTO.setChestCircumference(95.0);
        
        PhysicalProgress updatedProgress = new PhysicalProgress();
        when(userService.updatePhysicalMeasurement(any(UUID.class), any(BodyMeasurements.class))).thenReturn(updatedProgress);
        
        ResponseEntity<PhysicalProgress> response = userController.updatePhysicalMeasurements(progressId, measurementsDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProgress, response.getBody());
        verify(userService).updatePhysicalMeasurement(eq(progressId), any(BodyMeasurements.class));
    }

    @Test
    public void testSetPhysicalGoal() {
        Map<String, String> body = new HashMap<>();
        body.put("goal", "Gain muscle");
        PhysicalProgress updatedProgress = new PhysicalProgress();
        
        when(userService.setPhysicalGoal(any(UUID.class), anyString())).thenReturn(updatedProgress);
        
        ResponseEntity<PhysicalProgress> response = userController.setPhysicalGoal(userId, body);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProgress, response.getBody());
        verify(userService).setPhysicalGoal(userId, "Gain muscle");
    }

    @Test
    public void testGetPhysicalProgressMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("weightChange", -2.5);
        metrics.put("waistReduction", 3.0);
        
        when(userService.calculatePhysicalProgressMetrics(any(UUID.class), anyInt())).thenReturn(metrics);
        
        ResponseEntity<Map<String, Double>> response = userController.getPhysicalProgressMetrics(userId, 3);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(metrics, response.getBody());
        verify(userService).calculatePhysicalProgressMetrics(userId, 3);
    }

    @Test
    public void testGetTraineePhysicalProgress() {
        UUID trainerId = UUID.randomUUID();
        List<PhysicalProgress> history = new ArrayList<>();
        
        when(userService.getPhysicalMeasurementHistory(any(UUID.class), any(), any())).thenReturn(history);
        
        ResponseEntity<List<PhysicalProgress>> response = userController.getTraineePhysicalProgress(
                trainerId, userId, LocalDate.now().minusMonths(1), LocalDate.now());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(history, response.getBody());
        verify(userService).getPhysicalMeasurementHistory(eq(userId), any(), any());
    }

    @Test
    public void testCreateCustomRoutine() {
        RoutineDTO routineDTO = new RoutineDTO();
        routineDTO.setName("Custom Workout");
        routineDTO.setDescription("Test routine");
        routineDTO.setDifficulty("Medium");
        routineDTO.setGoal("Strength");
        
        List<RoutineExerciseDTO> exercises = new ArrayList<>();
        RoutineExerciseDTO exerciseDTO = new RoutineExerciseDTO();
        exerciseDTO.setBaseExerciseId(UUID.randomUUID());
        exerciseDTO.setSets(3);
        exerciseDTO.setRepetitions(12);
        routineDTO.setExercises(exercises);
        
        Routine routine = new Routine();
        routine.setId(UUID.randomUUID());
        
        when(userService.createCustomRoutine(any(UUID.class), any(Routine.class))).thenReturn(routine);
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.of(routine));
        
        ResponseEntity<Routine> response = userController.createCustomRoutine(userId, routineDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(routine, response.getBody());
        verify(userService).createCustomRoutine(eq(userId), any(Routine.class));
    }

    @Test
    public void testUpdateRoutine() {
        UUID routineId = UUID.randomUUID();
        RoutineDTO routineDTO = new RoutineDTO();
        routineDTO.setName("Updated Workout");
        routineDTO.setDescription("Updated description");
        
        Routine existingRoutine = new Routine();
        Routine updatedRoutine = new Routine();
        
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingRoutine));
        when(userService.updateRoutine(any(UUID.class), any(Routine.class))).thenReturn(updatedRoutine);
        
        ResponseEntity<Routine> response = userController.updateRoutine(routineId, routineDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRoutine, response.getBody());
        verify(userService).updateRoutine(eq(routineId), any(Routine.class));
    }

    @Test
    public void testLogRoutineProgress() {
        UUID routineId = UUID.randomUUID();
        Map<String, Integer> progressData = new HashMap<>();
        progressData.put("completed", 75);
        
        doNothing().when(userService).logRoutineProgress(any(UUID.class), any(UUID.class), anyInt());
        
        ResponseEntity<Void> response = userController.logRoutineProgress(userId, routineId, progressData);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).logRoutineProgress(userId, routineId, 75);
    }

    @Test
    public void testGetRecommendedRoutines() {
        List<Routine> recommendations = new ArrayList<>();
        when(userService.getRecommendedRoutines(any(UUID.class))).thenReturn(recommendations);
        
        ResponseEntity<List<Routine>> response = userController.getRecommendedRoutines(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recommendations, response.getBody());
        verify(userService).getRecommendedRoutines(userId);
    }

    @Test
    public void testCheckAvailabilityForTime() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(14, 0);
        Map<String, Object> availability = new HashMap<>();
        availability.put("available", true);
        availability.put("capacity", 20);
        
        when(gymReservationService.getAvailability(any(LocalDate.class), any(LocalTime.class))).thenReturn(availability);
        
        ResponseEntity<Map<String, Object>> response = userController.checkAvailabilityForTime(date, time);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availability, response.getBody());
        verify(gymReservationService).getAvailability(date, time);
    }

    @Test
    public void testGetUserReservations() {
        List<ReservationDTO> reservations = new ArrayList<>();
        when(gymReservationService.getByUserId(any(UUID.class))).thenReturn(reservations);
        
        ResponseEntity<List<ReservationDTO>> response = userController.getUserReservations(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(gymReservationService).getByUserId(userId);
    }
    
    // Additional tests can be added as needed
}