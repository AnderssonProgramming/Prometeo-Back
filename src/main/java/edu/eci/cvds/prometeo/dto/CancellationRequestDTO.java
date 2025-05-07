package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CancellationRequestDTO {
    private String reason;
    private UUID trainerId;
}