package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.Equipment;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.EquipmentRepository;
import edu.eci.cvds.prometeo.service.GymReservationService;
import edu.eci.cvds.prometeo.service.NotificationService;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.PrometeoExceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GymReservationServiceImpl implements GymReservationService {
    
    private final ReservationRepository reservationRepository;
    private final GymSessionRepository gymSessionRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public GymReservationServiceImpl(
            ReservationRepository reservationRepository,
            GymSessionRepository gymSessionRepository,
            UserRepository userRepository,
            EquipmentRepository equipmentRepository,
            NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.gymSessionRepository = gymSessionRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    @Transactional
    public UUID makeReservation(UUID userId, LocalDate date, LocalTime startTime, LocalTime endTime, Optional<List<UUID>> equipmentIds) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ENCONTRADO));
                
        // Check if time slot is available
        if (!checkAvailability(date, startTime, endTime)) {
            throw new PrometeoExceptions(PrometeoExceptions.HORARIO_NO_DISPONIBLE);
        }
        
        // Check if user has reached reservation limit (e.g., 3 active reservations)
        List<Reservation> activeReservations = reservationRepository
                .findByUserIdAndDateGreaterThanEqualAndStatusNot(
                    userId, LocalDate.now(), "CANCELLED");
                    
        if (activeReservations.size() >= 3) {
            throw new PrometeoExceptions(PrometeoExceptions.LIMITE_RESERVAS_ALCANZADO);
        }
        
        // Find gym session for the selected time slot
        GymSession session = gymSessionRepository
                .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                    date, startTime, endTime)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_SESION));
                
        // Check if session has capacity
        if (!session.hasAvailability()) {
            throw new PrometeoExceptions(PrometeoExceptions.CAPACIDAD_EXCEDIDA);
        }
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setReservationDate(LocalDateTime.of(date, startTime));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setSessionId(session.getId());
        
        // Add equipment if specified
        if (equipmentIds.isPresent() && !equipmentIds.get().isEmpty()) {
            List<UUID> validEquipmentIds = validateAndReserveEquipment(equipmentIds.get(), date, startTime, endTime);
            reservation.setEquipmentIds(validEquipmentIds);
        }
        
        // Update session capacity
        session.reserve();
        gymSessionRepository.save(session);
        
        // Save reservation
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Send confirmation notification
        notificationService.sendNotification(
            userId, 
            "Gym Reservation Confirmed", 
            "Your reservation for " + date + " from " + startTime + " to " + endTime + " has been confirmed.",
            "RESERVATION_CONFIRMATION",
            Optional.of(savedReservation.getId())
        );
        
        return savedReservation.getId();
    }
    
    @Override
    @Transactional
    public boolean cancelReservation(UUID reservationId, UUID userId, Optional<String> reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RESERVA));
                
        // Validate user is the owner
        if (!reservation.getUserId().equals(userId)) {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_AUTORIZADO);
        }
        
        // Check if reservation is already cancelled
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new PrometeoExceptions(PrometeoExceptions.RESERVA_YA_CANCELADA);
        }
        
        // Check if reservation date is in the past
        if (reservation.getDate().isBefore(LocalDate.now())) {
            throw new PrometeoExceptions(PrometeoExceptions.NO_CANCELAR_RESERVAS_PASADAS);
        }
        
        // Update session capacity
        GymSession session = gymSessionRepository.findById(reservation.getSessionId())
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.SESION_NO_ENCONTRADA));
        session.cancelReservation();
        gymSessionRepository.save(session);
        
        // Release reserved equipment
        if (reservation.getEquipmentIds() != null && !reservation.getEquipmentIds().isEmpty()) {
            // Logic to release equipment would go here
            // This depends on how equipment reservation is implemented
        }
        
        // Update reservation status
        reservation.setStatus("CANCELLED");
        reason.ifPresent(reservation::setCancellationReason);
        reservationRepository.save(reservation);
        
        // Send cancellation notification
        notificationService.sendNotification(
            userId, 
            "Gym Reservation Cancelled", 
            "Your reservation for " + reservation.getDate() + " has been cancelled successfully.",
            "RESERVATION_CANCELLATION",
            Optional.of(reservationId)
        );
        
        return true;
    }
    
    @Override
    public List<Object> getUpcomingReservations(UUID userId) {
        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationRepository
                .findByUserIdAndDateGreaterThanEqualAndStatusOrderByDateAscStartTimeAsc(
                    userId, today, "CONFIRMED");
                    
        return enrichReservationsWithDetails(reservations);
    }
    
    @Override
    public List<Object> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        LocalDate start = startDate.orElse(LocalDate.now().minusMonths(3));
        LocalDate end = endDate.orElse(LocalDate.now());
        
        List<Reservation> reservations = reservationRepository
                .findByUserIdAndDateBetweenOrderByDateDescStartTimeDesc(userId, start, end);
                
        return enrichReservationsWithDetails(reservations);
    }
    
    @Override
    @Transactional
    public boolean updateReservationTime(UUID reservationId, LocalDate newDate, 
                                         LocalTime newStartTime, LocalTime newEndTime, UUID userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RESERVA));
                
        // Validate user is the owner
        if (!reservation.getUserId().equals(userId)) {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_AUTORIZADO);
        }
        
        // Check if reservation can be updated (not in the past, not cancelled)
        if (reservation.getDate().isBefore(LocalDate.now())) {
            throw new PrometeoExceptions(PrometeoExceptions.FECHA_PASADA);
        }
        
        if ("CANCELLED".equals(reservation.getStatus())) {
            throw new PrometeoExceptions(PrometeoExceptions.RESERVA_YA_CANCELADA);
        }
        
        // Check if the new time slot is available
        if (!checkAvailability(newDate, newStartTime, newEndTime)) {
            throw new PrometeoExceptions(PrometeoExceptions.HORARIO_NO_DISPONIBLE);
        }
        
        // Release the current gym session
        GymSession oldSession = gymSessionRepository.findById(reservation.getSessionId())
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.SESION_NO_ENCONTRADA));
        oldSession.cancelReservation();
        gymSessionRepository.save(oldSession);
        
        // Find gym session for the new time slot
        GymSession newSession = gymSessionRepository
                .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                    newDate, newStartTime, newEndTime)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_SESION));
                
        // Check if new session has capacity
        if (!newSession.hasAvailability()) {
            throw new PrometeoExceptions(PrometeoExceptions.CAPACIDAD_EXCEDIDA);
        }
        
        // Reserve the new session
        newSession.reserve();
        gymSessionRepository.save(newSession);
        
        // Update reservation
        reservation.setDate(newDate);
        reservation.setStartTime(newStartTime);
        reservation.setEndTime(newEndTime);
        reservation.setSessionId(newSession.getId());
        reservationRepository.save(reservation);
        
        // Send update notification
        notificationService.sendNotification(
            userId, 
            "Gym Reservation Updated", 
            "Your reservation has been updated to " + newDate + " from " + newStartTime + " to " + newEndTime + ".",
            "RESERVATION_UPDATE",
            Optional.of(reservationId)
        );
        
        return true;
    }
    
    @Override
    public boolean checkAvailability(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Check if date is in the past
        if (date.isBefore(LocalDate.now())) {
            return false;
        }
        
        // Check if there's a gym session covering the requested time
        Optional<GymSession> session = gymSessionRepository
                .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                    date, startTime, endTime);
                    
        if (session.isEmpty()) {
            return false;
        }
        
        // Check if session has available capacity
        return session.get().hasAvailability();
    }
    
    @Override
    public List<Object> getAvailableTimeSlots(LocalDate date) {
        // Find all sessions for the date
        List<GymSession> sessions = gymSessionRepository.findBySessionDateOrderByStartTime(date);
        
        // Create time slot objects with availability info
        return sessions.stream()
                .filter(GymSession::hasAvailability)
                .map(session -> {
                    Map<String, Object> timeSlot = new HashMap<>();
                    timeSlot.put("sessionId", session.getId());
                    timeSlot.put("date", session.getSessionDate());
                    timeSlot.put("startTime", session.getStartTime());
                    timeSlot.put("endTime", session.getEndTime());
                    timeSlot.put("availableSpots", session.getAvailableSpots());
                    timeSlot.put("trainerId", session.getTrainerId());
                    return timeSlot;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean recordAttendance(UUID reservationId, boolean attended, UUID trainerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RESERVA));
                
        // Only confirmed reservations can be marked as attended
        if (!"CONFIRMED".equals(reservation.getStatus())) {
            throw new PrometeoExceptions(PrometeoExceptions.SOLO_RESERVAS_CONFIRMADAS);
        }
        
        // Update reservation
        reservation.setAttended(attended);
        reservation.setStatus("COMPLETED");
        reservation.setCompletedById(trainerId);
        reservation.setCompletedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        // Send notification based on attendance
        if (attended) {
            notificationService.sendNotification(
                reservation.getUserId(), 
                "Gym Attendance Recorded", 
                "Your attendance has been recorded for your reservation on " + reservation.getDate() + ".",
                "ATTENDANCE_RECORDED",
                Optional.of(reservationId)
            );
        } else {
            notificationService.sendNotification(
                reservation.getUserId(), 
                "Missed Gym Session", 
                "You were marked as absent for your reservation on " + reservation.getDate() + ".",
                "ATTENDANCE_MISSED",
                Optional.of(reservationId)
            );
        }
        
        return true;
    }
    
    // Helper methods
    private List<UUID> validateAndReserveEquipment(List<UUID> equipmentIds, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<UUID> validEquipmentIds = new ArrayList<>();
        
        for (UUID equipmentId : equipmentIds) {
            // Check if equipment exists
            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPAMIENTO + ": " + equipmentId));
                    
            // Check if equipment is available at the requested time
            boolean isAvailable = equipmentRepository.isEquipmentAvailable(
                    equipmentId, date, startTime, endTime);
                    
            if (isAvailable) {
                validEquipmentIds.add(equipmentId);
            }
        }
        
        if (validEquipmentIds.isEmpty() && !equipmentIds.isEmpty()) {
            throw new PrometeoExceptions(PrometeoExceptions.EQUIPAMIENTO_NO_DISPONIBLE);
        }
        
        return validEquipmentIds;
    }
    
    private List<Object> enrichReservationsWithDetails(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", reservation.getId());
                    result.put("date", reservation.getDate());
                    result.put("startTime", reservation.getStartTime());
                    result.put("endTime", reservation.getEndTime());
                    result.put("status", reservation.getStatus());
                    
                    // Add gym session details
                    GymSession session = gymSessionRepository.findById(reservation.getSessionId())
                            .orElse(null);
                    if (session != null) {
                        Map<String, Object> sessionDetails = new HashMap<>();
                        sessionDetails.put("id", session.getId());
                        sessionDetails.put("trainer", session.getTrainerId());
                        result.put("session", sessionDetails);
                    }
                    
                    // Add equipment details if applicable
                    if (reservation.getEquipmentIds() != null && !reservation.getEquipmentIds().isEmpty()) {
                        List<Equipment> equipment = equipmentRepository.findAllById(reservation.getEquipmentIds());
                        result.put("equipment", equipment);
                    }
                    
                    return result;
                })
                .collect(Collectors.toList());
    }
}