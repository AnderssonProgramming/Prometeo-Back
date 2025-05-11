package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RecommendationDTO {
    private UUID id;
    private UUID userId;
    private UUID routineId;
    private boolean active;
}
