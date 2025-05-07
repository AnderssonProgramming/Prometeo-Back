package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.EquipmentDTO;
import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.*;
import edu.eci.cvds.prometeo.service.GymReservationService;
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
public class GymReservationServiceImpl implements GymReservationService {
    
    private final ReservationRepository reservationRepository;
    private final GymSessionRepository gymSessionRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final WaitlistEntryRepository waitlistEntryRepository;
    private final NotificationService notificationService;
    
    // Maximum number of active reservations per user
    private static final int MAX_ACTIVE_RESERVATIONS = 3;
    
    @Autowired
    public GymReservationServiceImpl(
            ReservationRepository reservationRepository,
            GymSessionRepository gymSessionRepository,
            UserRepository userRepository,
            EquipmentRepository equipmentRepository,
            WaitlistEntryRepository waitlistEntryRepository,
            NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.gymSessionRepository = gymSessionRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.waitlistEntryRepository = waitlistEntryRepository;
        this.notificationService = notificationService;
    }
    
    @Override
    @Transactional
    public UUID makeReservation(UUID userId, UUID sessionId, Optional<List<UUID>> equipmentIds) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions("Usuario no encontrado"));
        
        // Get the session
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Check if session is active
        if (!"ACTIVE".equals(session.getStatus())) {
            throw new PrometeoExceptions("La sesión ha sido cancelada o no está disponible");
        }
        
        // Check if the date is in the past
        if (session.getSessionDate().isBefore(LocalDate.now()) || 
            (session.getSessionDate().isEqual(LocalDate.now()) && session.getStartTime().isBefore(LocalTime.now()))) {
            throw new PrometeoExceptions("No se pueden hacer reservas para fechas pasadas");
        }
        
        // Check if session has capacity
        if (!session.hasAvailability()) {
            throw new PrometeoExceptions("La sesión está a capacidad máxima");
        }
        
        // Check if user has reached reservation limit
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByUserIdFromDate(
                userId, now);
                
        if (activeReservations.size() >= MAX_ACTIVE_RESERVATIONS) {
            throw new PrometeoExceptions("Ha alcanzado el límite de " + MAX_ACTIVE_RESERVATIONS + 
                                         " reservas activas simultáneamente");
        }
        
        // Check if user already has a reservation for this session
        boolean hasReservation = activeReservations.stream()
                .anyMatch(r -> r.getSessionId().equals(sessionId));
                
        if (hasReservation) {
            throw new PrometeoExceptions("Ya tiene una reserva para esta sesión");
        }
        
        // Check if user already has a reservation at this time
        LocalDateTime sessionDateTime = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        boolean hasOverlappingReservation = activeReservations.stream()
                .anyMatch(r -> {
                    GymSession s = gymSessionRepository.findById(r.getSessionId()).orElse(null);
                    if (s == null) return false;
                    
                    LocalDateTime otherDateTime = LocalDateTime.of(s.getSessionDate(), s.getStartTime());
                    LocalDateTime otherEndDateTime = LocalDateTime.of(s.getSessionDate(), s.getEndTime());
                    
                    return (sessionDateTime.isEqual(otherDateTime) || 
                            (sessionDateTime.isAfter(otherDateTime) && sessionDateTime.isBefore(otherEndDateTime)));
                });
                
        if (hasOverlappingReservation) {
            throw new PrometeoExceptions("Ya tiene una reserva activa en este horario");
        }
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setSessionId(sessionId);
        reservation.setReservationDate(sessionDateTime);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        
        // Add equipment if specified
        if (equipmentIds.isPresent() && !equipmentIds.get().isEmpty()) {
            List<UUID> validatedEquipmentIds = validateAndReserveEquipment(
                equipmentIds.get(), session.getSessionDate(), session.getStartTime(), session.getEndTime());
            reservation.setEquipmentIds(validatedEquipmentIds);
        }
        
        // Update session capacity
        session.reserve();
        gymSessionRepository.save(session);
        
        // Save reservation
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Remove from waitlist if present
        waitlistEntryRepository.findByUserIdAndSessionId(userId, sessionId)
                .ifPresent(entry -> waitlistEntryRepository.delete(entry));
        
        // Send confirmation notification
        notificationService.sendNotification(
            userId, 
            "Reserva de Gimnasio Confirmada", 
            "Su reserva para el " + session.getSessionDate() + " de " + 
            session.getStartTime() + " a " + session.getEndTime() + " ha sido confirmada.",
            "RESERVATION_CONFIRMATION",
            Optional.of(savedReservation.getId())
        );
        
        return savedReservation.getId();
    }
    
    @Override
    @Transactional
    public boolean cancelReservation(UUID reservationId, UUID userId, Optional<String> reason) {
        // Get the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new PrometeoExceptions("Reserva no encontrada"));
        
        // Validate user is the owner
        if (!reservation.getUserId().equals(userId)) {
            throw new PrometeoExceptions("No está autorizado para cancelar esta reserva");
        }
        
        // Check if reservation is already cancelled
        if (ReservationStatus.CANCELLED.equals(reservation.getStatus())) {
            throw new PrometeoExceptions("La reserva ya ha sido cancelada");
        }
        
        // Check if reservation date is in the past
        LocalDateTime reservationDateTime = reservation.getReservationDate();
        if (reservationDateTime.isBefore(LocalDateTime.now())) {
            throw new PrometeoExceptions("No se pueden cancelar reservas pasadas");
        }
        
        // Get the session
        GymSession session = gymSessionRepository.findById(reservation.getSessionId())
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Free up the spot in the session
        session.cancelReservation();
        gymSessionRepository.save(session);
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCanceledAt(LocalDateTime.now());
        reason.ifPresent(reservation::setCancellationReason);
        reservationRepository.save(reservation);
        
        // Send cancellation notification
        notificationService.sendNotification(
            userId, 
            "Reserva de Gimnasio Cancelada", 
            "Su reserva para el " + session.getSessionDate() + " de " + 
            session.getStartTime() + " a " + session.getEndTime() + " ha sido cancelada.",
            "RESERVATION_CANCELLATION",
            Optional.of(reservationId)
        );
        
        // Notify next person on waitlist if any
        notifyNextWaitlistedUser(reservation.getSessionId());
        
        return true;
    }
    
    @Override
    public List<ReservationDTO> getUpcomingReservations(UUID userId) {
        // Get reservations for today and future with CONFIRMED status
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository.findByUserIdAndDateGreaterThanEqualAndStatusOrderByDateAsc(
            userId, now, ReservationStatus.CONFIRMED);
        
        return enrichReservationsWithDetails(reservations);
    }
    
    @Override
    public List<ReservationDTO> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        LocalDate start = startDate.orElse(LocalDate.now().minusMonths(3));
        LocalDate end = endDate.orElse(LocalDate.now());
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        
        List<Reservation> reservations = reservationRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDateTime, endDateTime);
                
        return enrichReservationsWithDetails(reservations);
    }
    
    @Override
    public boolean checkSessionAvailability(UUID sessionId) {
        // Check if the session exists and has availability
        return gymSessionRepository.findById(sessionId)
                .map(GymSession::hasAvailability)
                .orElse(false);
    }
    
    @Override
    public Optional<ReservationDTO> getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::mapToReservationDTO);
    }
    
    @Override
    @Transactional
    public boolean recordAttendance(UUID reservationId, boolean attended, UUID trainerId) {
        // Get the reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new PrometeoExceptions("Reserva no encontrada"));
        
        // Only confirmed reservations can be marked as attended
        if (!ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
            throw new PrometeoExceptions("Solo se puede registrar asistencia para reservas confirmadas");
        }
        
        // Update reservation
        reservation.setAttended(attended);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setCompletedById(trainerId);
        reservation.setCompletedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        // Get session details for notification
        GymSession session = gymSessionRepository.findById(reservation.getSessionId())
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Send notification based on attendance
        if (attended) {
            notificationService.sendNotification(
                reservation.getUserId(), 
                "Asistencia Registrada", 
                "Su asistencia ha sido registrada para la sesión del " + 
                session.getSessionDate() + " de " + session.getStartTime() + " a " + session.getEndTime() + ".",
                "ATTENDANCE_RECORDED",
                Optional.of(reservationId)
            );
        } else {
            notificationService.sendNotification(
                reservation.getUserId(), 
                "Ausencia Registrada", 
                "Se ha registrado su ausencia para la sesión del " + 
                session.getSessionDate() + " de " + session.getStartTime() + " a " + session.getEndTime() + ".",
                "ATTENDANCE_MISSED",
                Optional.of(reservationId)
            );
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean addToWaitlist(UUID userId, UUID sessionId) {
        // Validate user and session exist
        if (!userRepository.existsById(userId)) {
            throw new PrometeoExceptions("Usuario no encontrado");
        }
        
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        // Check if session is active
        if (!"ACTIVE".equals(session.getStatus())) {
            throw new PrometeoExceptions("La sesión ha sido cancelada o no está disponible");
        }
        
        // Check if the date is in the past
        if (session.getSessionDate().isBefore(LocalDate.now())) {
            throw new PrometeoExceptions("No se puede añadir a lista de espera para fechas pasadas");
        }
        
        // Check if user already has a reservation for this session
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByUserIdFromDate(
                userId, now);
                
        boolean hasReservation = activeReservations.stream()
                .anyMatch(r -> r.getSessionId().equals(sessionId));
                
        if (hasReservation) {
            throw new PrometeoExceptions("Ya tiene una reserva para esta sesión");
        }
        
        // Check if user is already on the waitlist
        Optional<WaitlistEntry> existingEntry = waitlistEntryRepository.findByUserIdAndSessionId(userId, sessionId);
        if (existingEntry.isPresent()) {
            throw new PrometeoExceptions("Ya está en lista de espera para esta sesión");
        }
        
        // Add to waitlist
        WaitlistEntry entry = new WaitlistEntry();
        entry.setUserId(userId);
        entry.setSessionId(sessionId);
        entry.setRequestDate(LocalDateTime.now());
        waitlistEntryRepository.save(entry);
        
        // Send confirmation notification
        notificationService.sendNotification(
            userId, 
            "Añadido a Lista de Espera", 
            "Ha sido añadido a la lista de espera para la sesión del " + 
            session.getSessionDate() + " de " + session.getStartTime() + " a " + session.getEndTime() + ".",
            "WAITLIST_ADDED",
            Optional.of(sessionId)
        );
        
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getWaitlistForSession(UUID sessionId) {
        // Get all waitlist entries for the session
        List<WaitlistEntry> entries = waitlistEntryRepository.findBySessionIdOrderByRequestDateAsc(sessionId);
        
        // Convert to response format
        List<Map<String, Object>> result = new ArrayList<>();
        for (WaitlistEntry entry : entries) {
            Map<String, Object> entryMap = new HashMap<>();
            entryMap.put("id", entry.getId());
            entryMap.put("userId", entry.getUserId());
            entryMap.put("requestDate", entry.getRequestDate());
            entryMap.put("notified", entry.isNotified());
            
            // Get user details
            userRepository.findById(entry.getUserId()).ifPresent(user -> {
                entryMap.put("userName", user.getName());
                entryMap.put("userEmail", user.getName() + "@escuelaing.edu.co");
            });
            
            result.add(entryMap);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean notifyNextWaitlistedUser(UUID sessionId) {
        // Check if session has availability
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PrometeoExceptions("Sesión no encontrada"));
        
        if (!session.hasAvailability()) {
            return false; // No availability
        }
        
        // Get next waitlisted user (first in first out)
        List<WaitlistEntry> waitlistEntries = waitlistEntryRepository.findBySessionIdOrderByRequestDateAsc(sessionId);
        if (waitlistEntries.isEmpty()) {
            return false; // No one on waitlist
        }
        
        WaitlistEntry nextEntry = waitlistEntries.get(0);
        
        // Mark as notified
        nextEntry.setNotified(true);
        nextEntry.setNotificationDate(LocalDateTime.now());
        waitlistEntryRepository.save(nextEntry);
        
        // Send notification
        notificationService.sendNotification(
            nextEntry.getUserId(), 
            "Espacio disponible en Gimnasio", 
            "Hay un cupo disponible para la sesión del " + 
            session.getSessionDate() + " de " + session.getStartTime() + " a " + session.getEndTime() + 
            ". Por favor realice su reserva lo más pronto posible.",
            "WAITLIST_NOTIFICATION",
            Optional.of(sessionId)
        );
        
        return true;
    }
    
    @Override
    public List<ReservationDTO> getSessionReservations(UUID sessionId, Optional<ReservationStatus> status) {
        // Get all reservations for the session
        List<Reservation> reservations;
        if (status.isPresent()) {
            reservations = reservationRepository.findBySessionIdAndStatus(sessionId, status.get());
        } else {
            reservations = reservationRepository.findBySessionId(sessionId);
        }
        
        return enrichReservationsWithDetails(reservations);
    }
    
    private List<ReservationDTO> enrichReservationsWithDetails(List<Reservation> reservations) {
        List<ReservationDTO> result = new ArrayList<>();
        
        for (Reservation reservation : reservations) {
            ReservationDTO dto = mapToReservationDTO(reservation);
            result.add(dto);
        }
        
        return result;
    }
    
    private ReservationDTO mapToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setSessionId(reservation.getSessionId());
        dto.setStatus(reservation.getStatus());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setAttended(reservation.isAttended());
        
        // Get user details
        userRepository.findById(reservation.getUserId())
            .ifPresent(user -> {
                dto.setUserName(user.getName());
                dto.setUserRole(user.getRole());
            });
        
        // Get session details
        gymSessionRepository.findById(reservation.getSessionId())
            .ifPresent(session -> {
                dto.setSessionDate(session.getSessionDate());
                dto.setStartTime(session.getStartTime());
                dto.setEndTime(session.getEndTime());
                dto.setTrainerId(session.getTrainerId());
                
                // Get trainer details
                if (session.getTrainerId() != null) {
                    userRepository.findById(session.getTrainerId())
                        .ifPresent(trainer -> dto.setTrainerName(
                            trainer.getName()));
                }
            });
        
        // Get equipment details if any
        if (reservation.getEquipmentIds() != null && !reservation.getEquipmentIds().isEmpty()) {
            List<EquipmentDTO> equipmentList = new ArrayList<>();
            for (UUID equipmentId : reservation.getEquipmentIds()) {
                equipmentRepository.findById(equipmentId).ifPresent(equipment -> {
                    EquipmentDTO equipmentDTO = new EquipmentDTO();
                    equipmentDTO.setId(equipment.getId());
                    equipmentDTO.setName(equipment.getName());
                    equipmentDTO.setType(equipment.getType());
                    equipmentDTO.setStatus(equipment.getStatus());
                    equipmentList.add(equipmentDTO);
                });
            }
            dto.setEquipment(equipmentList);
        }
        
        return dto;
    }
    
    private List<UUID> validateAndReserveEquipment(
            List<UUID> equipmentIds, 
            LocalDate date, 
            LocalTime startTime, 
            LocalTime endTime) {
        
        List<UUID> validEquipmentIds = new ArrayList<>();
        
        for (UUID equipmentId : equipmentIds) {
            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new PrometeoExceptions("Equipo no encontrado: " + equipmentId));
            
            // Check if equipment is available
            if (!"AVAILABLE".equals(equipment.getStatus())) {
                throw new PrometeoExceptions("El equipo no está disponible: " + equipment.getName());
            }
            
            // Check if equipment is already reserved for this time
            boolean isAlreadyReserved = reservationRepository.isEquipmentReservedForTimeSlot(
                    equipmentId, date, startTime, endTime);
            
            if (isAlreadyReserved) {
                throw new PrometeoExceptions("El equipo ya está reservado para este horario: " + equipment.getName());
            }
            
            validEquipmentIds.add(equipmentId);
        }
        
        return validEquipmentIds;
    }
    
    @Override
    public List<GymSession> getAvailableSessionsByDate(LocalDate date) {
        // Find all active sessions for the given date that have availability
        return gymSessionRepository.findAvailableSessionsByDate(date);
    }
    
    @Override
    public boolean requestWaitlistNotification(UUID userId, UUID sessionId) {
        // Implementation as a wrapper around addToWaitlist
        return addToWaitlist(userId, sessionId);
    }
}