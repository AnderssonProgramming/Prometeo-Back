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
}
