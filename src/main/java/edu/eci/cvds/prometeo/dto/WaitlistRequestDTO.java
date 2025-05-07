package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WaitlistRequestDTO {
    private UUID userId;
    private UUID sessionId;
}