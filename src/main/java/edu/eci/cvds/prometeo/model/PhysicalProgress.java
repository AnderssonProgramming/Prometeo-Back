package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import edu.eci.cvds.prometeo.model.Weight.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "physical_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalProgress extends AuditableEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Embedded
    private Weight weight;

    @Embedded
    private BodyMeasurements measurements;

    @Column(name = "physical_goal")
    private String physicalGoal;

    @Column(name = "trainer_observations")
    private String trainerObservations;

    // TODO: Fix lombok issue with @AllArgsConstructor and @NoArgsConstructor
    public void updateWeight(double weightValue) {
        if (this.weight == null) {
            this.weight = new Weight(weightValue, WeightUnit.KG);
        } else {
            this.weight.setValue(weightValue);
        }
    }

    public void updateMeasurements(BodyMeasurements measurements) {
        this.measurements = measurements;
    }

    public void updateGoal(String goal) {
        this.physicalGoal = goal;
    }

    public void addObservation(String observation) {
        this.trainerObservations = observation;
    }
}