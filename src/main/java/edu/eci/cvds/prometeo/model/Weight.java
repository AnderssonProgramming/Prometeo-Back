package edu.eci.cvds.prometeo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class Weight {

    @Column(name = "weight_value")
    private double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit")
    private WeightUnit unit;

    public Weight(double value, WeightUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public enum WeightUnit {
        KG, LB
    }    
    
    public double convertTo(WeightUnit targetUnit) {
        if (this.unit == targetUnit) {
            return this.value;
        }

        if (this.unit == WeightUnit.KG && targetUnit == WeightUnit.LB) {
            return this.value * 2.20462;
        } else {
            return this.value / 2.20462;
        }
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setUnit(WeightUnit unit) {
        this.unit = unit;
    }

    public WeightUnit getUnit() {
        return unit;
    }
}