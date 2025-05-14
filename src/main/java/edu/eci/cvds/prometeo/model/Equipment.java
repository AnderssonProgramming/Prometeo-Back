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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public java.time.LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(java.time.LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public java.time.LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(java.time.LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public java.time.LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(java.time.LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public boolean isReservable() {
        return reservable;
    }

    public void setReservable(boolean reservable) {
        this.reservable = reservable;
    }

    public Integer getMaxReservationHours() {
        return maxReservationHours;
    }

    public void setMaxReservationHours(Integer maxReservationHours) {
        this.maxReservationHours = maxReservationHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(String primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public String getSecondaryMuscleGroups() {
        return secondaryMuscleGroups;
    }

    public void setSecondaryMuscleGroups(String secondaryMuscleGroups) {
        this.secondaryMuscleGroups = secondaryMuscleGroups;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

}