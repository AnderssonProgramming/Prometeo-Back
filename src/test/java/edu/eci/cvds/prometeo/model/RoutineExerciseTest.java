package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class RoutineExerciseTest {

    private RoutineExercise routineExercise;

    @BeforeEach
    void setUp() {
        routineExercise = new RoutineExercise();
        routineExercise.setRoutineId(UUID.randomUUID());
        routineExercise.setBaseExerciseId(UUID.randomUUID());
        routineExercise.setSets(3);
        routineExercise.setRepetitions(12);
        routineExercise.setRestTime(30);
        routineExercise.setSequenceOrder(1);
    }

    @Test
    void testRoutineExerciseCreation() {
        assertThat(routineExercise.getRoutineId()).isNotNull();
        assertThat(routineExercise.getBaseExerciseId()).isNotNull();
        assertThat(routineExercise.getSets()).isEqualTo(3);
        assertThat(routineExercise.getRepetitions()).isEqualTo(12);
        assertThat(routineExercise.getRestTime()).isEqualTo(30);
        assertThat(routineExercise.getSequenceOrder()).isEqualTo(1);
    }

    @Test
    void testUpdateConfiguration() {
        routineExercise.updateConfiguration(4, 15, 40);

        assertThat(routineExercise.getSets()).isEqualTo(4);
        assertThat(routineExercise.getRepetitions()).isEqualTo(15);
        assertThat(routineExercise.getRestTime()).isEqualTo(40);
    }

    @Test
    void testDefaultValues() {
        RoutineExercise newRoutineExercise = new RoutineExercise();

        assertThat(newRoutineExercise.getRoutineId()).isNull();
        assertThat(newRoutineExercise.getBaseExerciseId()).isNull();
        assertThat(newRoutineExercise.getSets()).isZero();
        assertThat(newRoutineExercise.getRepetitions()).isZero();
        assertThat(newRoutineExercise.getRestTime()).isZero();
        assertThat(newRoutineExercise.getSequenceOrder()).isZero();
    }
}
