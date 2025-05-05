package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    // Existing methods
    List<Reservation> findByUserId(UUID userId);
    
    List<Reservation> findBySessionId(UUID sessionId);
    
    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    
    List<Reservation> findBySessionIdAndStatus(UUID sessionId, ReservationStatus status);
    
    List<Reservation> findBySessionIdAndUserId(UUID sessionId, UUID userId);
    
    Long countBySessionId(UUID sessionId);
    
    Long countBySessionIdAndStatus(UUID sessionId, ReservationStatus status);
    
    // Additional methods needed by GymReservationServiceImpl
    
    /**
     * Finds reservations for a user with date on or after a specific date and not having a specific status
     * Used to find active (non-cancelled) reservations
     * 
     * @param userId User ID
     * @param date Current date or reference date
     * @param status Status to exclude (e.g., "CANCELLED")
     * @return List of matching reservations
     */
    List<Reservation> findByUserIdAndDateGreaterThanEqualAndStatusNot(
            UUID userId, LocalDate date, String status);
    
    /**
     * Finds upcoming reservations for a user with a specific status, ordered by date and time
     * 
     * @param userId User ID
     * @param date Current date or reference date
     * @param status Status to filter by (e.g., "CONFIRMED")
     * @return List of matching reservations ordered by date and time ascending
     */
    List<Reservation> findByUserIdAndDateGreaterThanEqualAndStatusOrderByDateAscStartTimeAsc(
            UUID userId, LocalDate date, String status);
    
    /**
     * Finds reservations for a user within a date range, ordered by date and time descending
     * Used for reservation history
     * 
     * @param userId User ID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @return List of matching reservations ordered by date and time descending
     */
    List<Reservation> findByUserIdAndDateBetweenOrderByDateDescStartTimeDesc(
            UUID userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Checks if there are any overlapping reservations for a specific equipment during a time period
     * 
     * @param equipmentId Equipment ID
     * @param date Date to check
     * @param startTime Start time
     * @param endTime End time
     * @return Count of overlapping reservations
     */
    @Query("SELECT COUNT(r) FROM Reservation r " +
           "JOIN r.equipmentIds e " +
           "WHERE e = :equipmentId " +
           "AND r.date = :date " +
           "AND r.status = 'CONFIRMED' " +
           "AND (r.startTime < :endTime AND r.endTime > :startTime)")
    Long countOverlappingReservationsByEquipment(
            @Param("equipmentId") UUID equipmentId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    /**
     * Finds all reservations for a specific date
     * 
     * @param date Date to search for
     * @return List of reservations on that date
     */
    List<Reservation> findByDate(LocalDate date);
    
    /**
     * Finds all reservations for a date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations in that date range
     */
    List<Reservation> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count reservations by user ID and date and status
     */
    int countByUserIdAndDateAndStatus(UUID userId, LocalDate date, String status);
    
    /**
     * Find reservations by equipment ID and date and status
     */
    List<Reservation> findByEquipmentIdsContainingAndDateAndStatus(
            UUID equipmentId, LocalDate date, String status);
}