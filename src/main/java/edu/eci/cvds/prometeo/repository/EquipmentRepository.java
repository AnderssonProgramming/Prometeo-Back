package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for managing Equipment entities
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    
    /**
     * Find equipment by type
     */
    List<Equipment> findByType(String type);
    
    /**
     * Find equipment by status
     */
    List<Equipment> findByStatus(String status);
    
    /**
     * Check if equipment is available at specific date/time
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Equipment e " +
           "WHERE e.id = :equipmentId AND e.status = 'AVAILABLE' " +
           "AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE :equipmentId MEMBER OF r.equipmentIds " +
           "AND r.date = :date AND r.status = 'CONFIRMED' " +
           "AND ((r.startTime < :endTime AND r.endTime > :startTime)))")
    boolean isEquipmentAvailable(
            @Param("equipmentId") UUID equipmentId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    /**
     * Find available equipment by date/time
     */
    @Query("SELECT e FROM Equipment e WHERE e.status = 'AVAILABLE' " +
           "AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE e.id MEMBER OF r.equipmentIds " +
           "AND r.date = :date AND r.status = 'CONFIRMED' " +
           "AND ((r.startTime < :endTime AND r.endTime > :startTime)))")
    List<Equipment> findAvailableEquipmentByDateTime(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    /**
     * Find available equipment by type and date/time
     */
    @Query("SELECT e FROM Equipment e WHERE e.type = :type AND e.status = 'AVAILABLE' " +
           "AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE e.id MEMBER OF r.equipmentIds " +
           "AND r.date = :date AND r.status = 'CONFIRMED' " +
           "AND ((r.startTime < :endTime AND r.endTime > :startTime)))")
    List<Equipment> findAvailableEquipmentByTypeAndDateTime(
            @Param("type") String type,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}