package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.NotificationService;
import edu.eci.cvds.prometeo.service.WaitlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






@ExtendWith(MockitoExtension.class)
public class GymReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private GymSessionRepository gymSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private WaitlistService waitlistService;

    @InjectMocks
    private GymReservationServiceImpl reservationService;

    private UUID userId;
    private UUID sessionId;
    private UUID reservationId;
    private GymSession gymSession;
    private Reservation reservation;
    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        reservationId = UUID.randomUUID();

        // Setup GymSession
        gymSession = new GymSession();
        gymSession.setId(sessionId);
        gymSession.setSessionDate(LocalDate.now().plusDays(1));
        gymSession.setStartTime(LocalTime.of(10, 0));
        gymSession.setEndTime(LocalTime.of(11, 0));
        gymSession.setCapacity(10);
        gymSession.setReservedSpots(5);
        gymSession.setTrainerId(UUID.randomUUID());

        // Setup Reservation
        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setUserId(userId);
        reservation.setSessionId(sessionId);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setEquipmentIds(new ArrayList<>());
        reservation.setNotes("Test reservation");

        // Setup ReservationDTO
        reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservationId);
        reservationDTO.setUserId(userId);
        reservationDTO.setSessionId(sessionId);
        reservationDTO.setStatus(ReservationStatus.CONFIRMED);
        reservationDTO.setReservationDate(LocalDateTime.now());
        reservationDTO.setEquipmentIds(new ArrayList<>());
        reservationDTO.setNotes("Test reservation");
    }

    @Test
    void getAll_ShouldReturnAllReservations() {
        // Given
        when(reservationRepository.findAll()).thenReturn(Collections.singletonList(reservation));

        // When
        List<ReservationDTO> result = reservationService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository).findAll();
    }

    @Test
    void getByUserId_ShouldReturnUserReservations() {
        // Given
        when(reservationRepository.findByUserId(userId)).thenReturn(Collections.singletonList(reservation));

        // When
        List<ReservationDTO> result = reservationService.getByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository).findByUserId(userId);
    }

    @Test
    void getById_WhenReservationExists_ShouldReturnReservation() {
        // Given
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When
        Optional<ReservationDTO> result = reservationService.getById(reservationId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(reservationId, result.get().getId());
        verify(reservationRepository).findById(reservationId);
    }

    @Test
    void getById_WhenReservationDoesNotExist_ShouldReturnEmpty() {
        // Given
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // When
        Optional<ReservationDTO> result = reservationService.getById(reservationId);

        // Then
        assertFalse(result.isPresent());
        verify(reservationRepository).findById(reservationId);
    }

    @Test
    void create_WhenValidData_ShouldCreateReservation() {
        // Given
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(reservationRepository.countByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        doNothing().when(notificationService).sendReservationConfirmation(userId, reservationId);

        // When
        ReservationDTO result = reservationService.create(reservationDTO);

        // Then
        assertNotNull(result);
        assertEquals(reservationId, result.getId());
        verify(gymSessionRepository).findById(sessionId);
        verify(userRepository).existsById(userId);
        verify(reservationRepository).countByUserIdAndStatusIn(eq(userId), anyList());
        verify(reservationRepository).save(any(Reservation.class));
        verify(notificationService).sendReservationConfirmation(userId, reservationId);
    }

    @Test
    void create_WhenSessionNotExists_ShouldThrowException() {
        // Given
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.create(reservationDTO);
        });
        assertEquals(PrometeoExceptions.NO_EXISTE_SESION, exception.getMessage());
    }

    @Test
    void create_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        when(userRepository.existsById(userId)).thenReturn(false);

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.create(reservationDTO);
        });
        assertEquals(PrometeoExceptions.NO_EXISTE_USUARIO, exception.getMessage());
    }

    @Test
    void create_WhenNoCapacity_ShouldThrowException() {
        // Given
        gymSession.setReservedSpots(gymSession.getCapacity()); // Full capacity
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        when(userRepository.existsById(userId)).thenReturn(true);

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.create(reservationDTO);
        });
        assertEquals(PrometeoExceptions.CAPACIDAD_EXCEDIDA, exception.getMessage());
    }

    @Test
    void create_WhenUserHasTooManyReservations_ShouldThrowException() {
        // Given
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(reservationRepository.countByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(5L);

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.create(reservationDTO);
        });
        assertEquals(PrometeoExceptions.LIMITE_RESERVAS_ALCANZADO, exception.getMessage());
    }

    @Test
    void delete_WhenValidReservation_ShouldCancelReservation() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        gymSession.setSessionDate(futureTime.toLocalDate());
        gymSession.setStartTime(futureTime.toLocalTime());

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));

        // When
        reservationService.delete(reservationId);

        // Then
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(sessionId);
        verify(gymSessionRepository).save(gymSession);
        verify(waitlistService).notifyNextInWaitlist(sessionId);
        verify(reservationRepository).save(reservation);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertNotNull(reservation.getCancellationDate());
    }

    @Test
    void delete_WhenReservationNotExists_ShouldThrowException() {
        // Given
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.delete(reservationId);
        });
        assertEquals(PrometeoExceptions.NO_EXISTE_RESERVA, exception.getMessage());
    }

    @Test
    void delete_WhenReservationAlreadyCancelled_ShouldThrowException() {
        // Given
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.delete(reservationId);
        });
        assertEquals(PrometeoExceptions.RESERVA_YA_CANCELADA, exception.getMessage());
    }

    @Test
    void getAvailability_ShouldReturnAvailableSessions() {
        // Given
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 30);
        
        when(gymSessionRepository.findBySessionDate(date)).thenReturn(Collections.singletonList(gymSession));

        // When
        Map<String, Object> result = reservationService.getAvailability(date, time);

        // Then
        assertNotNull(result);
        assertEquals(date, result.get("date"));
        assertEquals(time, result.get("requestedTime"));
        assertNotNull(result.get("availableSessions"));
        
        List<?> availableSessions = (List<?>) result.get("availableSessions");
        assertEquals(1, availableSessions.size());
        
        verify(gymSessionRepository).findBySessionDate(date);
    }

    @Test
    void joinWaitlist_WhenValidAndFull_ShouldAddToWaitlist() {
        // Given
        gymSession.setReservedSpots(gymSession.getCapacity()); // Full capacity
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        when(userRepository.existsById(userId)).thenReturn(true);
        

        // When
        boolean result = reservationService.joinWaitlist(userId, sessionId);

        // Then
        assertTrue(result);
        verify(gymSessionRepository).findById(sessionId);
        verify(waitlistService).addToWaitlist(userId, sessionId);
    }

    @Test
    void joinWaitlist_WhenSessionNotFull_ShouldThrowException() {
        // Given
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession)); // Not full by default

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.joinWaitlist(userId, sessionId);
        });
    }

    @Test
    void getWaitlistStatus_ShouldReturnStatus() {
        // Given
        int position = 2;
        when(waitlistService.getWaitlistPosition(userId, sessionId)).thenReturn(position);
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));
        
        Map<String, Object> waitlistStats = new HashMap<>();
        waitlistStats.put("totalCount", 5);
        when(waitlistService.getWaitlistStats(sessionId)).thenReturn(waitlistStats);

        // When
        Map<String, Object> result = reservationService.getWaitlistStatus(userId, sessionId);

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("inWaitlist"));
        assertEquals(position, result.get("position"));
        assertEquals(gymSession.getSessionDate(), result.get("sessionDate"));
        assertEquals(5, result.get("totalInWaitlist"));
        
        verify(waitlistService).getWaitlistPosition(userId, sessionId);
        verify(gymSessionRepository).findById(sessionId);
        verify(waitlistService).getWaitlistStats(sessionId);
    }

    @Test
    void getUserWaitlists_ShouldReturnUserWaitlists() {
        // Given
        List<Map<String, Object>> expectedWaitlists = new ArrayList<>();
        when(waitlistService.getUserWaitlistSessions(userId)).thenReturn(expectedWaitlists);

        // When
        List<Map<String, Object>> result = reservationService.getUserWaitlists(userId);

        // Then
        assertNotNull(result);
        assertEquals(expectedWaitlists, result);
        verify(waitlistService).getUserWaitlistSessions(userId);
    }

    @Test
    void leaveWaitlist_ShouldCallWaitlistService() {
        // Given
        when(waitlistService.removeFromWaitlist(userId, sessionId)).thenReturn(true);

        // When
        boolean result = reservationService.leaveWaitlist(userId, sessionId);

        // Then
        assertTrue(result);
        verify(waitlistService).removeFromWaitlist(userId, sessionId);
    }
}