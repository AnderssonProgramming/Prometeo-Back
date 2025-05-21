package edu.eci.cvds.prometeo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;





public class WaitlistEntryTest {

    @Test
    public void testDefaultConstructor() {
        WaitlistEntry waitlistEntry = new WaitlistEntry();
        
        assertNull(waitlistEntry.getUserId());
        assertNull(waitlistEntry.getSessionId());
        assertNull(waitlistEntry.getRequestTime());
        assertFalse(waitlistEntry.isNotificationSent());
        assertNull(waitlistEntry.getNotificationTime());
    }
    
    @Test
    public void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        LocalDateTime requestTime = LocalDateTime.now();
        boolean notificationSent = true;
        LocalDateTime notificationTime = LocalDateTime.now().plusHours(1);
        
        WaitlistEntry waitlistEntry = new WaitlistEntry(userId, sessionId, requestTime, notificationSent, notificationTime);
        
        assertEquals(userId, waitlistEntry.getUserId());
        assertEquals(sessionId, waitlistEntry.getSessionId());
        assertEquals(requestTime, waitlistEntry.getRequestTime());
        assertEquals(notificationSent, waitlistEntry.isNotificationSent());
        assertEquals(notificationTime, waitlistEntry.getNotificationTime());
    }
    
    @Test
    public void testGettersAndSetters() {
        WaitlistEntry waitlistEntry = new WaitlistEntry();
        
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        LocalDateTime requestTime = LocalDateTime.now();
        boolean notificationSent = true;
        LocalDateTime notificationTime = LocalDateTime.now().plusHours(1);
        
        waitlistEntry.setUserId(userId);
        waitlistEntry.setSessionId(sessionId);
        waitlistEntry.setRequestTime(requestTime);
        waitlistEntry.setNotificationSent(notificationSent);
        waitlistEntry.setNotificationTime(notificationTime);
        
        assertEquals(userId, waitlistEntry.getUserId());
        assertEquals(sessionId, waitlistEntry.getSessionId());
        assertEquals(requestTime, waitlistEntry.getRequestTime());
        assertEquals(notificationSent, waitlistEntry.isNotificationSent());
        assertEquals(notificationTime, waitlistEntry.getNotificationTime());
    }
    
    @Test
    public void testPrePersist() {
        WaitlistEntry waitlistEntry = new WaitlistEntry();
        
        // Before prePersist
        assertNull(waitlistEntry.getRequestTime());
        assertFalse(waitlistEntry.isNotificationSent());
        
        // Execute prePersist
        waitlistEntry.prePersist();
        
        // After prePersist
        assertNotNull(waitlistEntry.getRequestTime());
        assertFalse(waitlistEntry.isNotificationSent());
        
        // Verify requestTime is close to current time
        LocalDateTime now = LocalDateTime.now();
        assertTrue(Math.abs(java.time.Duration.between(now, waitlistEntry.getRequestTime()).getSeconds()) < 2);
    }
}