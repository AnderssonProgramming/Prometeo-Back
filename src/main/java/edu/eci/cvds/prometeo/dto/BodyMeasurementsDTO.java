package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.util.Map;

@Data
public class BodyMeasurementsDTO {
    private double height;
    private double chestCircumference;
    private double waistCircumference;
    private double hipCircumference;
    private double bicepsCircumference;
    private double thighCircumference;
    private Map<String, Double> additionalMeasures;


    // Getters
    public double getHeight() {
        return height;
    }

    public double getChestCircumference() {
        return chestCircumference;
    }

    public double getWaistCircumference() {
        return waistCircumference;
    }

    public double getHipCircumference() {
        return hipCircumference;
    }

    public double getBicepsCircumference() {
        return bicepsCircumference;
    }

    public double getThighCircumference() {
        return thighCircumference;
    }

    public Map<String, Double> getAdditionalMeasures() {
        return additionalMeasures;
    }

    // Setters
    public void setHeight(double height) {
        this.height = height;
    }

    public void setChestCircumference(double chestCircumference) {
        this.chestCircumference = chestCircumference;
    }

    public void setWaistCircumference(double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public void setHipCircumference(double hipCircumference) {
        this.hipCircumference = hipCircumference;
    }

    public void setBicepsCircumference(double bicepsCircumference) {
        this.bicepsCircumference = bicepsCircumference;
    }

    public void setThighCircumference(double thighCircumference) {
        this.thighCircumference = thighCircumference;
    }

    public void setAdditionalMeasures(Map<String, Double> additionalMeasures) {
        this.additionalMeasures = additionalMeasures;
    }
}
