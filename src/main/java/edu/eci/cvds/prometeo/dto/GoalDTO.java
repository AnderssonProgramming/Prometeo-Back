package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class GoalDTO {
    private UUID userId;
    private UUID goalId;
    private String goal;
    private boolean active;
}
