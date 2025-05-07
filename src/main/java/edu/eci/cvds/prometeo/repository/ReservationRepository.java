package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    List<Reservation> findBySessionId(UUID sessionId);
    
    List<Reservation> findBySessionIdAndStatus(UUID sessionId, ReservationStatus status);
    
    Optional<Reservation> findBySessionIdAndUserId(UUID sessionId, UUID userId);
    
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.reservationDate >= :date AND r.status = 'CONFIRMED'")
    List<Reservation> findActiveReservationsByUserIdFromDate(@Param("userId") UUID userId, @Param("date") LocalDateTime date);
    
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.reservationDate >= :startDate AND r.reservationDate <= :endDate ORDER BY r.reservationDate DESC")
    List<Reservation> findByUserIdAndDateBetweenOrderByDateDesc(
            @Param("userId") UUID userId, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.reservationDate >= :date AND r.status = :status ORDER BY r.reservationDate ASC")
    List<Reservation> findByUserIdAndDateGreaterThanEqualAndStatusOrderByDateAsc(
            @Param("userId") UUID userId, 
            @Param("date") LocalDateTime date,
            @Param("status") ReservationStatus status);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
           "WHERE :equipmentId MEMBER OF r.equipmentIds " +
           "AND :date = FUNCTION('date', r.reservationDate) " +
           "AND ((FUNCTION('time', r.reservationDate) <= :endTime AND " +
           "FUNCTION('time', r.reservationDate) >= :startTime) " +
           "OR (FUNCTION('time', r.reservationDate) <= :endTime AND " +
           "FUNCTION('time', r.reservationDate) >= :startTime)) " +
           "AND r.status = 'CONFIRMED'")
    boolean isEquipmentReservedForTimeSlot(
            @Param("equipmentId") UUID equipmentId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}