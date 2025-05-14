package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.WaitlistEntry;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.WaitlistRepository;
import edu.eci.cvds.prometeo.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






class WaitlistServiceImplTest {

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private GymSessionRepository gymSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WaitlistServiceImpl waitlistService;

    private UUID userId;
    private UUID sessionId;
    private WaitlistEntry testEntry;
    private GymSession testSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        
        testEntry = new WaitlistEntry();
        testEntry.setId(UUID.randomUUID());
        testEntry.setUserId(userId);
        testEntry.setSessionId(sessionId);
        testEntry.setRequestTime(LocalDateTime.now());
        testEntry.setNotificationSent(false);
        
        testSession = new GymSession();
        testSession.setId(sessionId);
        testSession.setSessionDate(LocalDate.now());
        testSession.setStartTime(LocalTime.of(10, 0));
        testSession.setEndTime(LocalTime.of(11, 0));
        testSession.setCapacity(20);
        testSession.setReservedSpots(20);
    }

    @Test
    void addToWaitlist_UserAlreadyInWaitlist_ReturnsExistingEntryId() {
        // Arrange
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(Collections.singletonList(testEntry));

        // Act
        UUID result = waitlistService.addToWaitlist(userId, sessionId);

        // Assert
        assertEquals(testEntry.getId(), result);
        verify(waitlistRepository, never()).save(any());
    }

    @Test
    void addToWaitlist_SessionDoesNotExist_ThrowsIllegalArgumentException() {
        // Arrange
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(Collections.emptyList());
        when(gymSessionRepository.existsById(sessionId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            waitlistService.addToWaitlist(userId, sessionId);
        });
    }

    @Test
    void addToWaitlist_UserDoesNotExist_ThrowsIllegalArgumentException() {
        // Arrange
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(Collections.emptyList());
        when(gymSessionRepository.existsById(sessionId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            waitlistService.addToWaitlist(userId, sessionId);
        });
    }

    @Test
    void addToWaitlist_ValidRequest_CreatesNewEntry() {
        // Arrange
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(Collections.emptyList());
        when(gymSessionRepository.existsById(sessionId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(waitlistRepository.save(any())).thenReturn(testEntry);

        // Act
        UUID result = waitlistService.addToWaitlist(userId, sessionId);

        // Assert
        assertEquals(testEntry.getId(), result);
        verify(waitlistRepository).save(any(WaitlistEntry.class));
    }

    @Test
    void getWaitlistPosition_UserInWaitlist_ReturnsCorrectPosition() {
        // Arrange
        WaitlistEntry entry1 = new WaitlistEntry();
        entry1.setUserId(UUID.randomUUID());
        
        WaitlistEntry entry2 = new WaitlistEntry();
        entry2.setUserId(userId);
        
        when(waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId))
                .thenReturn(Arrays.asList(entry1, entry2));

        // Act
        int position = waitlistService.getWaitlistPosition(userId, sessionId);

        // Assert
        assertEquals(2, position);
    }

    @Test
    void getWaitlistPosition_UserNotInWaitlist_ReturnsZero() {
        // Arrange
        WaitlistEntry entry1 = new WaitlistEntry();
        entry1.setUserId(UUID.randomUUID());
        
        when(waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId))
                .thenReturn(Collections.singletonList(entry1));

        // Act
        int position = waitlistService.getWaitlistPosition(userId, sessionId);

        // Assert
        assertEquals(0, position);
    }

    @Test
    void notifyNextInWaitlist_NoOneInWaitlist_ReturnsFalse() {
        // Arrange
        when(waitlistRepository.findFirstBySessionIdAndNotificationSentFalseOrderByRequestTimeAsc(sessionId))
                .thenReturn(Optional.empty());

        // Act
        boolean result = waitlistService.notifyNextInWaitlist(sessionId);

        // Assert
        assertFalse(result);
        verify(notificationService, never()).sendSpotAvailableNotification(any(), any());
    }

    @Test
    void notifyNextInWaitlist_NotificationFails_ReturnsFalse() {
        // Arrange
        when(waitlistRepository.findFirstBySessionIdAndNotificationSentFalseOrderByRequestTimeAsc(sessionId))
                .thenReturn(Optional.of(testEntry));
        when(notificationService.sendSpotAvailableNotification(userId, sessionId))
                .thenReturn(false);

        // Act
        boolean result = waitlistService.notifyNextInWaitlist(sessionId);

        // Assert
        assertFalse(result);
        verify(waitlistRepository, never()).save(any());
    }

    @Test
    void notifyNextInWaitlist_NotificationSucceeds_UpdatesEntryAndReturnsTrue() {
        // Arrange
        when(waitlistRepository.findFirstBySessionIdAndNotificationSentFalseOrderByRequestTimeAsc(sessionId))
                .thenReturn(Optional.of(testEntry));
        when(notificationService.sendSpotAvailableNotification(userId, sessionId))
                .thenReturn(true);

        // Act
        boolean result = waitlistService.notifyNextInWaitlist(sessionId);

        // Assert
        assertTrue(result);
        assertTrue(testEntry.isNotificationSent());
        assertNotNull(testEntry.getNotificationTime());
        verify(waitlistRepository).save(testEntry);
    }

    @Test
    void removeFromWaitlist_UserNotInWaitlist_ReturnsFalse() {
        // Arrange
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(Collections.emptyList());

        // Act
        boolean result = waitlistService.removeFromWaitlist(userId, sessionId);

        // Assert
        assertFalse(result);
        verify(waitlistRepository, never()).deleteAll(anyList());
    }

    @Test
    void removeFromWaitlist_UserInWaitlist_RemovesEntryAndReturnsTrue() {
        // Arrange
        List<WaitlistEntry> entries = Collections.singletonList(testEntry);
        when(waitlistRepository.findByUserIdAndSessionId(userId, sessionId))
                .thenReturn(entries);

        // Act
        boolean result = waitlistService.removeFromWaitlist(userId, sessionId);

        // Assert
        assertTrue(result);
        verify(waitlistRepository).deleteAll(entries);
    }

    @Test
    void getWaitlistStats_EmptyWaitlist_ReturnsBasicStats() {
        // Arrange
        when(waitlistRepository.countBySessionId(sessionId)).thenReturn(0L);
        when(waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId))
                .thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> stats = waitlistService.getWaitlistStats(sessionId);

        // Assert
        assertEquals(0L, stats.get("totalCount"));
        assertEquals(0L, stats.get("notifiedCount"));
        assertEquals(0L, stats.get("pendingCount"));
        assertFalse(stats.containsKey("oldestRequest"));
        assertFalse(stats.containsKey("newestRequest"));
    }

    @Test
    void getWaitlistStats_NonEmptyWaitlist_ReturnsCompleteStats() {
        // Arrange
        WaitlistEntry entry1 = new WaitlistEntry();
        entry1.setNotificationSent(false);
        entry1.setRequestTime(LocalDateTime.now().minusHours(2));
        
        WaitlistEntry entry2 = new WaitlistEntry();
        entry2.setNotificationSent(true);
        entry2.setRequestTime(LocalDateTime.now().minusHours(1));
        
        List<WaitlistEntry> entries = Arrays.asList(entry1, entry2);
        
        when(waitlistRepository.countBySessionId(sessionId)).thenReturn(2L);
        when(waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId))
                .thenReturn(entries);

        // Act
        Map<String, Object> stats = waitlistService.getWaitlistStats(sessionId);

        // Assert
        assertEquals(2L, stats.get("totalCount"));
        assertEquals(1L, stats.get("notifiedCount"));
        assertEquals(1L, stats.get("pendingCount"));
        assertEquals(entry1.getRequestTime(), stats.get("oldestRequest"));
        assertEquals(entry2.getRequestTime(), stats.get("newestRequest"));
    }

    @Test
    void getUserWaitlistSessions_NoEntries_ReturnsEmptyList() {
        // Arrange
        when(waitlistRepository.findByUserIdAndNotificationSentFalse(userId))
                .thenReturn(Collections.emptyList());

        // Act
        List<Map<String, Object>> result = waitlistService.getUserWaitlistSessions(userId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserWaitlistSessions_WithEntries_ReturnsFormattedList() {
        // Arrange
        when(waitlistRepository.findByUserIdAndNotificationSentFalse(userId))
                .thenReturn(Collections.singletonList(testEntry));
        when(gymSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(testSession));
        when(waitlistRepository.findBySessionIdOrderByRequestTimeAsc(sessionId))
                .thenReturn(Collections.singletonList(testEntry));

        // Act
        List<Map<String, Object>> result = waitlistService.getUserWaitlistSessions(userId);

        // Assert
        assertEquals(1, result.size());
        Map<String, Object> entryMap = result.get(0);
        assertEquals(testEntry.getId(), entryMap.get("entryId"));
        assertEquals(testEntry.getRequestTime(), entryMap.get("requestTime"));
        assertEquals(1, entryMap.get("position"));
        
        Map<String, Object> sessionMap = (Map<String, Object>) entryMap.get("session");
        assertNotNull(sessionMap);
        assertEquals(sessionId, sessionMap.get("id"));
        assertEquals(testSession.getSessionDate(), sessionMap.get("date"));
        assertEquals(testSession.getStartTime(), sessionMap.get("startTime"));
        assertEquals(testSession.getEndTime(), sessionMap.get("endTime"));
        assertEquals(testSession.getCapacity(), sessionMap.get("capacity"));
        assertEquals(testSession.getReservedSpots(), sessionMap.get("reservedSpots"));
    }
}