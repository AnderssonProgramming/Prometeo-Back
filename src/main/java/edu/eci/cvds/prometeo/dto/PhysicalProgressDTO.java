package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PhysicalProgressDTO {
    private UUID id;
    private UUID userId;
    private LocalDate recordDate;
    private WeightDTO weight;
    private BodyMeasurementsDTO measurements;
    private String physicalGoal;
    private String trainerObservations;

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public WeightDTO getWeight() {
        return weight;
    }

    public BodyMeasurementsDTO getMeasurements() {
        return measurements;
    }

    public String getPhysicalGoal() {
        return physicalGoal;
    }

    public String getTrainerObservations() {
        return trainerObservations;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public void setWeight(WeightDTO weight) {
        this.weight = weight;
    }

    public void setMeasurements(BodyMeasurementsDTO measurements) {
        this.measurements = measurements;
    }

    public void setPhysicalGoal(String physicalGoal) {
        this.physicalGoal = physicalGoal;
    }

    public void setTrainerObservations(String trainerObservations) {
        this.trainerObservations = trainerObservations;
    }
}
