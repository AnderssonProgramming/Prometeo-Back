package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReservationRequestDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<UUID> equipmentIds;
}