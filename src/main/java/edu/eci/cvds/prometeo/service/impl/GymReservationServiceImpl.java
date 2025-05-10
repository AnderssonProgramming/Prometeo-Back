package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.ReservationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.GymReservationService;
import edu.eci.cvds.prometeo.service.NotificationService;
import edu.eci.cvds.prometeo.service.WaitlistService;
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

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private GymSessionRepository gymSessionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private WaitlistService waitlistService;
    
    // Constantes
    private static final int MAX_ACTIVE_RESERVATIONS_PER_USER = 5;
    private static final int MIN_HOURS_BEFORE_CANCELLATION = 2;
    
    @Override
    public List<ReservationDTO> getAll() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ReservationDTO> getByUserId(UUID userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ReservationDTO> getById(UUID id) {
        return reservationRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    public ReservationDTO create(ReservationDTO dto) {
        // Validar que la sesión exista
        GymSession session = gymSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException(PrometeoExceptions.NO_EXISTE_SESION));
        
        // Validar que el usuario exista
        if (!userRepository.existsById(dto.getUserId())) {
            throw new IllegalArgumentException(PrometeoExceptions.NO_EXISTE_USUARIO);
        }
        
        // Validar que la sesión tenga cupo disponible
        if (session.getReservedSpots() >= session.getCapacity()) {
            throw new IllegalArgumentException(PrometeoExceptions.CAPACIDAD_EXCEDIDA);
        }
        
        // Validar que el usuario no tenga demasiadas reservas activas
        long activeReservations = reservationRepository.countByUserIdAndStatusIn(
                dto.getUserId(), 
                Arrays.asList(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN)
        );
        
        if (activeReservations >= MAX_ACTIVE_RESERVATIONS_PER_USER) {
            throw new IllegalArgumentException(PrometeoExceptions.LIMITE_RESERVAS_ALCANZADO);
        }
        
        // Validar que la fecha de la sesión no sea en el pasado
        LocalDateTime sessionDateTime = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        if (sessionDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(PrometeoExceptions.FECHA_PASADA);
        }
        
        // Crear reserva
        Reservation reservation = new Reservation();
        reservation.setUserId(dto.getUserId());
        reservation.setSessionId(dto.getSessionId());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setEquipmentIds(dto.getEquipmentIds());
        reservation.setNotes(dto.getNotes());
        
        // Incrementar contador de reservas en la sesión
        session.setReservedSpots(session.getReservedSpots() + 1);
        gymSessionRepository.save(session);
        
        // Guardar la reserva
        Reservation saved = reservationRepository.save(reservation);
        
        // Enviar confirmación
        notificationService.sendReservationConfirmation(dto.getUserId(), saved.getId());
        
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException(PrometeoExceptions.NO_EXISTE_RESERVA);
        }
        
        Reservation reservation = reservationOpt.get();
        
        // Validar que la reserva no esté ya cancelada
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException(PrometeoExceptions.RESERVA_YA_CANCELADA);
        }
        
        // Validar que la sesión no haya pasado ya
        Optional<GymSession> sessionOpt = gymSessionRepository.findById(reservation.getSessionId());
        
        if (sessionOpt.isPresent()) {
            GymSession session = sessionOpt.get();
            LocalDateTime sessionDateTime = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
            
            if (sessionDateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException(PrometeoExceptions.NO_CANCELAR_RESERVAS_PASADAS);
            }
            
            // Validar tiempo mínimo para cancelación
            LocalDateTime minCancellationTime = sessionDateTime.minusHours(MIN_HOURS_BEFORE_CANCELLATION);
            if (LocalDateTime.now().isAfter(minCancellationTime)) {
                throw new IllegalArgumentException(PrometeoExceptions.CANCELACION_TARDIA);
            }
            
            // Actualizar contador de reservas en la sesión
            session.setReservedSpots(Math.max(0, session.getReservedSpots() - 1));
            gymSessionRepository.save(session);
            
            // Notificar a la siguiente persona en la lista de espera
            waitlistService.notifyNextInWaitlist(session.getId());
        }
        
        // Cancelar la reserva
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationDate(LocalDateTime.now());
        reservationRepository.save(reservation);
    }
    
    @Override
    public Map<String, Object> getAvailability(LocalDate date, LocalTime time) {
        Map<String, Object> result = new HashMap<>();
        
        // Encontrar sesiones que incluyan la hora solicitada
        List<GymSession> sessions = gymSessionRepository.findBySessionDate(date);
        
        List<GymSession> availableSessions = sessions.stream()
                .filter(session -> !session.getStartTime().isAfter(time) && !session.getEndTime().isBefore(time))
                .filter(session -> session.getReservedSpots() < session.getCapacity())
                .collect(Collectors.toList());
        
        // Preparar respuesta
        result.put("date", date);
        result.put("requestedTime", time);
        result.put("availableSessions", availableSessions.stream().map(session -> {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("startTime", session.getStartTime());
            sessionMap.put("endTime", session.getEndTime());
            sessionMap.put("capacity", session.getCapacity());
            sessionMap.put("availableSpots", session.getCapacity() - session.getReservedSpots());
            sessionMap.put("trainerId", session.getTrainerId());
            return sessionMap;
        }).collect(Collectors.toList()));
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean joinWaitlist(UUID userId, UUID sessionId) {
        // Verificar que la sesión exista
        GymSession session = gymSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException(PrometeoExceptions.NO_EXISTE_SESION));
        
        // Verificar que la sesión esté a capacidad máxima
        if (session.getReservedSpots() < session.getCapacity()) {
            throw new IllegalArgumentException("La sesión aún tiene cupos disponibles, por favor reserve directamente");
        }
        
        // Agregar a la lista de espera
        waitlistService.addToWaitlist(userId, sessionId);
        
        return true;
    }
    
    @Override
    public Map<String, Object> getWaitlistStatus(UUID userId, UUID sessionId) {
        Map<String, Object> result = new HashMap<>();
        
        int position = waitlistService.getWaitlistPosition(userId, sessionId);
        result.put("inWaitlist", position > 0);
        result.put("position", position);
        
        // Obtener detalles de la sesión
        gymSessionRepository.findById(sessionId).ifPresent(session -> {
            result.put("sessionDate", session.getSessionDate());
            result.put("startTime", session.getStartTime());
            result.put("endTime", session.getEndTime());
            result.put("capacity", session.getCapacity());
            result.put("reservedSpots", session.getReservedSpots());
            
            // Stats de la lista de espera
            Map<String, Object> waitlistStats = waitlistService.getWaitlistStats(sessionId);
            result.put("totalInWaitlist", waitlistStats.get("totalCount"));
        });
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getUserWaitlists(UUID userId) {
        return waitlistService.getUserWaitlistSessions(userId);
    }
    
    @Override
    @Transactional
    public boolean leaveWaitlist(UUID userId, UUID sessionId) {
        return waitlistService.removeFromWaitlist(userId, sessionId);
    }
    
    // Método auxiliar para convertir Entidad a DTO
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUserId());
        dto.setSessionId(reservation.getSessionId());
        dto.setStatus(reservation.getStatus());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setCancellationDate(reservation.getCancellationDate());
        dto.setCheckInTime(reservation.getCheckInTime());
        dto.setEquipmentIds(reservation.getEquipmentIds());
        dto.setNotes(reservation.getNotes());
        return dto;
    }
}