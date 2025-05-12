package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineTest {

    private Routine routine;
    private RoutineExercise exercise;

    @BeforeEach
    void setUp() {
        routine = new Routine();
        routine.setName("Strength Training");
        routine.setDescription("Routine for improving overall strength");
        routine.setDifficulty("Intermediate");
        routine.setGoal("Muscle Gain");
        routine.setTrainerId(UUID.randomUUID());
        routine.setCreationDate(LocalDate.now());

        exercise = new RoutineExercise();
        exercise.setRoutineId(UUID.randomUUID());
        exercise.setBaseExerciseId(UUID.randomUUID());
        exercise.setSequenceOrder(1);

        routine.addExercise(exercise);
    }

    @Test
    void testRoutineCreation() {
        assertThat(routine.getName()).isEqualTo("Strength Training");
        assertThat(routine.getDescription()).isEqualTo("Routine for improving overall strength");
        assertThat(routine.getDifficulty()).isEqualTo("Intermediate");
        assertThat(routine.getGoal()).isEqualTo("Muscle Gain");
        assertThat(routine.getTrainerId()).isNotNull();
        assertThat(routine.getCreationDate()).isNotNull();
        assertThat(routine.getExercises()).hasSize(1);
    }

    @Test
    void testAddExercise() {
        RoutineExercise newExercise = new RoutineExercise();
        newExercise.setRoutineId(UUID.randomUUID());
        newExercise.setBaseExerciseId(UUID.randomUUID());
        routine.addExercise(newExercise);

        assertThat(routine.getExercises()).hasSize(2);
        assertThat(routine.getExercises()).contains(newExercise);
    }

    @Test
    void testRemoveExercise() {
        UUID baseExerciseId = exercise.getBaseExerciseId();
        routine.removeExercise(baseExerciseId);

        assertThat(routine.getExercises()).isEmpty();
    }

    @Test
    void testUpdateExerciseOrder() {
        UUID baseExerciseId = exercise.getBaseExerciseId();
        int newOrder = 5;
        routine.updateExerciseOrder(baseExerciseId, newOrder);

        assertThat(routine.getExercises().get(0).getSequenceOrder()).isEqualTo(newOrder);
    }

    @Test
    void testDefaultValues() {
        Routine newRoutine = new Routine();

        assertThat(newRoutine.getId()).isNull();
        assertThat(newRoutine.getName()).isNull();
        assertThat(newRoutine.getDescription()).isNull();
        assertThat(newRoutine.getDifficulty()).isNull();
        assertThat(newRoutine.getGoal()).isNull();
        assertThat(newRoutine.getTrainerId()).isNull();
        assertThat(newRoutine.getCreationDate()).isNull();
        assertThat(newRoutine.getExercises()).isEmpty();
    }
}
