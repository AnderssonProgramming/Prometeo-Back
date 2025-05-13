package edu.eci.cvds.prometeo.service.impl;


import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



public class GymSessionServiceImplTest {

    @Mock
    private GymSessionRepository gymSessionRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GymSessionServiceImpl gymSessionService;

    private UUID sessionId;
    private UUID trainerId;
    private UUID userId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private GymSession testSession;
    private User testTrainer;
    private User testUser;

    @BeforeEach
    public void setUp() {
        sessionId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        sessionDate = LocalDate.now();
        startTime = LocalTime.of(10, 0);
        endTime = LocalTime.of(11, 0);

        // Set up test session
        testSession = new GymSession();
        testSession.setId(sessionId);
        testSession.setSessionDate(sessionDate);
        testSession.setStartTime(startTime);
        testSession.setEndTime(endTime);
        testSession.setCapacity(10);
        testSession.setReservedSpots(5);
        testSession.setTrainerId(trainerId);

        // Set up test trainer
        testTrainer = new User();
        testTrainer.setId(trainerId);
        testTrainer.setName("Test Trainer");

        // Set up test user
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setInstitutionalId("12345");
    }

    @Test
    public void testCreateSession_Success() {
        // Arrange
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Optional.empty());
        when(gymSessionRepository.save(any(GymSession.class))).thenReturn(testSession);

        // Act
        UUID result = gymSessionService.createSession(sessionDate, startTime, endTime, 10, Optional.empty(), trainerId);

        // Assert
        assertEquals(sessionId, result);
        verify(gymSessionRepository).save(any(GymSession.class));
    }

    @Test
    public void testCreateSession_OverlappingSession_ThrowsException() {
        // Arrange
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Optional.of(testSession));

        // Act - should throw exception
        gymSessionService.createSession(sessionDate, startTime, endTime, 10, Optional.empty(), trainerId);
    }

    @Test
    public void testUpdateSession_Success() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Optional.of(testSession));

        // Act
        boolean result = gymSessionService.updateSession(sessionId, sessionDate, startTime, endTime, 15, trainerId);

        // Assert
        assertTrue(result);
        verify(gymSessionRepository).save(any(GymSession.class));
    }

    @Test
    public void testUpdateSession_SessionNotFound_ThrowsException() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act - should throw exception
        gymSessionService.updateSession(sessionId, sessionDate, startTime, endTime, 15, trainerId);
    }

    @Test
    public void testUpdateSession_OverlappingSession_ThrowsException() {
        // Arrange
        GymSession otherSession = new GymSession();
        otherSession.setId(UUID.randomUUID());
        
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Optional.of(otherSession));

        // Act - should throw exception
        gymSessionService.updateSession(sessionId, sessionDate, startTime, endTime, 15, trainerId);
    }

    @Test
    public void testCancelSession_Success() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        // Act
        boolean result = gymSessionService.cancelSession(sessionId, "Testing cancellation", trainerId);

        // Assert
        assertTrue(result);
        verify(gymSessionRepository).delete(testSession);
    }

    @Test
    public void testCancelSession_SessionNotFound_ThrowsException() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act - should throw exception
        gymSessionService.cancelSession(sessionId, "Testing cancellation", trainerId);
    }

    @Test
    public void testGetSessionsByDate_ReturnsSessionList() {
        // Arrange
        List<GymSession> sessions = Collections.singletonList(testSession);
        when(gymSessionRepository.findBySessionDateOrderByStartTime(sessionDate)).thenReturn(sessions);

        // Act
        List<Object> result = gymSessionService.getSessionsByDate(sessionDate);

        // Assert
        assertEquals(1, result.size());
        Map<String, Object> sessionMap = (Map<String, Object>) result.get(0);
        assertEquals(sessionId, sessionMap.get("id"));
        assertEquals(sessionDate, sessionMap.get("date"));
    }

    @Test
    public void testGetSessionsByTrainer_ReturnsSessionList() {
        // Arrange
        List<GymSession> sessions = Collections.singletonList(testSession);
        when(gymSessionRepository.findBySessionDateAndTrainerId(any(LocalDate.class), eq(trainerId)))
                .thenReturn(sessions);

        // Act
        List<Object> result = gymSessionService.getSessionsByTrainer(trainerId);

        // Assert
        assertEquals(1, result.size());
        Map<String, Object> sessionMap = (Map<String, Object>) result.get(0);
        assertEquals(sessionId, sessionMap.get("id"));
        assertEquals(trainerId, sessionMap.get("trainerId"));
    }

    @Test
    public void testGetAvailableTimeSlots_ReturnsAvailableSlots() {
        // Arrange
        List<GymSession> sessions = Collections.singletonList(testSession);
        when(gymSessionRepository.findBySessionDateOrderByStartTime(sessionDate)).thenReturn(sessions);

        // Act
        List<Map<String, Object>> result = gymSessionService.getAvailableTimeSlots(sessionDate);

        // Assert
        assertEquals(1, result.size());
        Map<String, Object> slotMap = result.get(0);
        assertEquals(sessionId, slotMap.get("sessionId"));
        assertEquals(5, slotMap.get("availableSpots"));
    }

    @Test
    public void testConfigureRecurringSessions_CreatesMultipleSessions() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1); // Sunday
        LocalDate endDate = LocalDate.of(2023, 1, 15);
        int dayOfWeek = 1; // Monday

        when(gymSessionRepository.findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Optional.empty());
        when(gymSessionRepository.save(any(GymSession.class))).thenReturn(testSession);

        // Act
        int sessionCount = gymSessionService.configureRecurringSessions(
                dayOfWeek, startTime, endTime, 10, Optional.empty(), trainerId, startDate, endDate);

        // Assert - should create 2 Monday sessions (Jan 2 and Jan 9)
        assertEquals(2, sessionCount);
        verify(gymSessionRepository, times(2)).save(any(GymSession.class));
    }

    @Test
    public void testGetOccupancyStatistics_CalculatesCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);
        
        List<GymSession> sessions = new ArrayList<>();
        sessions.add(testSession);
        
        GymSession session2 = new GymSession();
        session2.setId(UUID.randomUUID());
        session2.setSessionDate(startDate.plusDays(1));
        session2.setCapacity(20);
        session2.setReservedSpots(10);
        sessions.add(session2);
        
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessions);

        // Act
        Map<LocalDate, Integer> stats = gymSessionService.getOccupancyStatistics(startDate, endDate);

        // Assert
        assertEquals(2, stats.size());
        assertEquals(Integer.valueOf(50), stats.get(sessionDate)); // 5/10 = 50%
        assertEquals(Integer.valueOf(50), stats.get(startDate.plusDays(1))); // 10/20 = 50%
    }

    @Test
    public void testGetRegisteredStudentsForSession_ReturnsStudentsList() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setUserId(userId);
        reservation.setSessionId(sessionId);
        reservation.setStatus("CONFIRMED");
        reservation.setAttended(true);
        
        List<Reservation> reservations = Collections.singletonList(reservation);
        when(reservationRepository.findBySessionId(sessionId)).thenReturn(reservations);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        List<Map<String, Object>> result = gymSessionService.getRegisteredStudentsForSession(sessionId);

        // Assert
        assertEquals(1, result.size());
        Map<String, Object> studentInfo = result.get(0);
        assertEquals(userId, studentInfo.get("userId"));
        assertEquals("Test User", studentInfo.get("name"));
        assertEquals("12345", studentInfo.get("institutionalId"));
        assertEquals(true, studentInfo.get("attended"));
    }

    @Test
    public void testGetRegisteredStudentsForSession_SessionNotFound_ThrowsException() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act - should throw exception
        gymSessionService.getRegisteredStudentsForSession(sessionId);
    }

    @Test
    public void testGetTrainerAttendanceStatistics_CalculatesCorrectly() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(5);
        
        List<GymSession> sessions = Collections.singletonList(testSession);
        when(gymSessionRepository.findByTrainerIdAndSessionDateBetween(trainerId, startDate, endDate))
                .thenReturn(sessions);
        
        Reservation reservation1 = new Reservation();
        reservation1.setId(UUID.randomUUID());
        reservation1.setAttended(true);
        
        Reservation reservation2 = new Reservation();
        reservation2.setId(UUID.randomUUID());
        reservation2.setAttended(false);
        
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        when(reservationRepository.findBySessionId(sessionId)).thenReturn(reservations);

        // Act
        Map<String, Object> stats = gymSessionService.getTrainerAttendanceStatistics(trainerId, startDate, endDate);

        // Assert
        assertEquals(1, stats.get("totalSessions"));
        assertEquals(10, stats.get("totalCapacity"));
        assertEquals(5, stats.get("reservedSpots"));
        assertEquals(1, stats.get("totalAttendance"));
        assertEquals(50.0, stats.get("occupancyRate"));
        assertEquals(20.0, stats.get("attendanceRate"));
    }

    @Test
    public void testGetSessionById_ReturnsSessionWithTrainer() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));

        // Act
        Map<String, Object> result = (Map<String, Object>) gymSessionService.getSessionById(sessionId);

        // Assert
        assertEquals(sessionId, result.get("id"));
        assertEquals(sessionDate, result.get("date"));
        assertEquals(startTime, result.get("startTime"));
        assertEquals(endTime, result.get("endTime"));
        
        Map<String, Object> trainerInfo = (Map<String, Object>) result.get("trainer");
        assertNotNull(trainerInfo);
        assertEquals(trainerId, trainerInfo.get("id"));
        assertEquals("Test Trainer", trainerInfo.get("name"));
    }

    @Test
    public void testGetSessionById_SessionNotFound_ThrowsException() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act - should throw exception
        gymSessionService.getSessionById(sessionId);
    }
}