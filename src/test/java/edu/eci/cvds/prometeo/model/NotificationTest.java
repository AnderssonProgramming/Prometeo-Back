package edu.eci.cvds.prometeo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




public class NotificationTest {

    @Test
    public void testDefaultConstructor() {
        Notification notification = new Notification();
        assertNotNull(notification);
        assertFalse(notification.isRead()); // Default value for read should be false
    }

    @Test
    public void testGettersAndSetters() {
        // Create notification with default constructor
        Notification notification = new Notification();

        // Prepare test data
        UUID userId = UUID.randomUUID();
        String title = "Test Title";
        String message = "Test Message";
        String type = "Test Type";
        boolean read = true;
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        LocalDateTime sentTime = LocalDateTime.now();
        UUID relatedEntityId = UUID.randomUUID();

        // Set properties
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(read);
        notification.setScheduledTime(scheduledTime);
        notification.setSentTime(sentTime);
        notification.setRelatedEntityId(relatedEntityId);

        // Verify properties
        assertEquals(userId, notification.getUserId());
        assertEquals(title, notification.getTitle());
        assertEquals(message, notification.getMessage());
        assertEquals(type, notification.getType());
        assertEquals(read, notification.isRead());
        assertEquals(scheduledTime, notification.getScheduledTime());
        assertEquals(sentTime, notification.getSentTime());
        assertEquals(relatedEntityId, notification.getRelatedEntityId());
    }

    @Test
    public void testMarkAsRead() {
        Notification notification = new Notification();
        assertFalse(notification.isRead()); // Initially should be false
        
        notification.markAsRead();
        assertTrue(notification.isRead()); // After marking as read, should be true
    }

    @Test
    public void testIsScheduled() {
        Notification notification = new Notification();
        
        // Case 1: Both scheduledTime and sentTime are null
        assertFalse(notification.isScheduled());
        
        // Case 2: ScheduledTime is set but sentTime is null
        notification.setScheduledTime(LocalDateTime.now().plusDays(1));
        assertTrue(notification.isScheduled());
        
        // Case 3: Both scheduledTime and sentTime are set
        notification.setSentTime(LocalDateTime.now());
        assertFalse(notification.isScheduled());
    }

    @Test
    public void testIsPending() {
        Notification notification = new Notification();
        
        // Case 1: Both scheduledTime and sentTime are null
        assertTrue(notification.isPending());
        
        // Case 2: ScheduledTime is set but sentTime is null
        notification.setScheduledTime(LocalDateTime.now().plusDays(1));
        assertFalse(notification.isPending());
        
        // Case 3: Both scheduledTime and sentTime are set
        notification.setSentTime(LocalDateTime.now());
        assertFalse(notification.isPending());
        
        // Case 4: ScheduledTime is null but sentTime is set
        notification.setScheduledTime(null);
        assertFalse(notification.isPending());
    }

    @Test
    public void testIsSent() {
        Notification notification = new Notification();
        
        // Case 1: sentTime is null
        assertFalse(notification.isSent());
        
        // Case 2: sentTime is set
        notification.setSentTime(LocalDateTime.now());
        assertTrue(notification.isSent());
    }
}