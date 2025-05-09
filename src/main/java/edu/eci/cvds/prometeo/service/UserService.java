package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.*;
import edu.eci.cvds.prometeo.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio central para operaciones de usuario y todas las funcionalidades relacionadas
 */
/**
 * Service interface for user management and related functionalities in the Prometeo system.
 * <p>
 * This service provides operations for:
 * <ul>
 *   <li>User profile management (get, update, assign)</li>
 *   <li>Physical progress tracking and measurements</li>
 *   <li>Fitness routine management and assignment</li>
 *   <li>Gym reservation system operations</li>
 *   <li>Gym equipment administration</li>
 *   <li>Statistical reports and analysis</li>
 * </ul>
 * <p>
 * The interface supports operations for both regular users and trainers,
 * including relationship management between them. It provides a comprehensive
 * set of methods to handle all user-related aspects of the fitness management system.
 *
 * @author Prometeo Team
 * @version 1.0
 */
public interface UserService {
    
    // ------------- Operaciones básicas de usuario -------------

    /**
     * Obtener usuario por ID
     * @param id ID del usuario
     * @return Entidad de usuario
     */
    User getUserById(UUID id);

    /**
     * Obtener usuario por ID de tarjeta de identificación
     * @param idCard ID de tarjeta de identificación del usuario
     * @return Entidad de usuario
     */

    /**
     * Obtener usuario por ID institucional
     * @param institutionalId ID institucional del usuario
     * @return Entidad de usuario
     */
    User getUserByInstitutionalId(String institutionalId);

    /**
     * Obtener todos los usuarios
     * @return lista de todos los usuarios registrados
     */
    List<User> getAllUsers();
    
    /**
     * Obtener usuarios por rol
     * @param role rol de usuario a filtrar
     * @return lista de usuarios con el rol especificado
     */
    List<User> getUsersByRole(String role);

    /**
     * Crear un nuevo usuario
     * @param userDTO datos del usuario a crear
     * @return nuevo usuario creado
     */
    User createUser(UserDTO userDTO);
    
    /**
     * Actualizar información de perfil de usuario
     * @param id ID del usuario
     * @param profileDTO datos de perfil a actualizar
     * @return perfil actualizado
     */
    User updateUser(UUID id, UserDTO user);
    
    /**
     * Eliminar usuario
     * @param id ID del usuario
     * @return usuario eliminado
     */
    User deleteUser(UUID id);
    /**
     * Obtener lista de usuarios asignados al entrenador actual
     * @return lista de perfiles de usuario
     */

     // TODO: Validar si la asignación de entrenadores es por sesión de gym
    // List<User> getTrainerAssignedUsers();
    
    // /**
    //  * Asignar un usuario a un entrenador
    //  * @param userId ID del usuario
    //  * @param trainerId ID del entrenador
    //  */
    // void assignUserToTrainer(Long userId, Long trainerId);
    
    // ------------- Seguimiento físico -------------
    
    /**
     * Registrar nueva medición física
     * @param userId ID del usuario
     * @param progress datos de progreso físico
     * @return progreso registrado
     */
    PhysicalProgress recordPhysicalMeasurement(UUID userId, PhysicalProgress progress);
    
    /**
     * Obtener historial de mediciones físicas
     * @param userId ID del usuario
     * @param startDate fecha de inicio opcional
     * @param endDate fecha de fin opcional
     * @return lista de mediciones
     */
    List<PhysicalProgress> getPhysicalMeasurementHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Obtener última medición física
     * @param userId ID del usuario
     * @return última medición
     */
    Optional<PhysicalProgress> getLatestPhysicalMeasurement(UUID userId);
    
    /**
     * Actualizar medición física existente
     * @param progressId ID de la medición
     * @param measurements nuevas medidas
     * @return medición actualizada
     */
    PhysicalProgress updatePhysicalMeasurement(UUID progressId, BodyMeasurements measurements);
    
    /**
     * Establecer meta física
     * @param userId ID del usuario
     * @param goal descripción de la meta
     * @return progreso con meta establecida
     */
    PhysicalProgress setPhysicalGoal(UUID userId, String goal);
    
    /**
     * Registrar observación médica
     * @param userId ID del usuario
     * @param observation texto de la observación
     * @param trainerId ID del entrenador que registra
     * @return progreso con observación
     */
    PhysicalProgress recordMedicalObservation(UUID userId, String observation, UUID trainerId);
    
    /**
     * Calcular métricas de progreso
     * @param userId ID del usuario
     * @param months número de meses a analizar
     * @return mapa de nombres de métricas a valores
     */
    Map<String, Double> calculatePhysicalProgressMetrics(UUID userId, int months);
    
    // ------------- Gestión de rutinas -------------
    
    /**
     * Obtener rutinas de un usuario
     * @param userId ID del usuario
     * @return lista de rutinas
     */
    List<Routine> getUserRoutines(UUID userId);
    
    /**
     * Asignar rutina a usuario
     * @param userId ID del usuario
     * @param routineId ID de la rutina
     */
    void assignRoutineToUser(UUID userId, UUID routineId);
    
    /**
     * Crear rutina personalizada
     * @param userId ID del usuario
     * @param routine datos de la rutina
     * @return rutina creada
     */
    Routine createCustomRoutine(UUID userId, Routine routine);
    
    /**
     * Actualizar rutina existente
     * @param routineId ID de la rutina
     * @param routine datos actualizados
     * @return rutina actualizada
     */
    Routine updateRoutine(UUID routineId, Routine routine);
    
    /**
     * Registrar progreso en rutina
     * @param userId ID del usuario
     * @param routineId ID de la rutina
     * @param completed porcentaje completado
     * @return estado actualizado
     */
    boolean logRoutineProgress(UUID userId, UUID routineId, int completed);
    
    /**
     * Obtener rutinas recomendadas para un usuario
     * @param userId ID del usuario
     * @return lista de rutinas recomendadas
     */
    List<Routine> getRecommendedRoutines(UUID userId);
    
    // ------------- Reservas de gimnasio -------------
    
    /**
     * Crear reserva de gimnasio
     * @param userId ID del usuario
     * @param date fecha de la reserva
     * @param startTime hora de inicio
     * @param endTime hora de fin
     * @param equipmentIds IDs de equipos opcionales
     * @return ID de la reserva creada
     */
    UUID createGymReservation(UUID userId, LocalDate date, LocalTime startTime, LocalTime endTime, Optional<List<UUID>> equipmentIds);
    
    /**
     * Cancelar reserva
     * @param reservationId ID de la reserva
     * @param userId ID del usuario
     * @param reason motivo opcional de cancelación
     * @return true si se canceló correctamente
     */
    boolean cancelGymReservation(UUID reservationId, UUID userId, Optional<String> reason);
    
    /**
     * Obtener próximas reservas
     * @param userId ID del usuario
     * @return lista de reservas
     */
    List<Object> getUpcomingReservations(UUID userId);
    
    /**
     * Obtener historial de reservas
     * @param userId ID del usuario
     * @param startDate fecha de inicio opcional
     * @param endDate fecha de fin opcional
     * @return lista de reservas
     */
    List<Object> getReservationHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    
    /**
     * Verificar disponibilidad de horarios
     * @param date fecha a consultar
     * @param startTime hora de inicio
     * @param endTime hora de fin
     * @return true si está disponible
     */
    boolean checkGymAvailability(LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * Obtener slots de tiempo disponibles
     * @param date fecha a consultar
     * @return lista de slots disponibles
     */
    List<Object> getAvailableTimeSlots(LocalDate date);
    
    /**
     * Registrar asistencia a reserva
     * @param reservationId ID de la reserva
     * @param attended true si asistió
     * @param trainerId ID del entrenador que registra
     * @return true si se registró correctamente
     */
    boolean recordGymAttendance(UUID reservationId, boolean attended, UUID trainerId);
    
    // ------------- Administración de equipos -------------
    
    /**
     * Obtener todos los equipos
     * @return lista de equipos
     */
    List<EquipmentDTO> getAllEquipment();
    
    /**
     * Obtener equipo por ID
     * @param id ID del equipo
     * @return equipo encontrado
     */
    Optional<EquipmentDTO> getEquipmentById(UUID id);
    
    /**
     * Guardar nuevo equipo
     * @param equipment datos del equipo
     * @return equipo guardado
     */
    EquipmentDTO saveEquipment(EquipmentDTO equipment);
    
    /**
     * Actualizar equipo existente
     * @param equipment datos actualizados
     * @return equipo actualizado
     */
    EquipmentDTO updateEquipment(EquipmentDTO equipment);
    
    /**
     * Enviar equipo a mantenimiento
     * @param equipmentId ID del equipo
     * @param endDate fecha estimada de fin
     * @return equipo actualizado
     */
    EquipmentDTO sendEquipmentToMaintenance(UUID equipmentId, LocalDate endDate);
    
    /**
     * Finalizar mantenimiento de equipo
     * @param equipmentId ID del equipo
     * @return equipo actualizado
     */
    EquipmentDTO completeEquipmentMaintenance(UUID equipmentId);
    
    // ------------- Reportes y estadísticas -------------
    
    /**
     * Generar reporte de asistencia
     * @param userId ID del usuario
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return datos del reporte
     */
    Map<String, Object> generateAttendanceReport(UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generar reporte de evolución física
     * @param userId ID del usuario
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return datos del reporte
     */
    Map<String, Object> generatePhysicalEvolutionReport(UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generar estadísticas de uso de gimnasio
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return datos estadísticos
     */
    Map<String, Object> generateGymUsageStatistics(LocalDate startDate, LocalDate endDate);
}