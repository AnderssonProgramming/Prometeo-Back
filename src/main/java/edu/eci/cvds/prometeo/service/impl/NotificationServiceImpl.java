package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the notification service
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GymSessionRepository gymSessionRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public boolean sendNotification(UUID userId, String title, String message, String type, Optional<UUID> referenceId) {
        // In a real implementation, this would connect to an email service, push notification system, etc.
        // For now, we'll just log the notification
        logger.info("Notification sent to user {}: {} - {}", userId, title, message);
        logger.info("Type: {}, Reference: {}", type, referenceId.orElse(null));
        
        // In a real implementation, handle errors and return false if sending fails
        return true;
    }

    public boolean sendSpotAvailableNotification(UUID userId, UUID sessionId) {
        // En una implementación real, esto enviaría un email o push notification
        // Por ahora, solo registramos en el log
        
        Optional<GymSession> sessionOpt = gymSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            logger.error("No se pudo enviar notificación: sesión {} no encontrada", sessionId);
            return false;
        }
        
        GymSession session = sessionOpt.get();
        
        logger.info("NOTIFICACIÓN: Cupo disponible para usuario {}", userId);
        logger.info("Fecha: {}, Horario: {} - {}", 
                session.getSessionDate().format(DATE_FORMATTER),
                session.getStartTime().format(TIME_FORMATTER),
                session.getEndTime().format(TIME_FORMATTER));
        
        return true;
    }
    
    @Override
    public boolean sendReservationConfirmation(UUID userId, UUID reservationId) {
        // En una implementación real, esto enviaría un email o push notification
        
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            logger.error("No se pudo enviar confirmación: reserva {} no encontrada", reservationId);
            return false;
        }
        
        Reservation reservation = reservationOpt.get();
        Optional<GymSession> sessionOpt = gymSessionRepository.findById(reservation.getSessionId());
        
        if (sessionOpt.isEmpty()) {
            logger.error("No se pudo enviar confirmación: sesión {} no encontrada", reservation.getSessionId());
            return false;
        }
        
        GymSession session = sessionOpt.get();
        
        logger.info("CONFIRMACIÓN DE RESERVA para usuario {}", userId);
        logger.info("Reserva ID: {}", reservationId);
        logger.info("Fecha: {}, Horario: {} - {}", 
                session.getSessionDate().format(DATE_FORMATTER),
                session.getStartTime().format(TIME_FORMATTER),
                session.getEndTime().format(TIME_FORMATTER));
        
        return true;
    }
    
    @Override
    public boolean sendSessionReminder(UUID userId, UUID reservationId) {
        // Similar a los métodos anteriores, pero para recordatorios
        
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            logger.error("No se pudo enviar recordatorio: reserva {} no encontrada", reservationId);
            return false;
        }
        
        Reservation reservation = reservationOpt.get();
        Optional<GymSession> sessionOpt = gymSessionRepository.findById(reservation.getSessionId());
        
        if (sessionOpt.isEmpty()) {
            logger.error("No se pudo enviar recordatorio: sesión {} no encontrada", reservation.getSessionId());
            return false;
        }
        
        GymSession session = sessionOpt.get();
        
        logger.info("RECORDATORIO DE SESIÓN para usuario {}", userId);
        logger.info("Reserva ID: {}", reservationId);
        logger.info("Fecha: {}, Horario: {} - {}", 
                session.getSessionDate().format(DATE_FORMATTER),
                session.getStartTime().format(TIME_FORMATTER),
                session.getEndTime().format(TIME_FORMATTER));
        
        return true;
    }
}
