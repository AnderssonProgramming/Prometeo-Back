package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.GymSessionService;
import edu.eci.cvds.prometeo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GymSessionServiceImpl implements GymSessionService {
    
    private final GymSessionRepository gymSessionRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public GymSessionServiceImpl(
            GymSessionRepository gymSessionRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.gymSessionRepository = gymSessionRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    @Transactional
    public UUID createSession(
            LocalDate date, 
            LocalTime startTime, 
            LocalTime endTime, 
            int capacity, 
            Optional<String> description,
            UUID trainerId) {
        
        // Validate trainer exists
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new PrometeoExceptions("Entrenador no encontrado"));
        
        // Validate trainer role
        if (!"TRAINER".equals(trainer.getRole())) {
            throw new PrometeoExceptions("Solo los entrenadores pueden crear sesiones");
        }
        
        // Validate date is not in the past
        if (date.isBefore(LocalDate.now())) {
            throw new PrometeoExceptions("No se pueden programar sesiones para fechas pasadas");
        }
        
        // Validate time range
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new PrometeoExceptions("La hora de finalización debe ser posterior a la de inicio");
        }
        
        // Validate capacity
        if (capacity <= 0) {
            throw new PrometeoExceptions("La capacidad debe ser mayor a cero");
        }
        
        // Check for schedule conflicts with other sessions for this trainer
        List<GymSession> trainerSessions = gymSessionRepository.findByTrainerIdAndSessionDate(trainerId, date);
        
        boolean hasConflict = trainerSessions.stream()
                .anyMatch(session -> 
                    (startTime.isBefore(session.getEndTime()) && endTime.isAfter(session.getStartTime())));
                    
        if (hasConflict) {
            throw new PrometeoExceptions("El horario se solapa con otra sesión programada");
        }
        
        // Create new session
        GymSession session = new GymSession();
        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setCapacity(capacity);
        session.setTrainerId(trainerId);
        description.ifPresent(session::setDescription);
        
        // Save and return
        GymSession savedSession = gymSessionRepository.save(session);
        return savedSession.getId();
    }
    
    @Override
    @Transactional
    public boolean updateSession(
            UUID sessionId, 
            LocalDate date, 
            LocalTime startTime, 
            LocalTime endTime, 
            int capacity,
            UUID trainerId) {
        
        // Check if trainer exists and has proper role
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new PrometeoExceptions("Entrenador no encontrado"));
        
        if (!"TRAINER".equals(trainer.getRole())) {
            throw new PrometeoExceptions("Solo los entrenadores pueden modificar sesiones");
        }
        
        // Get the session
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Validate the trainer is the owner of this session
        if (!session.getTrainerId().equals(trainerId)) {
            throw new PrometeoExceptions("Solo el entrenador asignado puede modificar esta sesión");
        }
        
        // Validate date is not in the past
        if (date.isBefore(LocalDate.now())) {
            throw new PrometeoExceptions("No se pueden programar sesiones para fechas pasadas");
        }
        
        // Validate time range
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new PrometeoExceptions("La hora de finalización debe ser posterior a la de inicio");
        }
        
        // Check if decreasing capacity will leave current bookings without a spot
        if (capacity < session.getCurrentBookings()) {
            throw new PrometeoExceptions("No se puede reducir la capacidad por debajo del número de reservas actuales");
        }
        
        // Check for schedule conflicts if date/time is being changed
        boolean timeChanged = !session.getSessionDate().equals(date) || 
                             !session.getStartTime().equals(startTime) || 
                             !session.getEndTime().equals(endTime);
                             
        if (timeChanged) {
            List<GymSession> trainerSessions = gymSessionRepository.findByTrainerIdAndSessionDate(trainerId, date);
            
            boolean hasConflict = trainerSessions.stream()
                    .filter(s -> !s.getId().equals(sessionId)) // Exclude current session
                    .anyMatch(s -> 
                        (startTime.isBefore(s.getEndTime()) && endTime.isAfter(s.getStartTime())));
                        
            if (hasConflict) {
                throw new PrometeoExceptions("El horario se solapa con otra sesión programada");
            }
            
            // Notify users of schedule change
            List<Reservation> reservations = reservationRepository.findBySessionIdAndStatus(
                    sessionId, ReservationStatus.CONFIRMED);
                    
            for (Reservation reservation : reservations) {
                notificationService.sendNotification(
                    reservation.getUserId(),
                    "Cambio en el Horario de Sesión",
                    "El horario de su sesión ha sido modificado: Nueva fecha " + date + 
                    " de " + startTime + " a " + endTime + ".",
                    "SESSION_MODIFIED",
                    Optional.of(sessionId)
                );
            }
        }
        
        // Update session details
        session.setSessionDate(date);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setCapacity(capacity);
        
        // Save changes
        gymSessionRepository.save(session);
        return true;
    }
    
    @Override
    @Transactional
    public boolean cancelSession(UUID sessionId, String reason, UUID trainerId) {
        // Validate trainer
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new PrometeoExceptions("Entrenador no encontrado"));
        
        if (!"TRAINER".equals(trainer.getRole()) && !"ADMIN".equals(trainer.getRole())) {
            throw new PrometeoExceptions("Solo los entrenadores pueden cancelar sesiones");
        }
        
        // Get the session
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Validate the trainer is the owner of this session or is admin
        if (!session.getTrainerId().equals(trainerId) && !"ADMIN".equals(trainer.getRole())) {
            throw new PrometeoExceptions("Solo el entrenador asignado puede cancelar esta sesión");
        }
        
        // Check if already cancelled
        if (!"ACTIVE".equals(session.getStatus())) {
            throw new PrometeoExceptions("La sesión ya ha sido cancelada");
        }
        
        // Mark as cancelled
        session.setStatus("CANCELLED");
        session.setCancellationReason(reason);
        gymSessionRepository.save(session);
        
        // Cancel all reservations and notify users
        List<Reservation> reservations = reservationRepository.findBySessionIdAndStatus(
                sessionId, ReservationStatus.CONFIRMED);
                
        for (Reservation reservation : reservations) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservation.setCanceledAt(LocalDateTime.now());
            reservation.setCancellationReason("Sesión cancelada por el entrenador: " + reason);
            reservationRepository.save(reservation);
            
            notificationService.sendNotification(
                reservation.getUserId(),
                "Sesión Cancelada",
                "Su sesión del " + session.getSessionDate() + " de " + 
                session.getStartTime() + " a " + session.getEndTime() + 
                " ha sido cancelada. Motivo: " + reason,
                "SESSION_CANCELLED",
                Optional.of(sessionId)
            );
        }
        
        return true;
    }
    
    @Override
    public List<GymSession> getSessionsByDate(LocalDate date) {
        return gymSessionRepository.findBySessionDateOrderByStartTimeAsc(date);
    }
    
    @Override
    public List<GymSession> getSessionsByTrainer(UUID trainerId) {
        return gymSessionRepository.findByTrainerIdOrderBySessionDateAscStartTimeAsc(trainerId);
    }
    
    @Override
    public Optional<GymSession> getSessionById(UUID sessionId) {
        return gymSessionRepository.findById(sessionId);
    }
    
    @Override
    public List<User> getRegisteredUsersForSession(UUID sessionId) {
        // Get all confirmed reservations for this session
        List<Reservation> reservations = reservationRepository.findBySessionIdAndStatus(
                sessionId, ReservationStatus.CONFIRMED);
        
        // Get user details
        List<User> users = new ArrayList<>();
        for (Reservation reservation : reservations) {
            userRepository.findById(reservation.getUserId())
                    .ifPresent(users::add);
        }
        
        return users;
    }
    
    @Override
    @Transactional
    public boolean markAttendance(UUID sessionId, UUID userId, boolean attended, UUID trainerId) {
        // Validate trainer
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new PrometeoExceptions("Entrenador no encontrado"));
        
        if (!"TRAINER".equals(trainer.getRole()) && !"ADMIN".equals(trainer.getRole())) {
            throw new PrometeoExceptions("Solo los entrenadores pueden registrar asistencia");
        }
        
        // Find the reservation
        Optional<Reservation> reservationOpt = reservationRepository.findBySessionIdAndUserId(sessionId, userId);
        
        if (reservationOpt.isEmpty()) {
            throw new PrometeoExceptions("No existe una reserva para este usuario en esta sesión");
        }
        
        Reservation reservation = reservationOpt.get();
        
        // Update attendance
        reservation.setAttended(attended);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCompletedById(trainerId);
        reservation.setCompletedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        return true;
    }
    
    @Override
    public Map<String, Object> getSessionOccupancyStats(
            UUID trainerId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        
        // Set default dates if not provided
        LocalDate start = startDate.orElse(LocalDate.now().minusMonths(1));
        LocalDate end = endDate.orElse(LocalDate.now());
        
        // Get all sessions for this trainer in the date range
        List<GymSession> sessions = gymSessionRepository.findByTrainerIdAndSessionDateBetween(
                trainerId, start, end);
        
        // Calculate statistics
        int totalSessions = sessions.size();
        int totalCapacity = sessions.stream().mapToInt(GymSession::getCapacity).sum();
        int totalBookings = sessions.stream().mapToInt(GymSession::getCurrentBookings).sum();
        double averageOccupancyRate = totalCapacity > 0 ? 
                (double) totalBookings / totalCapacity * 100 : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", totalSessions);
        stats.put("totalCapacity", totalCapacity);
        stats.put("totalBookings", totalBookings);
        stats.put("averageOccupancyRate", Math.round(averageOccupancyRate * 100.0) / 100.0);
        
        // Add daily breakdown
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (GymSession session : sessions) {
            Map<String, Object> sessionStats = new HashMap<>();
            sessionStats.put("sessionId", session.getId());
            sessionStats.put("date", session.getSessionDate());
            sessionStats.put("startTime", session.getStartTime());
            sessionStats.put("endTime", session.getEndTime());
            sessionStats.put("capacity", session.getCapacity());
            sessionStats.put("bookings", session.getCurrentBookings());
            sessionStats.put("occupancyRate", 
                    Math.round(((double) session.getCurrentBookings() / session.getCapacity() * 100) * 100.0) / 100.0);
            
            dailyStats.add(sessionStats);
        }
        
        stats.put("sessions", dailyStats);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getAttendanceStatsForSession(UUID sessionId) {
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Get all reservations for this session
        List<Reservation> reservations = reservationRepository.findBySessionId(sessionId);
        
        // Calculate attendance statistics
        int totalReservations = reservations.size();
        long attendedCount = reservations.stream().filter(Reservation::isAttended).count();
        long noShowCount = reservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) && !r.isAttended())
                .count();
        long cancelledCount = reservations.stream()
                .filter(r -> ReservationStatus.CANCELLED.equals(r.getStatus()))
                .count();
        long pendingCount = reservations.stream()
                .filter(r -> ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .count();
        
        double attendanceRate = totalReservations > 0 ? 
                (double) attendedCount / totalReservations * 100 : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("sessionId", session.getId());
        stats.put("date", session.getSessionDate());
        stats.put("startTime", session.getStartTime());
        stats.put("endTime", session.getEndTime());
        stats.put("capacity", session.getCapacity());
        stats.put("bookings", session.getCurrentBookings());
        stats.put("attended", attendedCount);
        stats.put("noShow", noShowCount);
        stats.put("cancelled", cancelledCount);
        stats.put("pending", pendingCount);
        stats.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
        
        return stats;
    }
}