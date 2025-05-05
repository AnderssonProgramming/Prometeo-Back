package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity representing gym equipment that can be reserved by users
 */
@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "status", nullable = false)
    private String status; // AVAILABLE, IN_USE, MAINTENANCE
    
    @Column(name = "serial_number", unique = true)
    private String serialNumber;
    
    @Column(name = "brand")
    private String brand;
    
    @Column(name = "model")
    private String model;
    
    @Column(name = "acquisition_date")
    private java.time.LocalDate acquisitionDate;
    
    @Column(name = "last_maintenance_date")
    private java.time.LocalDate lastMaintenanceDate;
    
    @Column(name = "next_maintenance_date")
    private java.time.LocalDate nextMaintenanceDate;
    
    @Column(name = "is_reservable", nullable = false)
    private boolean reservable = true;
    
    @Column(name = "max_reservation_hours")
    private Integer maxReservationHours;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "weight")
    private Double weight;
    
    @Column(name = "dimensions")
    private String dimensions;
    
    @Column(name = "primary_muscle_group")
    private String primaryMuscleGroup;
    
    @Column(name = "secondary_muscle_groups")
    private String secondaryMuscleGroups;
    
    @Column(name = "maintenance_date")
    private String maintenanceDate;

    /**
     * Checks if the equipment is available for use
     * @return true if the equipment is available
     */
    public boolean isAvailable() {
        return "AVAILABLE".equals(status) && reservable;
    }
    
    /**
     * Marks the equipment as in use
     */
    public void markAsInUse() {
        this.status = "IN_USE";
    }
    
    /**
     * Marks the equipment as available
     */
    public void markAsAvailable() {
        this.status = "AVAILABLE";
    }
    
    /**
     * Sends the equipment to maintenance
     * @param nextMaintenanceDate The date when maintenance is expected to be completed
     */
    public void sendToMaintenance(java.time.LocalDate nextMaintenanceDate) {
        this.status = "MAINTENANCE";
        this.lastMaintenanceDate = java.time.LocalDate.now();
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
    
    /**
     * Records that maintenance was completed
     */
    public void completeMaintenance() {
        this.status = "AVAILABLE";
        this.lastMaintenanceDate = java.time.LocalDate.now();
    }
    
    /**
     * Checks if maintenance is due
     * @return true if maintenance is due
     */
    public boolean isMaintenanceDue() {
        return nextMaintenanceDate != null && !nextMaintenanceDate.isAfter(java.time.LocalDate.now());
    }
}