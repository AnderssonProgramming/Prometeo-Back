package edu.eci.cvds.prometeo.model;


import edu.eci.cvds.prometeo.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "progress_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressHistory extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "measure_type", nullable = false)
    private String measureType;

    @Column(name = "old_value")
    private double oldValue;

    @Column(name = "new_value")
    private double newValue;

    @Column(name = "notes")
    private String notes;

    public double calculateChange() {
        return newValue - oldValue;
    }

    public double calculatePercentageChange() {
        if (oldValue == 0) {
            return 0;
        }
        return (calculateChange() / Math.abs(oldValue)) * 100;
    }
}