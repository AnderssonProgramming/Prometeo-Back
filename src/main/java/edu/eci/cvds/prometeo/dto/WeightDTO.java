package edu.eci.cvds.prometeo.dto;

import lombok.Data;

@Data
public class WeightDTO {
    private double value;
    private String unit; // "KG" or "LB"

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
