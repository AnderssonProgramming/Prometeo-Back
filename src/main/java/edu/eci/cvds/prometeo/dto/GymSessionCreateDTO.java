package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class GymSessionCreateDTO {
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private UUID trainerId;
    private String description;
}