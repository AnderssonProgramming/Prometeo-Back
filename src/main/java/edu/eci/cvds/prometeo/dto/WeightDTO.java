package edu.eci.cvds.prometeo.dto;

import lombok.Data;

@Data
public class WeightDTO {
    private double value;
    private String unit; // "KG" or "LB"
}
