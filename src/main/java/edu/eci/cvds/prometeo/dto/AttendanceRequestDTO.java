package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceRequestDTO {
    private boolean attended;
    private UUID trainerId;
}