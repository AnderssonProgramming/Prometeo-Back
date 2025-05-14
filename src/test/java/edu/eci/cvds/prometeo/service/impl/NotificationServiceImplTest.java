package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GymSessionRepository gymSessionRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UUID sessionId;
    private UUID reservationId;
    private GymSession gymSession;
    private Reservation reservation;    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        reservationId = UUID.randomUUID();

        // Create test GymSession
        gymSession = new GymSession();
        gymSession.setId(sessionId);
        gymSession.setSessionDate(LocalDate.now());
        gymSession.setStartTime(LocalTime.of(10, 0));
        gymSession.setEndTime(LocalTime.of(11, 0));

        // Create test Reservation
        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setSessionId(sessionId);
        reservation.setUserId(userId);
    }    @Test
    void testSendNotification() {
        // Arrange
        String title = "Test Title";
        String message = "Test Message";
        String type = "Test Type";
        Optional<UUID> referenceId = Optional.of(UUID.randomUUID());

        // Act
        boolean result = notificationService.sendNotification(userId, title, message, type, referenceId);

        // Assert
        assertTrue(result);
    }    @Test
    void testSendSpotAvailableNotification_Success() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));

        // Act
        boolean result = notificationService.sendSpotAvailableNotification(userId, sessionId);

        // Assert
        assertTrue(result);
        verify(gymSessionRepository).findById(sessionId);
    }

    @Test
    void testSendSpotAvailableNotification_SessionNotFound() {
        // Arrange
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act
        boolean result = notificationService.sendSpotAvailableNotification(userId, sessionId);

        // Assert
        assertFalse(result);
        verify(gymSessionRepository).findById(sessionId);
    }    @Test
    void testSendReservationConfirmation_Success() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));

        // Act
        boolean result = notificationService.sendReservationConfirmation(userId, reservationId);

        // Assert
        assertTrue(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(sessionId);
    }

    @Test
    void testSendReservationConfirmation_ReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act
        boolean result = notificationService.sendReservationConfirmation(userId, reservationId);

        // Assert
        assertFalse(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository, never()).findById(any());
    }

    @Test
    void testSendReservationConfirmation_SessionNotFound() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act
        boolean result = notificationService.sendReservationConfirmation(userId, reservationId);

        // Assert
        assertFalse(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(sessionId);
    }    @Test
    void testSendSessionReminder_Success() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.of(gymSession));

        // Act
        boolean result = notificationService.sendSessionReminder(userId, reservationId);

        // Assert
        assertTrue(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(sessionId);
    }

    @Test
    void testSendSessionReminder_ReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act
        boolean result = notificationService.sendSessionReminder(userId, reservationId);

        // Assert
        assertFalse(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository, never()).findById(any());
    }

    @Test
    void testSendSessionReminder_SessionNotFound() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(gymSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Act
        boolean result = notificationService.sendSessionReminder(userId, reservationId);

        // Assert
        assertFalse(result);
        verify(reservationRepository).findById(reservationId);
        verify(gymSessionRepository).findById(sessionId);
    }
}