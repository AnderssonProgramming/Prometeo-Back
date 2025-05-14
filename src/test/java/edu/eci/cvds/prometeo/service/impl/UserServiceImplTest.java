package edu.eci.cvds.prometeo.service.impl;


import edu.eci.cvds.prometeo.dto.UserDTO;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.*;
import edu.eci.cvds.prometeo.service.PhysicalProgressService;
import edu.eci.cvds.prometeo.service.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;






@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PhysicalProgressRepository physicalProgressRepository;
    
    @Mock
    private RoutineRepository routineRepository;
    
    @Mock
    private RecommendationRepository recommendationRepository;
    
    @Mock
    private EquipmentRepository equipmentRepository;
    
    @Mock
    private GymSessionRepository gymSessionRepository;
    
    @Mock
    private ReservationRepository reservationRepository;
    
    @Mock
    private PhysicalProgressService physicalProgressService;
    
    @Mock
    private RoutineService routineService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private UUID userId;
    private String institutionalId;
    private PhysicalProgress testPhysicalProgress;
    private Routine testRoutine;
    private GymSession testGymSession;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        institutionalId = "test123";
        
        // Setup test user
        testUser = new User();
        testUser.setId(userId);
        testUser.setInstitutionalId(institutionalId);
        testUser.setName("Test User");
        testUser.setWeight(70.0);
        testUser.setHeight(175.0);
        testUser.setRole("STUDENT");

        // Setup test user DTO
        testUserDTO = new UserDTO();
        testUserDTO.setInstitutionalId(institutionalId);
        testUserDTO.setName("Test User");
        testUserDTO.setWeight(70.0);
        testUserDTO.setHeight(175.0);
        testUserDTO.setRole("STUDENT");

        // Setup test physical progress
        testPhysicalProgress = new PhysicalProgress();
        testPhysicalProgress.setId(UUID.randomUUID());
        testPhysicalProgress.setUserId(userId);
        
        // Setup test routine
        testRoutine = new Routine();
        testRoutine.setId(UUID.randomUUID());
        testRoutine.setName("Test Routine");
        testRoutine.setDescription("Test Description");
        
        // Setup test gym session
        testGymSession = new GymSession();
        testGymSession.setId(UUID.randomUUID());
        testGymSession.setSessionDate(LocalDate.now());
        testGymSession.setStartTime(LocalTime.of(9, 0));
        testGymSession.setEndTime(LocalTime.of(10, 0));
        testGymSession.setCapacity(20);
        testGymSession.setReservedSpots(10);
        
        // Setup test reservation
        testReservation = new Reservation();
        testReservation.setId(UUID.randomUUID());
        testReservation.setUserId(userId);
        testReservation.setSessionId(testGymSession.getId());
        testReservation.setReservationDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)));
        testReservation.setStatus(ReservationStatus.CONFIRMED);
    }

    // --------- Basic User Operations Tests ---------
    
    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findByInstitutionalId(institutionalId)).thenReturn(Optional.of(testUser));
        
        User result = userService.getUserById(institutionalId);
        
        assertNotNull(result);
        assertEquals(institutionalId, result.getInstitutionalId());
        verify(userRepository).findByInstitutionalId(institutionalId);
    }
    
    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByInstitutionalId(institutionalId)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.getUserById(institutionalId));
        
        assertTrue(exception.getMessage().contains("not found"));
    }
    
    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> userList = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(userList);
        
        List<User> result = userService.getAllUsers();
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }
    
    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() {
        String role = "STUDENT";
        List<User> userList = Arrays.asList(testUser);
        when(userRepository.findByRole(role)).thenReturn(userList);
        
        List<User> result = userService.getUsersByRole(role);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
        verify(userRepository).findByRole(role);
    }
    
    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.createUser(testUserDTO);
        
        assertNotNull(result);
        assertEquals(institutionalId, result.getInstitutionalId());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setName("Updated Name");
        updatedDTO.setWeight(75.0);
        updatedDTO.setHeight(180.0);
        updatedDTO.setRole("STUDENT");
        
        when(userRepository.findByInstitutionalId(institutionalId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.updateUser(institutionalId, updatedDTO);
        
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals(75.0, result.getWeight());
        assertEquals(180.0, result.getHeight());
        verify(userRepository).findByInstitutionalId(institutionalId);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void deleteUser_ShouldDeleteAndReturnUser() {
        when(userRepository.findByInstitutionalId(institutionalId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));
        
        User result = userService.deleteUser(institutionalId);
        
        assertNotNull(result);
        assertEquals(institutionalId, result.getInstitutionalId());
        verify(userRepository).findByInstitutionalId(institutionalId);
        verify(userRepository).delete(any(User.class));
    }
    
    // --------- Physical Progress Tests ---------
    
    @Test
    void recordPhysicalMeasurement_ShouldRecordAndReturnMeasurement() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(routineRepository.findCurrentRoutineByUserId(userId)).thenReturn(Optional.of(testRoutine));
        when(physicalProgressService.recordMeasurement(eq(userId), any(PhysicalProgress.class))).thenReturn(testPhysicalProgress);
        
        PhysicalProgress result = userService.recordPhysicalMeasurement(userId, new PhysicalProgress());
        
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userRepository).findById(userId);
        verify(routineRepository).findCurrentRoutineByUserId(userId);
        verify(physicalProgressService).recordMeasurement(eq(userId), any(PhysicalProgress.class));
    }
    
    @Test
    void getPhysicalMeasurementHistory_ShouldReturnMeasurements() {
        List<PhysicalProgress> progressList = Arrays.asList(testPhysicalProgress);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(physicalProgressService.getMeasurementHistory(eq(userId), any(), any())).thenReturn(progressList);
        
        List<PhysicalProgress> result = userService.getPhysicalMeasurementHistory(
                userId, Optional.empty(), Optional.empty());
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(physicalProgressService).getMeasurementHistory(eq(userId), any(), any());
    }
    
    // --------- Routine Management Tests ---------
    
    @Test
    void getUserRoutines_ShouldReturnRoutines() {
        List<Routine> routineList = Arrays.asList(testRoutine);
        when(routineService.getUserRoutines(userId, false)).thenReturn(routineList);
        
        List<Routine> result = userService.getUserRoutines(userId);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(routineService).getUserRoutines(userId, false);
    }
      @Test
    void assignRoutineToUser_ShouldAssignRoutine() {
        UUID routineId = testRoutine.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        UserRoutine mockUserRoutine = new UserRoutine();
        when(routineService.assignRoutineToUser(eq(routineId), eq(userId), isNull(), 
                any(Optional.class), any(Optional.class))).thenReturn(mockUserRoutine);
        
        userService.assignRoutineToUser(userId, routineId);
        
        verify(userRepository).findById(userId);
        verify(routineService).assignRoutineToUser(eq(routineId), eq(userId), isNull(), 
                any(Optional.class), any(Optional.class));
    }
    
    // --------- Gym Reservation Tests ---------
    
    @Test
    void createGymReservation_ShouldCreateAndReturnReservation() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                eq(date), eq(startTime), eq(endTime))).thenReturn(Optional.of(testGymSession));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        
        UUID result = userService.createGymReservation(userId, date, startTime, endTime, Optional.empty());
          assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(gymSessionRepository, atLeastOnce()).findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                eq(date), eq(startTime), eq(endTime));
        verify(gymSessionRepository).save(any(GymSession.class));
        verify(reservationRepository).save(any(Reservation.class));
    }
    
    @Test
    void cancelGymReservation_ShouldCancelReservation() {
        UUID reservationId = testReservation.getId();
        Optional<String> reason = Optional.of("Test cancellation reason");
        
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(testReservation));
        when(gymSessionRepository.findById(testGymSession.getId())).thenReturn(Optional.of(testGymSession));
        
        boolean result = userService.cancelGymReservation(reservationId, userId, reason);
        
        assertTrue(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(testGymSession.getId());
        verify(gymSessionRepository).save(any(GymSession.class));
        verify(reservationRepository).save(any(Reservation.class));
    }
    
    @Test
    void getUpcomingReservations_ShouldReturnUpcomingReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        
        when(reservationRepository.findByUserIdAndReservationDateGreaterThanEqualAndStatusOrderByReservationDateAsc(
                eq(userId), any(LocalDateTime.class), eq(ReservationStatus.CONFIRMED))).thenReturn(reservations);
        when(gymSessionRepository.findById(testGymSession.getId())).thenReturn(Optional.of(testGymSession));
        
        List<Object> result = userService.getUpcomingReservations(userId);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(reservationRepository).findByUserIdAndReservationDateGreaterThanEqualAndStatusOrderByReservationDateAsc(
                eq(userId), any(LocalDateTime.class), eq(ReservationStatus.CONFIRMED));
    }
    
    @Test
    void checkGymAvailability_ShouldReturnTrue_WhenSessionAvailable() {
        LocalDate date = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(10, 0);
        
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                eq(date), eq(startTime), eq(endTime))).thenReturn(Optional.of(testGymSession));
        
        boolean result = userService.checkGymAvailability(date, startTime, endTime);
        
        assertTrue(result);
        verify(gymSessionRepository).findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                eq(date), eq(startTime), eq(endTime));
    }
    
    @Test
    void recordGymAttendance_ShouldRecordAttendance() {
        UUID reservationId = testReservation.getId();
        LocalDateTime attendanceTime = LocalDateTime.of(testReservation.getReservationDate().toLocalDate(), 
                testGymSession.getStartTime().plusMinutes(5));
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(testReservation));
        when(gymSessionRepository.findById(testGymSession.getId())).thenReturn(Optional.of(testGymSession));
        
        boolean result = userService.recordGymAttendance(userId, reservationId, attendanceTime);
        
        assertTrue(result);
        verify(userRepository).findById(userId);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(testGymSession.getId());
        verify(reservationRepository).save(any(Reservation.class));
    }
    
    // --------- Additional Tests for Coverage ---------
    
    @Test
    void getAvailableTimeSlots_ShouldReturnTimeSlots() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalTime openingTime = LocalTime.of(6, 0);
        LocalTime closingTime = LocalTime.of(22, 0);
        
        // Create some existing sessions that occupy time slots
        GymSession morning = new GymSession();
        morning.setSessionDate(date);
        morning.setStartTime(LocalTime.of(8, 0));
        morning.setEndTime(LocalTime.of(10, 0));
        morning.setCapacity(20);
        morning.setReservedSpots(10); // Half-full session
        
        GymSession afternoon = new GymSession();
        afternoon.setSessionDate(date);
        afternoon.setStartTime(LocalTime.of(14, 0));
        afternoon.setEndTime(LocalTime.of(16, 0));
        afternoon.setCapacity(20);
        afternoon.setReservedSpots(10); // Half-full session
        
        List<GymSession> existingSessions = Arrays.asList(morning, afternoon);
        
        // Fix to match actual implementation in UserServiceImpl
        when(gymSessionRepository.findBySessionDateOrderByStartTime(date)).thenReturn(existingSessions);
        
        // Act
        List<Object> result = userService.getAvailableTimeSlots(date);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Both sessions have space available
        verify(gymSessionRepository).findBySessionDateOrderByStartTime(date);
    }
    
    @Test
    void createCustomRoutine_ShouldCreateAndReturnRoutine() {
        // Arrange
        UUID userId = testUser.getId();
        Routine customRoutine = new Routine();
        customRoutine.setName("Custom Routine");
        customRoutine.setDescription("Custom Description");
        customRoutine.setDifficulty("Intermediate");
        customRoutine.setGoal("Muscle Building");
        
        Routine createdRoutine = new Routine(); // Create a new instance to return
        createdRoutine.setId(UUID.randomUUID());
        createdRoutine.setName("Custom Routine");
        createdRoutine.setDescription("Custom Description");
        createdRoutine.setDifficulty("Intermediate");
        createdRoutine.setGoal("Muscle Building");
        createdRoutine.setTrainerId(userId); // This simulates what should happen
        
        // Allow multiple calls to findById since the implementation calls it directly 
        // and again through assignRoutineToUser
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(routineService.createRoutine(any(Routine.class), any())).thenReturn(createdRoutine);
        
        // We need to mock the assignRoutineToUser method since it's called within createCustomRoutine
        UserRoutine mockUserRoutine = new UserRoutine();
        when(routineService.assignRoutineToUser(any(UUID.class), eq(userId), isNull(), 
                any(Optional.class), any(Optional.class))).thenReturn(mockUserRoutine);
        
        // Act
        Routine result = userService.createCustomRoutine(userId, customRoutine);
        
        // Assert
        assertNotNull(result);
        assertEquals("Custom Routine", result.getName());
        assertEquals(userId, result.getTrainerId());
        
        // Verify findById is called at least once (it's actually called twice)
        verify(userRepository, atLeastOnce()).findById(userId);
        verify(routineService).createRoutine(any(Routine.class), any());
    }
    
    @Test
    void getReservationHistory_ShouldReturnReservations() {
        // Arrange
        UUID userId = testUser.getId();
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<Reservation> reservations = Arrays.asList(testReservation);
        
        when(reservationRepository.findByUserIdAndReservationDateBetweenOrderByReservationDateDesc(
                any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(reservations);
        when(gymSessionRepository.findById(testGymSession.getId())).thenReturn(Optional.of(testGymSession));
        
        // Act
        List<Object> result = userService.getReservationHistory(userId, 
                Optional.of(startDate), Optional.of(endDate));
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(reservationRepository).findByUserIdAndReservationDateBetweenOrderByReservationDateDesc(
                any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(gymSessionRepository).findById(testGymSession.getId());
    }
    
    @Test
    void getRecommendedRoutines_ShouldReturnRecommendedRoutines() {
        // Arrange
        UUID userId = testUser.getId();
        
        // Create a proper recommendation with direct user and routine references
        Recommendation recommendation = new Recommendation();
        recommendation.setId(UUID.randomUUID());
        recommendation.setUser(testUser);
        recommendation.setRoutine(testRoutine);
        recommendation.setActive(true);
        recommendation.setWeight(5);
        
        List<Recommendation> recommendations = Arrays.asList(recommendation);
        
        // Need to mock user repository first to avoid NPE
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(recommendationRepository.findByUserIdAndActive(userId, true)).thenReturn(recommendations);
        // The findById mock is not needed as the recommendation already has the routine attached
        
        // Act
        List<Routine> result = userService.getRecommendedRoutines(userId);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(recommendationRepository).findByUserIdAndActive(userId, true);
    }
    
    @Test
    void getUserByInstitutionalId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByInstitutionalId(institutionalId)).thenReturn(Optional.of(testUser));
        
        // Act
        User result = userService.getUserByInstitutionalId(institutionalId);
        
        // Assert
        assertNotNull(result);
        assertEquals(institutionalId, result.getInstitutionalId());
        verify(userRepository).findByInstitutionalId(institutionalId);
    }
    
    @Test
    void logRoutineProgress_ShouldLogProgress() {
        // Arrange
        UUID userId = testUser.getId();
        UUID routineId = testRoutine.getId();
        int completionPercentage = 75;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Act
        boolean result = userService.logRoutineProgress(userId, routineId, completionPercentage);
        
        // Assert
        assertTrue(result);
        verify(userRepository).findById(userId);
    }
    
    @Test
    void updateRoutine_ShouldUpdateRoutine() {
        // Arrange
        UUID routineId = testRoutine.getId();
        Routine updatedRoutine = new Routine();
        updatedRoutine.setId(routineId);
        updatedRoutine.setName("Updated Routine");
        updatedRoutine.setDescription("Updated Description");
        
        when(routineService.updateRoutine(any(UUID.class), any(Routine.class), isNull())).thenReturn(updatedRoutine);
        
        // Act
        Routine result = userService.updateRoutine(routineId, updatedRoutine);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Routine", result.getName());
        verify(routineService).updateRoutine(any(UUID.class), any(Routine.class), isNull());
    }
    
    @Test
    void setPhysicalGoal_ShouldSetGoalAndReturnProgress() {
        // Arrange
        UUID userId = testUser.getId();
        String goal = "Lose weight";
        PhysicalProgress updatedProgress = new PhysicalProgress();
        updatedProgress.setId(UUID.randomUUID());
        updatedProgress.setUserId(userId);
        
        when(physicalProgressService.setGoal(userId, goal)).thenReturn(updatedProgress);
        
        // Act
        PhysicalProgress result = userService.setPhysicalGoal(userId, goal);
        
        // Assert
        assertNotNull(result);
        verify(physicalProgressService).setGoal(userId, goal);
    }
    
    @Test
    void updatePhysicalMeasurement_ShouldUpdateAndReturnProgress() {
        // Arrange
        UUID progressId = UUID.randomUUID();
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(180.0);
        
        PhysicalProgress updatedProgress = new PhysicalProgress();
        updatedProgress.setId(progressId);
        updatedProgress.setMeasurements(measurements);
        
        when(physicalProgressService.updateMeasurement(progressId, measurements)).thenReturn(updatedProgress);
        
        // Act
        PhysicalProgress result = userService.updatePhysicalMeasurement(progressId, measurements);
        
        // Assert
        assertNotNull(result);
        assertEquals(measurements.getHeight(), result.getMeasurements().getHeight());
        verify(physicalProgressService).updateMeasurement(progressId, measurements);
    }
    
    @Test
    void calculatePhysicalProgressMetrics_ShouldReturnMetrics() {
        // Arrange
        UUID userId = testUser.getId();
        int months = 3;
        Map<String, Double> expectedMetrics = new HashMap<>();
        expectedMetrics.put("weightChange", -5.0);
        expectedMetrics.put("bmiChange", -1.5);
        
        when(physicalProgressService.calculateProgressMetrics(userId, months)).thenReturn(expectedMetrics);
        
        // Act
        Map<String, Double> result = userService.calculatePhysicalProgressMetrics(userId, months);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedMetrics.size(), result.size());
        assertEquals(expectedMetrics.get("weightChange"), result.get("weightChange"));
        assertEquals(expectedMetrics.get("bmiChange"), result.get("bmiChange"));
        verify(physicalProgressService).calculateProgressMetrics(userId, months);
    }
    
    @Test
    void getLatestPhysicalMeasurement_ShouldReturnLatestMeasurement() {
        // Arrange
        UUID userId = testUser.getId();
        PhysicalProgress latestProgress = new PhysicalProgress();
        latestProgress.setId(UUID.randomUUID());
        latestProgress.setUserId(userId);
        
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(180.0);
        latestProgress.setMeasurements(measurements);
        
        when(physicalProgressService.getLatestMeasurement(userId)).thenReturn(Optional.of(latestProgress));
        
        // Act
        Optional<PhysicalProgress> result = userService.getLatestPhysicalMeasurement(userId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(latestProgress.getId(), result.get().getId());
        verify(physicalProgressService).getLatestMeasurement(userId);
    }
    
    @Test
    void getLatestPhysicalMeasurement_ShouldReturnEmpty_WhenNoMeasurementExists() {
        // Arrange
        UUID userId = testUser.getId();
        when(physicalProgressService.getLatestMeasurement(userId)).thenReturn(Optional.empty());
        
        // Act
        Optional<PhysicalProgress> result = userService.getLatestPhysicalMeasurement(userId);
        
        // Assert
        assertFalse(result.isPresent());
        verify(physicalProgressService).getLatestMeasurement(userId);
    }
}