package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.*;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.model.enums.ReportFormat;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

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
    
    @Mock
    private ReportService reportService;
    
    @InjectMocks
    private UserController userController;
    
    private User testUser;
    private UUID userId;
    private UserDTO userDTO;
      @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test user");
        
        userDTO = new UserDTO();
        userDTO.setName("Test user");
    }
    
    // User profile endpoint tests
      @Test
    void testGetUserById() {
        // Use exact match instead of anyString()
        when(userService.getUserById("1")).thenReturn(testUser);
        
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
    void createUserSuccessfully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("institutionalId")).thenReturn("A12345");
        when(request.getAttribute("username")).thenReturn("testuser");
        when(request.getAttribute("name")).thenReturn("Test User");
        when(request.getAttribute("role")).thenReturn("USER");

        UserDTO userDTO = new UserDTO();
        userDTO.setInstitutionalId("A12345");
        userDTO.setName("Test User");
        userDTO.setRole("USER");

        User createdUser = new User();
        createdUser.setInstitutionalId("A12345");
        createdUser.setName("Test User");
        createdUser.setRole("USER");

        when(userService.userExistsByInstitutionalId("A12345")).thenReturn(false);
        when(userService.createUser(userDTO)).thenReturn(createdUser);

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdUser, response.getBody());
        verify(userService).userExistsByInstitutionalId("A12345");
        verify(userService).createUser(userDTO);
    }

    @Test
    void createUserFailsWhenAttributesAreMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("institutionalId")).thenReturn(null);
        when(request.getAttribute("name")).thenReturn("Test User");
        when(request.getAttribute("role")).thenReturn("USER");

        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).userExistsByInstitutionalId(anyString());
        verify(userService, never()).createUser(any(UserDTO.class));
    }


      @Test
    void testCreateUser() {
        // Use the exact object instead of any()
        when(userService.createUser(userDTO)).thenReturn(testUser);
        
        ResponseEntity<User> response = userController.createUser(userDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).createUser(userDTO);
    }

      @Test
    void testUpdateUser() {
        // Use exact matches instead of any()
        when(userService.updateUser("1", userDTO)).thenReturn(testUser);
        
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
    void testRecordPhysicalMeasurement() {
        PhysicalProgress progress = new PhysicalProgress();
        PhysicalProgressDTO progressDTO = new PhysicalProgressDTO();
        
        WeightDTO weightDTO = new WeightDTO();
        weightDTO.setValue(70.5);
        progressDTO.setWeight(weightDTO);
        
        BodyMeasurementsDTO measurementsDTO = new BodyMeasurementsDTO();
        measurementsDTO.setHeight(180.0);
        measurementsDTO.setChestCircumference(90.0);
        progressDTO.setMeasurements(measurementsDTO);
        
        // For this kind of case where we can't easily predict the exact object,
        // we need to use the Mockito.argThat matcher
        when(userService.recordPhysicalMeasurement(eq(userId), any(PhysicalProgress.class))).thenReturn(progress);
        
        ResponseEntity<PhysicalProgress> response = userController.recordPhysicalMeasurement(userId, progressDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(progress, response.getBody());
        verify(userService).recordPhysicalMeasurement(eq(userId), any(PhysicalProgress.class));
    }
      @Test
    void testGetPhysicalMeasurementHistory() {
        List<PhysicalProgress> history = new ArrayList<>();
        
        // Pre-define the dates to use exact values in our stubbing
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        
        when(userService.getPhysicalMeasurementHistory(
                eq(userId), 
                eq(Optional.of(startDate)), 
                eq(Optional.of(endDate))
        )).thenReturn(history);
        
        ResponseEntity<List<PhysicalProgress>> response = userController.getPhysicalMeasurementHistory(
                userId, startDate, endDate);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(history, response.getBody());
        verify(userService).getPhysicalMeasurementHistory(
                eq(userId), 
                eq(Optional.of(startDate)), 
                eq(Optional.of(endDate))
        );
    }
      @Test
    void testGetLatestPhysicalMeasurement() {
        PhysicalProgress progress = new PhysicalProgress();
        when(userService.getLatestPhysicalMeasurement(userId)).thenReturn(Optional.of(progress));
        
        ResponseEntity<PhysicalProgress> response = userController.getLatestPhysicalMeasurement(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(progress, response.getBody());
        verify(userService).getLatestPhysicalMeasurement(userId);
    }
    
    @Test
    void testGetLatestPhysicalMeasurement_NotFound() {
        when(userService.getLatestPhysicalMeasurement(userId)).thenReturn(Optional.empty());
        
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
    }    @Test
    void testGetPhysicalProgressMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("weightChange", -2.5);
        metrics.put("waistReduction", 3.0);
        
        when(userService.calculatePhysicalProgressMetrics(userId, 3)).thenReturn(metrics);
        
        ResponseEntity<Map<String, Double>> response = userController.getPhysicalProgressMetrics(userId, 3);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(metrics, response.getBody());
        verify(userService).calculatePhysicalProgressMetrics(userId, 3);
    }@Test
    void testGetTraineePhysicalProgress() {
        UUID trainerId = UUID.randomUUID();
        List<PhysicalProgress> history = new ArrayList<>();
        
        // Pre-define the dates to use exact values
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        when(userService.getPhysicalMeasurementHistory(
                eq(userId), 
                eq(Optional.of(startDate)), 
                eq(Optional.of(endDate))
        )).thenReturn(history);
        
        ResponseEntity<List<PhysicalProgress>> response = userController.getTraineePhysicalProgress(
                trainerId, userId, startDate, endDate);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(history, response.getBody());
        verify(userService).getPhysicalMeasurementHistory(
                eq(userId), 
                eq(Optional.of(startDate)), 
                eq(Optional.of(endDate))
        );
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
    }    @Test
    void testLogRoutineProgress() {
        UUID routineId = UUID.randomUUID();
        Map<String, Integer> progressData = new HashMap<>();
        progressData.put("completed", 75);
        
        // Fix: Don't use doNothing for methods that aren't void - just don't mock the return value
        // The method call will do nothing by default if it's not explicitly mocked
        
        ResponseEntity<Void> response = userController.logRoutineProgress(userId, routineId, progressData);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).logRoutineProgress(userId, routineId, 75);
    }@Test
    void testGetRecommendedRoutines() {
        List<Routine> recommendations = new ArrayList<>();
        when(userService.getRecommendedRoutines(userId)).thenReturn(recommendations);
        
        ResponseEntity<List<Routine>> response = userController.getRecommendedRoutines(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recommendations, response.getBody());
        verify(userService).getRecommendedRoutines(userId);
    }@Test
    void testCheckAvailabilityForTime() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(14, 0);
        Map<String, Object> availability = new HashMap<>();
        availability.put("available", true);
        availability.put("capacity", 20);
        
        when(gymReservationService.getAvailability(date, time)).thenReturn(availability);
        
        ResponseEntity<Map<String, Object>> response = userController.checkAvailabilityForTime(date, time);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(availability, response.getBody());
        verify(gymReservationService).getAvailability(date, time);
    }@Test
    void testGetUserReservations() {
        List<ReservationDTO> reservations = new ArrayList<>();
        when(gymReservationService.getByUserId(userId)).thenReturn(reservations);
        
        ResponseEntity<List<ReservationDTO>> response = userController.getUserReservations(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(gymReservationService).getByUserId(userId);
    }

    @Test
    public void testGetReservationDetails() {
        UUID reservationId = UUID.randomUUID();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserId(userId);
        reservationDTO.setId(reservationId);
        
        when(gymReservationService.getById(reservationId)).thenReturn(Optional.of(reservationDTO));
        
        ResponseEntity<ReservationDTO> response = userController.getReservationDetails(userId, reservationId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservationDTO, response.getBody());
        verify(gymReservationService).getById(reservationId);
    }

    @Test
    public void testGetReservationDetails_NotFound() {
        UUID reservationId = UUID.randomUUID();
        when(gymReservationService.getById(reservationId)).thenReturn(Optional.empty());
        
        ResponseEntity<ReservationDTO> response = userController.getReservationDetails(userId, reservationId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(gymReservationService).getById(reservationId);
    }

    @Test
    public void testGetReservationDetails_WrongUser() {
        UUID reservationId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserId(differentUserId);
        reservationDTO.setId(reservationId);
        
        when(gymReservationService.getById(reservationId)).thenReturn(Optional.of(reservationDTO));
        
        ResponseEntity<ReservationDTO> response = userController.getReservationDetails(userId, reservationId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(gymReservationService).getById(reservationId);
    }

    @Test
    public void testCancelReservation() {
        UUID reservationId = UUID.randomUUID();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserId(userId);
        reservationDTO.setId(reservationId);
        
        when(gymReservationService.getById(reservationId)).thenReturn(Optional.of(reservationDTO));
        doNothing().when(gymReservationService).delete(reservationId);
        
        ResponseEntity<Object> response = userController.cancelReservation(userId, reservationId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = (Map<String, String>) response.getBody();
        assertEquals("Reserva cancelada exitosamente", responseMap.get("message"));
        verify(gymReservationService).getById(reservationId);
        verify(gymReservationService).delete(reservationId);
    }

    @Test
    public void testCancelReservation_WrongUser() {
        UUID reservationId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserId(differentUserId);
        reservationDTO.setId(reservationId);
        
        when(gymReservationService.getById(reservationId)).thenReturn(Optional.of(reservationDTO));
        
        ResponseEntity<Object> response = userController.cancelReservation(userId, reservationId);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(gymReservationService).getById(reservationId);
        verify(gymReservationService, never()).delete(any(UUID.class));
    }

    @Test
    public void testGetExercisesByMuscleGroup() {
        String muscleGroup = "chest";
        List<BaseExercise> exercises = new ArrayList<>();
        when(baseExerciseService.getExercisesByMuscleGroup(muscleGroup)).thenReturn(exercises);
        
        ResponseEntity<List<BaseExercise>> response = userController.getExercisesByMuscleGroup(muscleGroup);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exercises, response.getBody());
        verify(baseExerciseService).getExercisesByMuscleGroup(muscleGroup);
    }

    @Test
    public void testSearchExercises() {
        String searchTerm = "push";
        List<BaseExercise> exercises = new ArrayList<>();
        when(baseExerciseService.searchExercisesByName(searchTerm)).thenReturn(exercises);
        
        ResponseEntity<List<BaseExercise>> response = userController.searchExercises(searchTerm);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exercises, response.getBody());
        verify(baseExerciseService).searchExercisesByName(searchTerm);
    }

    @Test
    public void testCreateExercise() {
        BaseExerciseDTO exerciseDTO = new BaseExerciseDTO();
        BaseExercise exercise = new BaseExercise();
        when(baseExerciseService.createExercise(exerciseDTO)).thenReturn(exercise);
        
        ResponseEntity<BaseExercise> response = userController.createExercise(exerciseDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(exercise, response.getBody());
        verify(baseExerciseService).createExercise(exerciseDTO);
    }


    @Test
    public void testGetSessionById() {
        UUID sessionId = UUID.randomUUID();
        Object session = new Object();
        when(gymSessionService.getSessionById(sessionId)).thenReturn(session);
        
        ResponseEntity<Object> response = userController.getSessionById(sessionId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(session, response.getBody());
        verify(gymSessionService).getSessionById(sessionId);
    }

    @Test
    public void testGetSessionById_NotFound() {
        UUID sessionId = UUID.randomUUID();
        when(gymSessionService.getSessionById(sessionId)).thenThrow(new RuntimeException("Session not found"));
        
        ResponseEntity<Object> response = userController.getSessionById(sessionId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        verify(gymSessionService).getSessionById(sessionId);
    }    @Test
    public void testGetUserWaitlists() {
        List<Map<String, Object>> waitlists = new ArrayList<>();
        when(gymReservationService.getUserWaitlists(userId)).thenReturn(waitlists);
        
        ResponseEntity<List<Map<String, Object>>> response = userController.getUserWaitlists(userId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(waitlists, response.getBody());
        verify(gymReservationService).getUserWaitlists(userId);
    }

    // @Test
    // public void testCreateRecurringSessions() {
    //     // Prepare test data
    //     Map<String, Object> recurringData = new HashMap<>();
    //     recurringData.put("startDate", LocalDate.now().toString());
    //     recurringData.put("endDate", LocalDate.now().plusMonths(1).toString());
    //     recurringData.put("dayOfWeek", "MONDAY");
    //     recurringData.put("startTime", "10:00");
    //     recurringData.put("endTime", "11:00");
    //     recurringData.put("capacity", 15);
    //     recurringData.put("trainerId", UUID.randomUUID().toString());
    //     recurringData.put("description", "Recurring gym session");
        
    //     List<UUID> createdSessionIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
    //     when(gymSessionService.createRecurringSessions(any())).thenReturn(createdSessionIds);
        
    //     ResponseEntity<Map<String, Object>> response = userController.createRecurringSessions(recurringData);
        
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    //     assertTrue(response.getBody().containsKey("sessionIds"));
    //     assertEquals(createdSessionIds, response.getBody().get("sessionIds"));
    //     verify(gymSessionService).createRecurringSessions(recurringData);
    // }

    // @Test
    // public void testUpdateSession() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     Map<String, Object> sessionData = new HashMap<>();
    //     sessionData.put("date", LocalDate.now().toString());
    //     sessionData.put("startTime", "14:00");
    //     sessionData.put("endTime", "15:00");
    //     sessionData.put("capacity", 20);
    //     sessionData.put("description", "Updated session description");
        
    //     when(gymSessionService.updateSession(eq(sessionId), any())).thenReturn(true);
        
    //     ResponseEntity<Object> response = userController.updateSession(sessionId, sessionData);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof Map);
    //     @SuppressWarnings("unchecked")
    //     Map<String, String> responseBody = (Map<String, String>) response.getBody();
    //     assertEquals("Sesión actualizada correctamente", responseBody.get("message"));
    //     verify(gymSessionService).updateSession(eq(sessionId), any());
    // }

    // @Test
    // public void testUpdateSession_Failure() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     Map<String, Object> sessionData = new HashMap<>();
    //     sessionData.put("date", LocalDate.now().toString());
        
    //     when(gymSessionService.updateSession(eq(sessionId), any())).thenReturn(false);
        
    //     ResponseEntity<Object> response = userController.updateSession(sessionId, sessionData);
        
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     verify(gymSessionService).updateSession(eq(sessionId), any());
    // }

    // @Test
    // public void testCancelSession() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     Map<String, Object> cancelData = new HashMap<>();
    //     cancelData.put("reason", "Maintenance");
        
    //     when(gymSessionService.cancelSession(eq(sessionId), any())).thenReturn(true);
        
    //     ResponseEntity<Object> response = userController.cancelSession(sessionId, cancelData);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof Map);
    //     @SuppressWarnings("unchecked")
    //     Map<String, String> responseBody = (Map<String, String>) response.getBody();
    //     assertEquals("Sesión cancelada correctamente", responseBody.get("message"));
    //     verify(gymSessionService).cancelSession(eq(sessionId), any());
    // }

    // @Test
    // public void testCancelSession_Failure() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     Map<String, Object> cancelData = new HashMap<>();
    //     cancelData.put("reason", "Maintenance");
        
    //     when(gymSessionService.cancelSession(eq(sessionId), any())).thenReturn(false);
        
    //     ResponseEntity<Object> response = userController.cancelSession(sessionId, cancelData);
        
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     verify(gymSessionService).cancelSession(eq(sessionId), any());
    // }

    // @Test
    // public void testRecordStudentAttendance() {
    //     // Prepare test data
    //     Map<String, Object> attendanceData = new HashMap<>();
    //     attendanceData.put("sessionId", UUID.randomUUID().toString());
    //     attendanceData.put("userId", UUID.randomUUID().toString());
    //     attendanceData.put("attended", true);
        
    //     when(gymSessionService.recordAttendance(any())).thenReturn(true);
        
    //     ResponseEntity<Object> response = userController.recordStudentAttendance(attendanceData);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof Map);
    //     @SuppressWarnings("unchecked")
    //     Map<String, String> responseBody = (Map<String, String>) response.getBody();
    //     assertEquals("Asistencia registrada correctamente", responseBody.get("message"));
    //     verify(gymSessionService).recordAttendance(attendanceData);
    // }

    // @Test
    // public void testRecordStudentAttendance_Failure() {
    //     // Prepare test data
    //     Map<String, Object> attendanceData = new HashMap<>();
    //     attendanceData.put("sessionId", UUID.randomUUID().toString());
    //     attendanceData.put("userId", UUID.randomUUID().toString());
    //     attendanceData.put("attended", true);
        
    //     when(gymSessionService.recordAttendance(any())).thenReturn(false);
        
    //     ResponseEntity<Object> response = userController.recordStudentAttendance(attendanceData);
        
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     verify(gymSessionService).recordAttendance(attendanceData);
    // }

    // @Test
    // public void testJoinWaitlist() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
        
    //     when(gymReservationService.addToWaitlist(userId, sessionId)).thenReturn(true);
        
    //     ResponseEntity<Object> response = userController.joinWaitlist(userId, sessionId);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof Map);
    //     @SuppressWarnings("unchecked")
    //     Map<String, String> responseBody = (Map<String, String>) response.getBody();
    //     assertEquals("Agregado a la lista de espera exitosamente", responseBody.get("message"));
    //     verify(gymReservationService).addToWaitlist(userId, sessionId);
    // }

    // @Test
    // public void testJoinWaitlist_Failure() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
        
    //     when(gymReservationService.addToWaitlist(userId, sessionId)).thenReturn(false);
        
    //     ResponseEntity<Object> response = userController.joinWaitlist(userId, sessionId);
        
    //     assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    //     verify(gymReservationService).addToWaitlist(userId, sessionId);
    // }

    // @Test
    // public void testLeaveWaitlist() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
        
    //     when(gymReservationService.removeFromWaitlist(userId, sessionId)).thenReturn(true);
        
    //     ResponseEntity<Object> response = userController.leaveWaitlist(userId, sessionId);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertTrue(response.getBody() instanceof Map);
    //     @SuppressWarnings("unchecked")
    //     Map<String, String> responseBody = (Map<String, String>) response.getBody();
    //     assertEquals("Eliminado de la lista de espera exitosamente", responseBody.get("message"));
    //     verify(gymReservationService).removeFromWaitlist(userId, sessionId);
    // }

    // @Test
    // public void testLeaveWaitlist_Failure() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
        
    //     when(gymReservationService.removeFromWaitlist(userId, sessionId)).thenReturn(false);
        
    //     ResponseEntity<Object> response = userController.leaveWaitlist(userId, sessionId);
        
    //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    //     verify(gymReservationService).removeFromWaitlist(userId, sessionId);
    // }    @Test
    public void testUpdateExercise() {
        // Prepare test data
        UUID exerciseId = UUID.randomUUID();
        BaseExerciseDTO exerciseDTO = new BaseExerciseDTO();
        exerciseDTO.setName("Updated Exercise");
        exerciseDTO.setDescription("Updated description");
        exerciseDTO.setMuscleGroup("Legs");
        
        BaseExercise updatedExercise = new BaseExercise();
        when(baseExerciseService.updateExercise(eq(exerciseId), any(BaseExerciseDTO.class))).thenReturn(updatedExercise);
        
        ResponseEntity<BaseExercise> response = userController.updateExercise(exerciseId, exerciseDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedExercise, response.getBody());
        verify(baseExerciseService).updateExercise(eq(exerciseId), any(BaseExerciseDTO.class));
    }
    
    @Test
    public void testUpdateExercise_NotFound() {
        // Prepare test data
        UUID exerciseId = UUID.randomUUID();
        BaseExerciseDTO exerciseDTO = new BaseExerciseDTO();
        exerciseDTO.setName("Updated Exercise");
        
        when(baseExerciseService.updateExercise(eq(exerciseId), any(BaseExerciseDTO.class)))
                .thenThrow(new RuntimeException("Exercise not found"));
        
        ResponseEntity<BaseExercise> response = userController.updateExercise(exerciseId, exerciseDTO);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testDeleteExercise() {
        // Prepare test data
        UUID exerciseId = UUID.randomUUID();
        doNothing().when(baseExerciseService).deleteExercise(exerciseId);
        
        ResponseEntity<Void> response = userController.deleteExercise(exerciseId);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(baseExerciseService).deleteExercise(exerciseId);
    }
    
    @Test
    public void testDeleteExercise_NotFound() {
        // Prepare test data
        UUID exerciseId = UUID.randomUUID();
        doThrow(new RuntimeException("Exercise not found")).when(baseExerciseService).deleteExercise(exerciseId);
        
        ResponseEntity<Void> response = userController.deleteExercise(exerciseId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testUpdateGoal() {
        // Prepare test data
        Map<UUID, String> updatedGoals = new HashMap<>();
        UUID goalId = UUID.randomUUID();
        updatedGoals.put(goalId, "Updated goal text");
        
        doNothing().when(goalService).updateUserGoal(updatedGoals);
        
        ResponseEntity<String> response = userController.updateGoal(updatedGoals);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal updated.", response.getBody());
    }
    
    @Test
    public void testUpdateGoal_Failure() {
        // Prepare test data
        Map<UUID, String> updatedGoals = new HashMap<>();
        UUID goalId = UUID.randomUUID();
        updatedGoals.put(goalId, "Updated goal text");
        
        doThrow(new RuntimeException("Goal not found")).when(goalService).updateUserGoal(updatedGoals);
        
        ResponseEntity<String> response = userController.updateGoal(updatedGoals);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Goal not found", response.getBody());
    }
    
    @Test
    public void testDeleteGoal() {
        // Prepare test data
        UUID goalId = UUID.randomUUID();
        
        doNothing().when(goalService).deleteGoal(goalId);
        
        ResponseEntity<String> response = userController.deleteGoal(goalId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Goal deleted.", response.getBody());
    }
    
    @Test
    public void testDeleteGoal_Failure() {
        // Prepare test data
        UUID goalId = UUID.randomUUID();
        
        doThrow(new RuntimeException("Goal not found")).when(goalService).deleteGoal(goalId);
        
        ResponseEntity<String> response = userController.deleteGoal(goalId);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Goal not found", response.getBody());
    }

    
    @Test
    public void testCreateGoal_Failure() {
        // Prepare test data
        List<String> goals = Arrays.asList("Lose weight", "Build muscle");
        
        doThrow(new RuntimeException("Invalid goal")).when(goalService).addUserGoal(eq(userId), anyList());
        
        ResponseEntity<String> response = userController.createGoal(userId, goals);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid goal", response.getBody());
    }

    // @Test
    // public void testGetAttendanceStatistics() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     LocalDate startDate = LocalDate.now().minusMonths(1);
    //     LocalDate endDate = LocalDate.now();
        
    //     Map<String, Object> statistics = new HashMap<>();
    //     statistics.put("totalSessions", 10);
    //     statistics.put("attendanceRate", 80.0);
        
    //     when(gymSessionService.getAttendanceStatistics(eq(sessionId), any(), any())).thenReturn(statistics);
        
    //     ResponseEntity<Map<String, Object>> response = userController.getAttendanceStatistics(sessionId, startDate, endDate);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(statistics, response.getBody());
    //     verify(gymSessionService).getAttendanceStatistics(eq(sessionId), eq(startDate), eq(endDate));
    // }    @Test
    public void testGetWaitlistStatus() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        
        Map<String, Object> status = new HashMap<>();
        status.put("position", 3);
        status.put("totalInWaitlist", 8);
        
        when(gymReservationService.getWaitlistStatus(userId, sessionId)).thenReturn(status);
        
        ResponseEntity<Map<String, Object>> response = userController.getWaitlistStatus(userId, sessionId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
        verify(gymReservationService).getWaitlistStatus(userId, sessionId);
    }
    
    @Test
    public void testCreateRecurringSessions() {
        // Prepare test data
        Map<String, Object> recurringData = new HashMap<>();
        recurringData.put("dayOfWeek", 2); // Martes
        recurringData.put("startTime", "10:00");
        recurringData.put("endTime", "11:00");
        recurringData.put("capacity", 15);
        recurringData.put("startDate", LocalDate.now().toString());
        recurringData.put("endDate", LocalDate.now().plusMonths(1).toString());
        recurringData.put("trainerId", UUID.randomUUID().toString());
        recurringData.put("description", "Recurring gym session");
        
        int sessionsCreated = 8;
        
        when(gymSessionService.configureRecurringSessions(
                anyInt(), any(LocalTime.class), any(LocalTime.class), anyInt(),
                any(Optional.class), any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sessionsCreated);
        
        ResponseEntity<Map<String, Object>> response = userController.createRecurringSessions(recurringData);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals(sessionsCreated, response.getBody().get("sessionsCreated"));
        assertEquals("Sesiones recurrentes creadas exitosamente", response.getBody().get("message"));
    }

    // @Test
    // public void testGetOccupancyStatistics() {
    //     // Prepare test data
    //     LocalDate startDate = LocalDate.now().minusMonths(1);
    //     LocalDate endDate = LocalDate.now();
        
    //     Map<String, Object> statistics = new HashMap<>();
    //     statistics.put("averageOccupancy", 75.5);
    //     statistics.put("peakOccupancy", 95.0);
        
    //     when(gymSessionService.getOccupancyStatistics(any(), any())).thenReturn(statistics);
        
    //     ResponseEntity<Map<String, Object>> response = userController.getOccupancyStatistics(startDate, endDate);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(statistics, response.getBody());
    //     verify(gymSessionService).getOccupancyStatistics(startDate, endDate);
    // }    @Test
    public void testGetSessionsByDate() {
        // Prepare test data
        LocalDate date = LocalDate.now();
        List<Object> sessions = new ArrayList<>();
        
        when(gymSessionService.getSessionsByDate(date)).thenReturn(sessions);
        
        ResponseEntity<List<Object>> response = userController.getSessionsByDate(date);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessions, response.getBody());
        verify(gymSessionService).getSessionsByDate(date);
    }
    
    @Test
    public void testUpdateSession() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("date", LocalDate.now().toString());
        sessionData.put("startTime", "14:00");
        sessionData.put("endTime", "15:00");
        sessionData.put("capacity", 20);
        sessionData.put("trainerId", UUID.randomUUID().toString());
        
        when(gymSessionService.updateSession(
                eq(sessionId), any(LocalDate.class), any(LocalTime.class), 
                any(LocalTime.class), anyInt(), any(UUID.class)))
                .thenReturn(true);
        
        ResponseEntity<Object> response = userController.updateSession(sessionId, sessionData);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Sesión actualizada exitosamente", responseBody.get("message"));
    }
    
    @Test
    public void testUpdateSession_Failure() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("date", LocalDate.now().toString());
        sessionData.put("startTime", "14:00");
        sessionData.put("endTime", "15:00");
        sessionData.put("capacity", 20);
        sessionData.put("trainerId", UUID.randomUUID().toString());
        
        when(gymSessionService.updateSession(
                eq(sessionId), any(LocalDate.class), any(LocalTime.class), 
                any(LocalTime.class), anyInt(), any(UUID.class)))
                .thenReturn(false);
        
        ResponseEntity<Object> response = userController.updateSession(sessionId, sessionData);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }    @Test
    public void testGetTrainerSessions() {
        // Prepare test data
        UUID trainerId = UUID.randomUUID();
        List<Object> sessions = new ArrayList<>();
        
        when(gymSessionService.getSessionsByTrainer(trainerId)).thenReturn(sessions);
        
        ResponseEntity<List<Object>> response = userController.getTrainerSessions(trainerId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessions, response.getBody());
        verify(gymSessionService).getSessionsByTrainer(trainerId);
    }
    
    @Test
    public void testCancelSession() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        Map<String, String> cancelData = new HashMap<>();
        cancelData.put("reason", "Maintenance");
        cancelData.put("trainerId", UUID.randomUUID().toString());
        
        when(gymSessionService.cancelSession(eq(sessionId), eq(cancelData.get("reason")), any(UUID.class)))
                .thenReturn(true);
        
        ResponseEntity<Object> response = userController.cancelSession(sessionId, cancelData);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Sesión cancelada exitosamente", responseBody.get("message"));
    }
    
    @Test
    public void testCancelSession_Failure() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        Map<String, String> cancelData = new HashMap<>();
        cancelData.put("reason", "Maintenance");
        cancelData.put("trainerId", UUID.randomUUID().toString());
        
        when(gymSessionService.cancelSession(eq(sessionId), eq(cancelData.get("reason")), any(UUID.class)))
                .thenReturn(false);
        
        ResponseEntity<Object> response = userController.cancelSession(sessionId, cancelData);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // @Test
    // public void testGetRegisteredStudents() {
    //     // Prepare test data
    //     UUID sessionId = UUID.randomUUID();
    //     List<Object> students = new ArrayList<>();
        
    //     when(gymSessionService.getRegisteredStudents(sessionId)).thenReturn(students);
        
    //     ResponseEntity<List<Object>> response = userController.getRegisteredStudents(sessionId);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals(students, response.getBody());
    //     verify(gymSessionService).getRegisteredStudents(sessionId);
    // }

    // @Test
    // public void testCreateGoal() {
    //     // Prepare test data
    //     UUID userId = UUID.randomUUID();
    //     List<String> goals = Arrays.asList("Lose weight", "Build muscle");
        
    //     doNothing().when(goalService).addUserGoal(eq(userId), anyList());
        
    //     ResponseEntity<String> response = userController.createGoal(userId, goals);
        
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    //     assertEquals("Goals updated and recommendations refreshed.", response.getBody());
    //     verify(goalService).addUserGoal(userId, goals);
    // }    @Test
    public void testRecordStudentAttendance() {
        // Prepare test data
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("userId", UUID.randomUUID().toString());
        attendanceData.put("reservationId", UUID.randomUUID().toString());
        attendanceData.put("attendanceTime", LocalDateTime.now().toString());
        
        when(userService.recordGymAttendance(any(UUID.class), any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(true);
        
        ResponseEntity<Map<String, Object>> response = userController.recordStudentAttendance(attendanceData);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals(true, response.getBody().get("success"));
        assertEquals("Asistencia registrada correctamente", response.getBody().get("message"));
    }
    
    @Test
    public void testRecordStudentAttendance_Failure() {
        // Prepare test data
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("userId", UUID.randomUUID().toString());
        attendanceData.put("reservationId", UUID.randomUUID().toString());
        
        when(userService.recordGymAttendance(any(UUID.class), any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(false);
        
        ResponseEntity<Map<String, Object>> response = userController.recordStudentAttendance(attendanceData);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("No se pudo registrar la asistencia", response.getBody().get("message"));
    }
    
    @Test
    public void testJoinWaitlist() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        
        when(gymReservationService.joinWaitlist(userId, sessionId)).thenReturn(true);
        Map<String, Object> status = new HashMap<>();
        status.put("position", 3);
        when(gymReservationService.getWaitlistStatus(userId, sessionId)).thenReturn(status);
        
        ResponseEntity<Object> response = userController.joinWaitlist(userId, sessionId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Has sido añadido a la lista de espera. Te notificaremos cuando haya cupo disponible.", 
                responseBody.get("message"));
        assertEquals(status, responseBody.get("status"));
    }
    
    @Test
    public void testJoinWaitlist_Failure() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        
        when(gymReservationService.joinWaitlist(userId, sessionId)).thenReturn(false);
        
        ResponseEntity<Object> response = userController.joinWaitlist(userId, sessionId);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testLeaveWaitlist() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        
        when(gymReservationService.leaveWaitlist(userId, sessionId)).thenReturn(true);
        
        ResponseEntity<Object> response = userController.leaveWaitlist(userId, sessionId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Has sido removido de la lista de espera exitosamente", responseBody.get("message"));
    }
    
    @Test
    public void testLeaveWaitlist_Failure() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        
        when(gymReservationService.leaveWaitlist(userId, sessionId)).thenReturn(false);
        
        ResponseEntity<Object> response = userController.leaveWaitlist(userId, sessionId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testGetAttendanceStatistics() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSessions", 10);
        statistics.put("attendanceRate", 80.0);
        
        when(gymSessionService.getTrainerAttendanceStatistics(eq(sessionId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(statistics);
        
        ResponseEntity<Map<String, Object>> response = userController.getAttendanceStatistics(sessionId, startDate, endDate);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statistics, response.getBody());
    }
    
    @Test
    public void testGetOccupancyStatistics() {
        // Prepare test data
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        Map<LocalDate, Integer> statistics = new HashMap<>();
        statistics.put(LocalDate.now(), 75);
        statistics.put(LocalDate.now().minusDays(1), 80);
        
        when(gymSessionService.getOccupancyStatistics(startDate, endDate)).thenReturn(statistics);
        
        ResponseEntity<Map<LocalDate, Integer>> response = userController.getOccupancyStatistics(startDate, endDate);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(statistics, response.getBody());
    }
    
    @Test
    public void testGetRegisteredStudents() {
        // Prepare test data
        UUID sessionId = UUID.randomUUID();
        List<Map<String, Object>> students = new ArrayList<>();
        
        Map<String, Object> student = new HashMap<>();
        student.put("userId", UUID.randomUUID());
        student.put("name", "John Doe");
        students.add(student);
        
        when(gymSessionService.getRegisteredStudentsForSession(sessionId)).thenReturn(students);
        
        ResponseEntity<List<Map<String, Object>>> response = userController.getRegisteredStudents(sessionId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }
    
    @Test
    public void testLambdaUpdateRoutine() {
        // This test covers the lambda function used in updateRoutine
        // We're testing the behavior that's already validated in testUpdateRoutine
        // but focusing on ensuring the lambda conversion works
        
        UUID routineId = UUID.randomUUID();
        RoutineDTO routineDTO = new RoutineDTO();
        routineDTO.setName("Lambda Test Routine");
        routineDTO.setDescription("Testing lambda function");
        
        Routine existingRoutine = new Routine();
        existingRoutine.setName("Old Name");
        existingRoutine.setDescription("Old Description");
        
        Routine updatedRoutine = new Routine();
        updatedRoutine.setName("Lambda Test Routine");
        updatedRoutine.setDescription("Testing lambda function");
        
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingRoutine));
        when(userService.updateRoutine(any(UUID.class), any(Routine.class))).thenReturn(updatedRoutine);
        
        ResponseEntity<Routine> response = userController.updateRoutine(routineId, routineDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRoutine, response.getBody());
        
        // Verify the lambda did the transformation correctly
        verify(userService).updateRoutine(eq(routineId), any(Routine.class));
    }

    @Test
    public void testLambdaCreateCustomRoutine() {
        // This test covers the lambda function used in createCustomRoutine
        // We're testing the behavior that's already validated in testCreateCustomRoutine
        // but focusing on ensuring the lambda conversion works
        
        UUID userId = UUID.randomUUID();
        RoutineDTO routineDTO = new RoutineDTO();
        routineDTO.setName("Lambda Test Custom Routine");
        routineDTO.setDescription("Testing lambda conversion");
        routineDTO.setExercises(new ArrayList<>());
        
        Routine createdRoutine = new Routine();
        createdRoutine.setName("Lambda Test Custom Routine");
        createdRoutine.setDescription("Testing lambda conversion");
        createdRoutine.setId(UUID.randomUUID());
        
        when(userService.createCustomRoutine(eq(userId), any(Routine.class))).thenReturn(createdRoutine);
        when(routineRepository.findById(any(UUID.class))).thenReturn(Optional.of(createdRoutine));
        
        ResponseEntity<Routine> response = userController.createCustomRoutine(userId, routineDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdRoutine, response.getBody());
        
        // Verify the lambda did the transformation correctly
        verify(userService).createCustomRoutine(eq(userId), any(Routine.class));
    }
    
    @Test
    public void testGetUserProgressReport() {
        // Prepare test data
        UUID userId = UUID.randomUUID();
        ReportFormat format = ReportFormat.PDF;
        byte[] mockReportData = "mock report data".getBytes();
        
        when(reportService.generateUserProgressReport(userId, format)).thenReturn(mockReportData);
        
        ResponseEntity<byte[]> response = userController.getUserProgressReport(userId, format);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReportData, response.getBody());
        
        // Verify the correct content type is set for PDF
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.APPLICATION_PDF, headers.getContentType());
        assertTrue(headers.getContentDisposition().toString().contains("attachment"));
        assertTrue(headers.getContentDisposition().toString().contains("user_progress_report.pdf"));
        
        verify(reportService).generateUserProgressReport(userId, format);
    }
    
    @Test
    public void testGetGymUsageReport() {
        // Prepare test data
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        ReportFormat format = ReportFormat.XLSX;
        byte[] mockReportData = "mock gym usage report data".getBytes();
        
        when(reportService.generateGymUsageReport(startDate, endDate, format)).thenReturn(mockReportData);
        
        ResponseEntity<byte[]> response = userController.getGymUsageReport(startDate, endDate, format);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReportData, response.getBody());
        
        // Verify the correct content type is set for XLSX
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), 
                     headers.getContentType());
        assertTrue(headers.getContentDisposition().toString().contains("attachment"));
        assertTrue(headers.getContentDisposition().toString().contains("gym_usage_report.xlsx"));
        
        verify(reportService).generateGymUsageReport(startDate, endDate, format);
    }
    
    @Test
    public void testGetAttendanceReport() {
        // Prepare test data
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        ReportFormat format = ReportFormat.CSV;
        byte[] mockReportData = "date,attendance\n2023-01-01,42".getBytes();
        
        when(reportService.getAttendanceStatistics(startDate, endDate, format)).thenReturn(mockReportData);
        
        ResponseEntity<byte[]> response = userController.getAttendanceReport(startDate, endDate, format);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReportData, response.getBody());
        
        // Verify the correct content type is set for CSV
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.parseMediaType("text/csv"), headers.getContentType());
        assertTrue(headers.getContentDisposition().toString().contains("attachment"));
        assertTrue(headers.getContentDisposition().toString().contains("attendance_report.csv"));
        
        verify(reportService).getAttendanceStatistics(startDate, endDate, format);
    }
    
    @Test
    public void testBuildResponseWithJSON() {
        // Use reflection to access the private method
        ReportFormat format = ReportFormat.JSON;
        byte[] content = "{\"data\": \"test\"}".getBytes();
        String filenameBase = "test_report";
        
        // Create a method that directly invokes buildResponse using reflection
        ResponseEntity<byte[]> response = null;
        try {
            java.lang.reflect.Method buildResponseMethod = UserController.class.getDeclaredMethod(
                "buildResponse", byte[].class, ReportFormat.class, String.class);
            buildResponseMethod.setAccessible(true);
            response = (ResponseEntity<byte[]>) buildResponseMethod.invoke(userController, content, format, filenameBase);
        } catch (Exception e) {
            fail("Failed to invoke buildResponse method: " + e.getMessage());
        }
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(content, response.getBody());
        
        // Verify JSON content type
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertTrue(headers.getContentDisposition().toString().contains("attachment"));
        assertTrue(headers.getContentDisposition().toString().contains("test_report.json"));
    }
}