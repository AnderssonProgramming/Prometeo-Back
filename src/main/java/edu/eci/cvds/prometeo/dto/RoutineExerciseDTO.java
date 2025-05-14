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

    // Note: Since you're using Lombok's @Data annotation, 
    // these getters and setters are automatically generated.
    // Adding them manually is redundant but here they are:

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoutineId() {
        return routineId;
    }

    public void setRoutineId(UUID routineId) {
        this.routineId = routineId;
    }

    public UUID getBaseExerciseId() {
        return baseExerciseId;
    }

    public void setBaseExerciseId(UUID baseExerciseId) {
        this.baseExerciseId = baseExerciseId;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}
