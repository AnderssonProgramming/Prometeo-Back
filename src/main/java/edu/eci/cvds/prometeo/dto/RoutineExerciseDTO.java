package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RoutineExerciseDTO {
    private UUID id;
    private UUID routineId;
    private UUID baseExerciseId;
    private int sets;
    private int repetitions;
    private int restTime;
    private int sequenceOrder;
}
