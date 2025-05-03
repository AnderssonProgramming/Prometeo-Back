package edu.eci.cvds.prometeo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

/**
 * Service for managing notifications in the system
 * Note: This service requires a Notification entity that doesn't appear in the provided code.
 * Implementation would need to create this entity.
 */
public interface NotificationService {
    
    /**
     * Sends a notification to a user
     * @param userId ID of the user
     * @param title Notification title
     * @param message Notification message
     * @param type Type of notification
     * @param entityId Optional related entity ID
     * @return ID of the sent notification
     */
    UUID sendNotification(UUID userId, String title, String message, String type, Optional<UUID> entityId);
    
    /**
     * Schedules a notification to be sent later
     * @param userId ID of the user
     * @param title Notification title
     * @param message Notification message
     * @param type Type of notification
     * @param scheduledTime When to send the notification
     * @param entityId Optional related entity ID
     * @return ID of the scheduled notification
     */
    UUID scheduleNotification(UUID userId, String title, String message, 
                             String type, LocalDateTime scheduledTime, Optional<UUID> entityId);
    
    /**
     * Gets unread notifications for a user
     * @param userId ID of the user
     * @return List of unread notifications
     */
    List<Object> getUnreadNotifications(UUID userId);
    
    /**
     * Marks a notification as read
     * @param notificationId ID of the notification
     * @param userId ID of the user
     * @return true if successfully marked
     */
    boolean markAsRead(UUID notificationId, UUID userId);
    
    /**
     * Sends notifications to users with upcoming reservations
     * @param hoursInAdvance Hours before reservation to send notification
     * @return Number of notifications sent
     */
    int sendReservationReminders(int hoursInAdvance);
    
    /**
     * Sends notifications about new routines
     * @param routineId ID of the new routine
     * @param targetUserIds Optional list of specific users to notify, empty for all
     * @return Number of notifications sent
     */
    int notifyNewRoutine(UUID routineId, List<UUID> targetUserIds);
    
    /**
     * Sends a progress milestone notification to a user
     * @param userId ID of the user
     * @param milestone Description of the milestone
     * @param achievement Value achieved
     * @return ID of the sent notification
     */
    UUID sendProgressMilestoneNotification(UUID userId, String milestone, double achievement);
}