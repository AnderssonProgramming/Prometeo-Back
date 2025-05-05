package edu.eci.cvds.prometeo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {
    
    private UUID id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    
    private String location;
    
    private String status;
    
    private String serialNumber;
    
    private String brand;
    
    private String model;
    
    private LocalDate acquisitionDate;
    
    private LocalDate lastMaintenanceDate;
    
    private LocalDate nextMaintenanceDate;
    
    private boolean reservable = true;
    
    private Integer maxReservationHours;
    
    private String imageUrl;
    
    private Double weight;
    
    private String dimensions;
    
    private String primaryMuscleGroup;
    
    private String secondaryMuscleGroups;
}
