package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProgressHistoryDTO {
    private UUID id;
    private UUID userId;
    private LocalDate recordDate;
    private String measureType;
    private double oldValue;
    private double newValue;
    private String notes;
}
