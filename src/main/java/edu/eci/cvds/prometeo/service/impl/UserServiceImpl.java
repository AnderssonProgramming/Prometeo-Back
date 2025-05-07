package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.dto.*;
import edu.eci.cvds.prometeo.model.*;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import edu.eci.cvds.prometeo.repository.*;
import edu.eci.cvds.prometeo.service.PhysicalProgressService;
import edu.eci.cvds.prometeo.service.RoutineService;
import edu.eci.cvds.prometeo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Implementación del servicio central de usuario que maneja todas las operaciones
 * relacionadas con usuarios, seguimiento físico, rutinas y reservas
 */
/**
 * Implementation of the UserService interface that provides comprehensive
 * functionality
 * for managing users in the fitness system.
 * 
 * This service handles multiple aspects of user management including:
 * - Basic user operations (retrieval, updates, trainer assignment)
 * - Physical progress tracking and measurements
 * - Fitness routine management and assignment
 * - Gym reservation system
 * - Fitness equipment administration
 * - Statistical reporting and analytics
 * 
 * The implementation relies on several repositories to interact with the
 * database:
 * - UserRepository: For core user data operations
 * - PhysicalProgressRepository: For storing and retrieving physical
 * measurements and progress
 * - RoutineRepository: For managing workout routines
 * - EquipmentRepository: For handling gym equipment information
 * 
 * @author Prometeo Team
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    // Repositories necesarios
    /**
     * Repository interface for User entity operations.
     * This field manages database interactions for user-related data,
     * providing methods for CRUD operations and custom queries on users.
     * It is injected as a dependency and marked as final to ensure immutability.
     */

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PhysicalProgressRepository physicalProgressRepository;
    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private GymSessionRepository gymSessionRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    

    @Autowired
    private PhysicalProgressService physicalProgressService; // Inyectar el servicio especializado
    @Autowired
    private RoutineService routineService; // Inyectar el servicio especializado para rutinas

    // ------------- Operaciones básicas de usuario -------------

    @Override
    public User getUserById(String institutionalId) {
        return userRepository.findByInstitutionalId(institutionalId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + institutionalId));
    }

    @Override
    public User getUserByInstitutionalId(String institutionalId) {
        return userRepository.findByInstitutionalId(institutionalId)
                .orElseThrow(() -> new RuntimeException("User not found with institutional id: " + institutionalId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User updateUser(String institutionalId, UserDTO user) {
        User existingUser = userRepository.findByInstitutionalId(institutionalId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + institutionalId));
        // Actualizar los campos necesarios
        existingUser.setName(user.getName());
        existingUser.setWeight(user.getWeight());
        existingUser.setHeight(user.getHeight());
        existingUser.setRole(user.getRole());
        // Guardar los cambios
        userRepository.save(existingUser);
        return existingUser;
    }

    @Override
    public User createUser(UserDTO userDTO) {
        // Create a new User entity from the DTO
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setInstitutionalId(userDTO.getInstitutionalId());
        newUser.setRole(userDTO.getRole());
        newUser.setWeight(userDTO.getWeight());
        newUser.setHeight(userDTO.getHeight());

        // Save the new user to the database
        return userRepository.save(newUser);
    }

    @Override
    public User deleteUser(String institutionalId) {
        User user = userRepository.findByInstitutionalId(institutionalId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + institutionalId));

        // Opcional: puedes realizar verificaciones adicionales antes de eliminar
        // Por ejemplo, verificar que el usuario no tiene reservas activas

        // Eliminar el usuario
        userRepository.delete(user);

        return user; // Devuelve el usuario eliminado
    }

    // ------------- Seguimiento físico -------------

    @Override
    @Transactional
    public PhysicalProgress recordPhysicalMeasurement(UUID userId, PhysicalProgress progress) {
        // Verifica que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Opcionalmente obtener la rutina activa del usuario
        Routine activeRoutine = routineRepository.findCurrentRoutineByUserId(userId).orElse(null);
        if (activeRoutine != null) {
            progress.setActiveRoutine(activeRoutine);
        }
        
        // Delega al servicio especializado
        return physicalProgressService.recordMeasurement(userId, progress);
    }

    @Override
    public List<PhysicalProgress> getPhysicalMeasurementHistory(UUID userId, Optional<LocalDate> startDate,
            Optional<LocalDate> endDate) {
        // Verifica que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Delega al servicio especializado
        return physicalProgressService.getMeasurementHistory(userId, startDate, endDate);
    }

    @Override
    public Optional<PhysicalProgress> getLatestPhysicalMeasurement(UUID userId) {
        // Delega al servicio especializado
        return physicalProgressService.getLatestMeasurement(userId);
    }

    @Override
    public PhysicalProgress updatePhysicalMeasurement(UUID progressId, BodyMeasurements measurements) {
        // Delega al servicio especializado
        return physicalProgressService.updateMeasurement(progressId, measurements);
    }

    @Override
    public PhysicalProgress setPhysicalGoal(UUID userId, String goal) {
        // Delega al servicio especializado
        return physicalProgressService.setGoal(userId, goal);
    }

    @Override
    public Map<String, Double> calculatePhysicalProgressMetrics(UUID userId, int months) {
        // Delega al servicio especializado
        return physicalProgressService.calculateProgressMetrics(userId, months);
    }

    // ------------- Gestión de rutinas -------------

    @Override
    public List<Routine> getUserRoutines(UUID userId) {
        // Delegar al servicio especializado para obtener todas las rutinas asignadas al usuario
        return routineService.getUserRoutines(userId, false);
    }
    
    @Override
    @Transactional
    public void assignRoutineToUser(UUID userId, UUID routineId) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // Delegar al servicio especializado para la asignación
        routineService.assignRoutineToUser(routineId, userId, null, 
                Optional.of(LocalDate.now()), Optional.of(LocalDate.now().plusMonths(3)));
    }
    
    @Override
    @Transactional
    public Routine createCustomRoutine(UUID userId, Routine routine) {
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // Obtener el entrenador (asumimos que hay un campo que indica si es entrenador)
        UUID trainerId = user.getRole().equals("TRAINER") ? userId : null;
        
        // Delegar al servicio especializado para la creación
        Routine createdRoutine = routineService.createRoutine(routine, Optional.ofNullable(trainerId));
        
        // Si el usuario no es entrenador, asignarle la rutina creada
        if (trainerId == null) {
            assignRoutineToUser(userId, createdRoutine.getId());
        }
        
        return createdRoutine;
    }
    
    @Override
    @Transactional
    public Routine updateRoutine(UUID routineId, Routine routine) {
        // Delegar al servicio especializado
        return routineService.updateRoutine(routineId, routine, null);
    }
    
    @Override
    @Transactional
    public boolean logRoutineProgress(UUID userId, UUID routineId, int completed) {
        // Verificar que el usuario y la rutina existen
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // Aquí podríamos actualizar estadísticas de progreso
        // Por simplicidad, solo registramos que se completó
        return true;
    }
    
    // @Override
    public List<Routine> getRecommendedRoutines(UUID userId) {
        // TODO: Implementar lógica de recomendación
    //     // Obtener el usuario
    //     User user = userRepository.findById(userId)
    //             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
    //     // Obtener rutinas basadas en objetivos similares o dificultad apropiada
    //     return routineService.getRoutines(Optional.ofNullable(user.getGoal()), Optional.empty());
        return null;
    }

    // ------------- Reservas de gimnasio -------------

    @Override
public UUID createGymReservation(UUID userId, LocalDate date, LocalTime startTime, LocalTime endTime, Optional<List<UUID>> equipmentIds) {
    // Verificar que el usuario existe
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    // Verificar disponibilidad de cupo
    if (!checkGymAvailability(date, startTime, endTime)) {
        throw new RuntimeException("No hay cupos disponibles para esta sesión");
    }
    
    // Buscar la sesión apropiada
    GymSession session = gymSessionRepository
            .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                date, startTime, endTime)
            .orElseThrow(() -> new RuntimeException("No existe sesión para el horario solicitado"));
    
    // Verificar si hay capacidad
    if (session.getReservedSpots() >= session.getCapacity()) {
        throw new RuntimeException("La sesión está a máxima capacidad");
    }
    
    // Crear la reserva
    Reservation reservation = new Reservation();
    reservation.setUserId(userId);
    reservation.setSessionId(session.getId());
    reservation.setReservationDate(LocalDateTime.of(date, startTime));
    reservation.setStatus(ReservationStatus.CONFIRMED);
    
    // Añadir equipos si se especificaron
    if (equipmentIds.isPresent() && !equipmentIds.get().isEmpty()) {
        reservation.setEquipmentIds(equipmentIds.get());
    }
    
    // Actualizar la capacidad de la sesión
    session.setReservedSpots(session.getReservedSpots() + 1);
    gymSessionRepository.save(session);
    
    // Guardar la reserva
    Reservation savedReservation = reservationRepository.save(reservation);
    
    // Opcionalmente enviar notificación
    // notificationService.sendNotification(...);
    
    return savedReservation.getId();
}

@Override
public boolean cancelGymReservation(UUID reservationId, UUID userId, Optional<String> reason) {
    // Buscar la reserva
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    
    // Verificar que el usuario es el propietario
    if (!reservation.getUserId().equals(userId)) {
        throw new RuntimeException("Usuario no autorizado para cancelar esta reserva");
    }
    
    // Verificar que la reserva no está ya cancelada
    if (reservation.getStatus() == ReservationStatus.CANCELLED) {
        throw new RuntimeException("La reserva ya está cancelada");
    }
    
    // Liberar el cupo en la sesión
    GymSession session = gymSessionRepository.findById(reservation.getSessionId())
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
    
    session.setReservedSpots(session.getReservedSpots() - 1);
    gymSessionRepository.save(session);
    
    // Actualizar la reserva
    reservation.setStatus(ReservationStatus.CANCELLED);
    reservation.setCanceledAt(LocalDateTime.now());
    reason.ifPresent(reservation::setCancellationReason);
    
    reservationRepository.save(reservation);
    
    return true;
}

@Override
public List<Object> getUpcomingReservations(UUID userId) {
    // Obtener reservas futuras del usuario
    LocalDate today = LocalDate.now();
    List<Reservation> reservations = reservationRepository
            .findByUserIdAndReservationDateGreaterThanEqualAndStatusOrderByReservationDateAsc(
                userId, LocalDateTime.now(), ReservationStatus.CONFIRMED);
    
    return convertReservationsToMaps(reservations);
}

@Override
public List<Object> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
    LocalDate start = startDate.orElse(LocalDate.now().minusMonths(3));
    LocalDate end = endDate.orElse(LocalDate.now());
    
    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atTime(23, 59, 59);
    
    List<Reservation> reservations = reservationRepository
            .findByUserIdAndReservationDateBetweenOrderByReservationDateDesc(
                userId, startDateTime, endDateTime);
    
    return convertReservationsToMaps(reservations);
}

@Override
public boolean checkGymAvailability(LocalDate date, LocalTime startTime, LocalTime endTime) {
    // Verificar si hay una sesión disponible
    Optional<GymSession> sessionOpt = gymSessionRepository
            .findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                date, startTime, endTime);
    
    if (sessionOpt.isEmpty()) {
        return false; // No hay sesión para ese horario
    }
    
    GymSession session = sessionOpt.get();
    return session.getReservedSpots() < session.getCapacity();
}

@Override
public List<Object> getAvailableTimeSlots(LocalDate date) {
    // Obtener todas las sesiones para la fecha
    List<GymSession> sessions = gymSessionRepository.findBySessionDateOrderByStartTime(date);
    List<Object> availableSlots = new ArrayList<>();
    
    for (GymSession session : sessions) {
        // Solo incluir sesiones que aún tengan cupo
        if (session.getReservedSpots() < session.getCapacity()) {
            Map<String, Object> slot = new HashMap<>();
            slot.put("sessionId", session.getId());
            slot.put("date", session.getSessionDate());
            slot.put("startTime", session.getStartTime());
            slot.put("endTime", session.getEndTime());
            slot.put("availableSpots", session.getCapacity() - session.getReservedSpots());
            slot.put("totalCapacity", session.getCapacity());
            availableSlots.add(slot);
        }
    }
    
    return availableSlots;
}

// Método auxiliar para convertir reservas a maps
private List<Object> convertReservationsToMaps(List<Reservation> reservations) {
    List<Object> result = new ArrayList<>();
    
    for (Reservation reservation : reservations) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", reservation.getId());
        map.put("date", reservation.getReservationDate().toLocalDate());
        map.put("time", reservation.getReservationDate().toLocalTime());
        map.put("status", reservation.getStatus());
        
        // Añadir detalles de la sesión
        GymSession session = gymSessionRepository.findById(reservation.getSessionId())
                .orElse(null);
        
        if (session != null) {
            Map<String, Object> sessionDetails = new HashMap<>();
            sessionDetails.put("id", session.getId());
            sessionDetails.put("startTime", session.getStartTime());
            sessionDetails.put("endTime", session.getEndTime());
            sessionDetails.put("capacity", session.getCapacity());
            
            map.put("session", sessionDetails);
        }
        
        result.add(map);
    }
    
    return result;
}

@Override
@Transactional
public boolean recordGymAttendance(UUID userId, UUID reservationId, LocalDateTime attendanceTime) {
    // Verificar que el usuario existe
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    // Buscar la reserva
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    
    // Verificar que la reserva corresponde al usuario
    if (!reservation.getUserId().equals(userId)) {
        throw new RuntimeException("La reserva no corresponde a este usuario");
    }
    
    // Verificar que la reserva está confirmada
    if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
        throw new RuntimeException("No se puede registrar asistencia para una reserva no confirmada");
    }
    
    // Verificar que la fecha/hora de asistencia es cercana a la fecha de la reserva
    LocalDate reservationDate = reservation.getReservationDate().toLocalDate();
    if (!attendanceTime.toLocalDate().equals(reservationDate)) {
        throw new RuntimeException("La fecha de asistencia no coincide con la fecha de reserva");
    }
    
    // Buscar la sesión para verificar el horario
    GymSession session = gymSessionRepository.findById(reservation.getSessionId())
            .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
    
    // Verificar que la hora de asistencia está dentro del rango de la sesión (con un margen de 15 minutos)
    LocalTime attendanceLocalTime = attendanceTime.toLocalTime();
    LocalTime sessionStartTime = session.getStartTime().minusMinutes(15);
    LocalTime sessionEndTime = session.getEndTime();
    
    if (attendanceLocalTime.isBefore(sessionStartTime) || attendanceLocalTime.isAfter(sessionEndTime)) {
        throw new RuntimeException("La hora de asistencia está fuera del horario permitido para la sesión");
    }
    
    // Actualizar la reserva para marcarla como asistida
    reservation.setAttended(true);
    reservation.setAttendanceTime(attendanceTime);
    reservationRepository.save(reservation);
    
    // Registrar en el historial de asistencia (opcional, si tienes otra tabla para esto)
    // attendanceHistoryRepository.save(new AttendanceHistory(userId, reservationId, attendanceTime));
    
    return true;
}
    // ------------- Administración de equipos -------------

    @Override
    public List<EquipmentDTO> getAllEquipment() {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public Optional<EquipmentDTO> getEquipmentById(UUID id) {
        // TODO: Implementar este método
        return Optional.empty();
    }

    @Override
    public EquipmentDTO saveEquipment(EquipmentDTO equipment) {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public EquipmentDTO updateEquipment(EquipmentDTO equipment) {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public EquipmentDTO sendEquipmentToMaintenance(UUID equipmentId, LocalDate endDate) {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public EquipmentDTO completeEquipmentMaintenance(UUID equipmentId) {
        // TODO: Implementar este método
        return null;
    }

    // ------------- Reportes y estadísticas -------------

    @Override
    public Map<String, Object> generateAttendanceReport(UUID userId, LocalDate startDate, LocalDate endDate) {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public Map<String, Object> generatePhysicalEvolutionReport(UUID userId, LocalDate startDate, LocalDate endDate) {
        // TODO: Implementar este método
        return null;
    }

    @Override
    public Map<String, Object> generateGymUsageStatistics(LocalDate startDate, LocalDate endDate) {
        // TODO: Implementar este método
        return null;
    }
}