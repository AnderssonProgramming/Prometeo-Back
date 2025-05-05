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
}
