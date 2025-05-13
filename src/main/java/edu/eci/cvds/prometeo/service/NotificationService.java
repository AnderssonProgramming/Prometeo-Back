package edu.eci.cvds.prometeo.service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for sending notifications to users
 */
public interface NotificationService {
    
    /**
     * Sends a notification to a user
     * 
     * @param userId ID of the user to notify
     * @param title Title of the notification
     * @param message Content of the notification
     * @param type Type of notification (e.g., RESERVATION_CONFIRMATION)
     * @param referenceId Optional ID reference (e.g., reservation ID)
     * @return true if notification was sent successfully
     */
    boolean sendNotification(UUID userId, String title, String message, String type, Optional<UUID> referenceId);

    /**
     * Envía una notificación sobre liberación de cupo
     * @param userId ID del usuario a notificar
     * @param sessionId ID de la sesión disponible
     * @return true si la notificación fue enviada correctamente
     */
    boolean sendSpotAvailableNotification(UUID userId, UUID sessionId);
    
    /**
     * Envía una confirmación de reserva
     * @param userId ID del usuario
     * @param reservationId ID de la reserva
     * @return true si la notificación fue enviada correctamente
     */
    boolean sendReservationConfirmation(UUID userId, UUID reservationId);
    
    /**
     * Envía un recordatorio de sesión próxima
     * @param userId ID del usuario
     * @param reservationId ID de la reserva
     * @return true si la notificación fue enviada correctamente
     */
    boolean sendSessionReminder(UUID userId, UUID reservationId);
}