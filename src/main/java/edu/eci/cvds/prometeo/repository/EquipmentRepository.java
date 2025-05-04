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
     * Checks if equipment is available for reservation at a specific date and time
     * @param equipmentId ID of the equipment
     * @param date Date for the reservation
     * @param startTime Start time of the reservation
     * @param endTime End time of the reservation
     * @return true if the equipment is available
     */
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END FROM Reservation r " +
           "JOIN r.equipmentIds e " +
           "WHERE e = :equipmentId " +
           "AND r.date = :date " +
           "AND r.status = 'CONFIRMED' " +
           "AND ((r.startTime <= :endTime AND r.endTime >= :startTime))")
    boolean isEquipmentAvailable(
            @Param("equipmentId") UUID equipmentId, 
            @Param("date") LocalDate date, 
            @Param("startTime") LocalTime startTime, 
            @Param("endTime") LocalTime endTime);
    
    /**
     * Finds all equipment by type
     * @param equipmentType Type of equipment
     * @return List of equipment of the specified type
     */
    List<Equipment> findByEquipmentType(String equipmentType);
    
    /**
     * Finds all available equipment
     * @return List of equipment with available status
     */
    List<Equipment> findByStatus(String status);
    
    /**
     * Finds all equipment in a specific location
     * @param location Location to search
     * @return List of equipment in the location
     */
    List<Equipment> findByLocation(String location);
    
    /**
     * Finds all equipment that targets a specific muscle group
     * @param muscleGroup Muscle group to target
     * @return List of equipment targeting the muscle group
     */
    @Query("SELECT e FROM Equipment e WHERE e.primaryMuscleGroup = :muscleGroup " +
           "OR e.secondaryMuscleGroups LIKE %:muscleGroup%")
    List<Equipment> findByMuscleGroup(@Param("muscleGroup") String muscleGroup);
    
    /**
     * Finds equipment that requires maintenance
     * @param currentDate Reference date
     * @return List of equipment due for maintenance
     */
    @Query("SELECT e FROM Equipment e WHERE e.nextMaintenanceDate <= :currentDate " +
           "AND e.status != 'MAINTENANCE'")
    List<Equipment> findEquipmentDueForMaintenance(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Finds most reserved equipment in a date range
     * @param startDate Start date
     * @param endDate End date
     * @param limit Maximum number of results
     * @return List of equipment IDs with their reservation count
     */
    @Query(value = "SELECT e.id, COUNT(r.id) as count FROM equipment e " +
                  "JOIN reservation_equipment re ON e.id = re.equipment_id " +
                  "JOIN reservations r ON re.reservation_id = r.id " +
                  "WHERE r.date BETWEEN :startDate AND :endDate " +
                  "GROUP BY e.id ORDER BY count DESC LIMIT :limit", 
           nativeQuery = true)
    List<Object[]> findMostReservedEquipment(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            @Param("limit") int limit);
}