package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.EquipmentDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for equipment management
 */
public interface EquipmentService {
    
    /**
     * Get all equipment
     * @return List of all equipment
     */
    List<EquipmentDTO> getAll();
    
    /**
     * Get equipment by ID
     * @param id equipment ID
     * @return Optional containing equipment if found
     */
    Optional<EquipmentDTO> getById(UUID id);
    
    /**
     * Get all available equipment
     * @return List of available equipment
     */
    List<EquipmentDTO> getAvailable();
    
    /**
     * Save new equipment
     * @param equipmentDTO equipment data to save
     * @return Saved equipment with ID
     */
    EquipmentDTO save(EquipmentDTO equipmentDTO);
    
    /**
     * Update existing equipment
     * @param equipmentDTO updated equipment data
     * @return Updated equipment
     */
    EquipmentDTO update(EquipmentDTO equipmentDTO);
    
    /**
     * Delete equipment by ID
     * @param id equipment ID to delete
     */
    void delete(UUID id);
    
    /**
     * Check if equipment exists
     * @param id equipment ID to check
     * @return true if equipment exists
     */
    boolean exists(UUID id);
    
    /**
     * Mark equipment as in maintenance
     * @param id equipment ID
     * @param endDate expected maintenance end date
     * @return Updated equipment
     */
    EquipmentDTO sendToMaintenance(UUID id, LocalDate endDate);
    
    /**
     * Mark equipment as available after maintenance
     * @param id equipment ID
     * @return Updated equipment
     */
    EquipmentDTO completeMaintenance(UUID id);
    
    /**
     * Find equipment by type
     * @param type equipment type
     * @return List of equipment matching the type
     */
    List<EquipmentDTO> findByType(String type);
}
