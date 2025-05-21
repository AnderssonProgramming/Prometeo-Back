package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;





public class NotificationDTOTest {

    @Test
    public void testNotificationDTOGettersAndSetters() {
        // Arrange
        NotificationDTO notification = new NotificationDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String title = "Test Title";
        String message = "Test Message";
        String type = "INFO";
        boolean read = true;
        LocalDateTime scheduledTime = LocalDateTime.now();
        LocalDateTime sentTime = LocalDateTime.now();
        UUID relatedEntityId = UUID.randomUUID();

        // Act
        notification.setId(id);
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(read);
        notification.setScheduledTime(scheduledTime);
        notification.setSentTime(sentTime);
        notification.setRelatedEntityId(relatedEntityId);

        // Assert
        assertEquals(id, notification.getId());
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
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        UUID relatedEntityId = UUID.randomUUID();

        NotificationDTO notification1 = new NotificationDTO();
        notification1.setId(id);
        notification1.setUserId(userId);
        notification1.setTitle("Test");
        notification1.setMessage("Message");
        notification1.setType("INFO");
        notification1.setRead(false);
        notification1.setScheduledTime(now);
        notification1.setSentTime(now);
        notification1.setRelatedEntityId(relatedEntityId);

        NotificationDTO notification2 = new NotificationDTO();
        notification2.setId(id);
        notification2.setUserId(userId);
        notification2.setTitle("Test");
        notification2.setMessage("Message");
        notification2.setType("INFO");
        notification2.setRead(false);
        notification2.setScheduledTime(now);
        notification2.setSentTime(now);
        notification2.setRelatedEntityId(relatedEntityId);

        NotificationDTO notificationDifferent = new NotificationDTO();
        notificationDifferent.setId(UUID.randomUUID());
        
        // Assert
        assertEquals(notification1, notification2);
        assertEquals(notification1.hashCode(), notification2.hashCode());
        assertNotEquals(notification1, notificationDifferent);
        assertNotEquals(notification1.hashCode(), notificationDifferent.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        NotificationDTO notification = new NotificationDTO();
        notification.setTitle("Test Title");
        
        // Act
        String toString = notification.toString();
        
        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("title=Test Title"));
    }
}