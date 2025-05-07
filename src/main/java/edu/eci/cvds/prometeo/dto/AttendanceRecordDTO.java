package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttendanceRecordDTO {
    private UUID reservationId;
    private boolean attended;
    private UUID trainerId;
}