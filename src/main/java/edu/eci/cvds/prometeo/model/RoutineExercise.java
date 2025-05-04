package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "routine_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExercise extends BaseEntity {

    @Column(name = "routine_id", nullable = false)
    private UUID routineId;

    @Column(name = "exercise_id", nullable = false)
    private UUID baseExerciseId;

    @Column(name = "sets")
    private int sets;

    @Column(name = "repetitions")
    private int repetitions;

    @Column(name = "rest_time")
    private int restTime;

    @Column(name = "sequence_order")
    private int sequenceOrder;

    public void updateConfiguration(int sets, int repetitions, int restTime) {
        this.sets = sets;
        this.repetitions = repetitions;
        this.restTime = restTime;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }   
}
