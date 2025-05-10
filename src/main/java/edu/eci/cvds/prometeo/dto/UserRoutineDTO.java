package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserRoutineDTO {
    private UUID id;
    private UUID userId;
    private UUID routineId;
    private LocalDate assignmentDate;
    private LocalDate endDate;
    private boolean active;
}
