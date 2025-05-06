package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.dto.EquipmentDTO;
import edu.eci.cvds.prometeo.model.Equipment;
import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.repository.EquipmentRepository;
import edu.eci.cvds.prometeo.service.EquipmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    
    @Autowired
    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }
    
    @Override
    public List<EquipmentDTO> getAll() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<EquipmentDTO> getById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
        return equipmentRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    public List<EquipmentDTO> getAvailable() {
        return equipmentRepository.findByStatus("AVAILABLE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public EquipmentDTO save(EquipmentDTO equipmentDTO) {
        if (equipmentDTO == null) {
            throw new IllegalArgumentException("Equipment data cannot be null");
        }
        
        validateEquipmentData(equipmentDTO);
        
        Equipment equipment = convertToEntity(equipmentDTO);
        
        // Set default values for new equipment
        if (equipment.getStatus() == null) {
            equipment.setStatus("AVAILABLE");
        }
        
        // For new equipment, generate a new UUID if not provided
        if (equipment.getId() == null) {
            equipment.setId(UUID.randomUUID());
        }
        
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    @Transactional
    public EquipmentDTO update(EquipmentDTO equipmentDTO) {
        if (equipmentDTO == null || equipmentDTO.getId() == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null for update operation");
        }
        
        // Verify that the equipment exists
        Equipment existingEquipment = equipmentRepository.findById(equipmentDTO.getId())
            .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO));
        
        validateEquipmentData(equipmentDTO);
        
        // Convert to entity but preserve creation-time fields if needed
        Equipment equipment = convertToEntity(equipmentDTO);
        
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(updatedEquipment);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
        
        // Check if equipment exists before deletion
        if (!equipmentRepository.existsById(id)) {
            throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO);
        }
        
        equipmentRepository.deleteById(id);
    }
    
    @Override
    public boolean exists(UUID id) {
        if (id == null) {
            return false;
        }
        return equipmentRepository.existsById(id);
    }
    
    @Override
    @Transactional
    public EquipmentDTO sendToMaintenance(UUID id, LocalDate endDate) {
        if (id == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
        
        if (endDate == null || endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Maintenance end date must be in the future");
        }
        
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO));
        
        // Make sure equipment is not already in maintenance
        if ("MAINTENANCE".equals(equipment.getStatus())) {
            throw new PrometeoExceptions("Equipment is already in maintenance");
        }
        
        equipment.sendToMaintenance(endDate);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    @Transactional
    public EquipmentDTO completeMaintenance(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Equipment ID cannot be null");
        }
        
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO));
        
        // Only complete maintenance if the equipment is actually in maintenance
        if (!"MAINTENANCE".equals(equipment.getStatus())) {
            throw new PrometeoExceptions("Equipment is not in maintenance status");
        }
        
        equipment.completeMaintenance();
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    public List<EquipmentDTO> findByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be null or empty");
        }
        
        return equipmentRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Validates equipment data for business rules
     * @param dto equipment data to validate
     */
    private void validateEquipmentData(EquipmentDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment name cannot be null or empty");
        }
        
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be null or empty");
        }
        
        // Add other validation rules as needed
    }
    
    /**
     * Convert Entity to DTO
     * @param equipment entity
     * @return DTO
     */
    private EquipmentDTO convertToDTO(Equipment equipment) {
        if (equipment == null) {
            return null;
        }
        
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(equipment.getId());
        dto.setName(equipment.getName());
        dto.setDescription(equipment.getDescription());
        dto.setType(equipment.getType());
        dto.setLocation(equipment.getLocation());
        dto.setStatus(equipment.getStatus());
        dto.setSerialNumber(equipment.getSerialNumber());
        dto.setBrand(equipment.getBrand());
        dto.setModel(equipment.getModel());
        dto.setAcquisitionDate(equipment.getAcquisitionDate());
        dto.setLastMaintenanceDate(equipment.getLastMaintenanceDate());
        dto.setNextMaintenanceDate(equipment.getNextMaintenanceDate());
        dto.setReservable(equipment.isReservable());
        dto.setMaxReservationHours(equipment.getMaxReservationHours());
        dto.setImageUrl(equipment.getImageUrl());
        dto.setWeight(equipment.getWeight());
        dto.setDimensions(equipment.getDimensions());
        dto.setPrimaryMuscleGroup(equipment.getPrimaryMuscleGroup());
        dto.setSecondaryMuscleGroups(equipment.getSecondaryMuscleGroups());
        return dto;
    }
    
    /**
     * Convert DTO to Entity
     * @param dto DTO
     * @return entity
     */
    private Equipment convertToEntity(EquipmentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Equipment equipment = new Equipment();
        equipment.setId(dto.getId());
        equipment.setName(dto.getName());
        equipment.setDescription(dto.getDescription());
        equipment.setType(dto.getType());
        equipment.setLocation(dto.getLocation());
        equipment.setStatus(dto.getStatus());
        equipment.setSerialNumber(dto.getSerialNumber());
        equipment.setBrand(dto.getBrand());
        equipment.setModel(dto.getModel());
        equipment.setAcquisitionDate(dto.getAcquisitionDate());
        equipment.setLastMaintenanceDate(dto.getLastMaintenanceDate());
        equipment.setNextMaintenanceDate(dto.getNextMaintenanceDate());
        equipment.setReservable(dto.isReservable());
        equipment.setMaxReservationHours(dto.getMaxReservationHours());
        equipment.setImageUrl(dto.getImageUrl());
        equipment.setWeight(dto.getWeight());
        equipment.setDimensions(dto.getDimensions());
        equipment.setPrimaryMuscleGroup(dto.getPrimaryMuscleGroup());
        equipment.setSecondaryMuscleGroups(dto.getSecondaryMuscleGroups());
        return equipment;
    }
}
