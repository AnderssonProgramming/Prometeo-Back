package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class EquipmentDTO {
    private UUID id;
    private String name;
    private String description;
    private String type;
    private String location;
    private String status;
    private String serialNumber;
    private String brand;
    private String model;
    private LocalDate acquisitionDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private boolean reservable;
    private Integer maxReservationHours;
    private String imageUrl;
    private Double weight;
    private String dimensions;
    private String primaryMuscleGroup;
    private String secondaryMuscleGroups;
}
