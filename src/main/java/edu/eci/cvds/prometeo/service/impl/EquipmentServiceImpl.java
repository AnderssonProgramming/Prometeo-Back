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
        Equipment equipment = convertToEntity(equipmentDTO);
        
        // Set default values for new equipment
        if (equipment.getStatus() == null) {
            equipment.setStatus("AVAILABLE");
        }
        
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    @Transactional
    public EquipmentDTO update(EquipmentDTO equipmentDTO) {
        // Verify that the equipment exists
        if (!equipmentRepository.existsById(equipmentDTO.getId())) {
            throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO);
        }
        
        Equipment equipment = convertToEntity(equipmentDTO);
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(updatedEquipment);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        equipmentRepository.deleteById(id);
    }
    
    @Override
    public boolean exists(UUID id) {
        return equipmentRepository.existsById(id);
    }
    
    @Override
    @Transactional
    public EquipmentDTO sendToMaintenance(UUID id, LocalDate endDate) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO));
        
        equipment.sendToMaintenance(endDate);
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    @Transactional
    public EquipmentDTO completeMaintenance(UUID id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO));
        
        equipment.completeMaintenance();
        Equipment savedEquipment = equipmentRepository.save(equipment);
        return convertToDTO(savedEquipment);
    }
    
    @Override
    public List<EquipmentDTO> findByType(String type) {
        return equipmentRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Entity to DTO
     * @param equipment entity
     * @return DTO
     */
    private EquipmentDTO convertToDTO(Equipment equipment) {
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
