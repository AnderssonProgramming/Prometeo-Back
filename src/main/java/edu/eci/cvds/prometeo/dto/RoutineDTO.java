package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class RoutineDTO {
    private UUID id;
    private String name;
    private String description;
    private String difficulty;
    private String goal;
    private UUID trainerId;
    private LocalDate creationDate;
    private List<RoutineExerciseDTO> exercises;
}
