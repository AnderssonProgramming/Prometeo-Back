package edu.eci.cvds.prometeo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurements {

    @Column(name = "height")
    private double height;

    @Column(name = "chest_circumference")
    private double chestCircumference;

    @Column(name = "waist_circumference")
    private double waistCircumference;

    @Column(name = "hip_circumference")
    private double hipCircumference;

    @Column(name = "biceps_circumference")
    private double bicepsCircumference;

    @Column(name = "thigh_circumference")
    private double thighCircumference;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_measures", columnDefinition = "jsonb")
    private Map<String, Double> additionalMeasures = new HashMap<>();

    public double getBmi() {
        if (height <= 0) return 0;
        // BMI = weight / (height in meters)²
        // Note: This would require weight to be passed as an argument
        return 0;
    }

    public double getWaistToHipRatio() {
        if (hipCircumference <= 0) return 0;
        return waistCircumference / hipCircumference;
    }

    public boolean hasImprovedFrom(BodyMeasurements previous) {
        // Implementation would depend on specific fitness goals
        return false;
    }
}
