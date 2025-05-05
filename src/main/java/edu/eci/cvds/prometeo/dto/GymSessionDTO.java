package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class GymSessionDTO {
    private UUID id;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int reservedSpots;
    private UUID trainerId;
    private String description;
}
