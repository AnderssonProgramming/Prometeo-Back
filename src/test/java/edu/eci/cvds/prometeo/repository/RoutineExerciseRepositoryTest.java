package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.RoutineExercise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoutineExerciseRepositoryTest {

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Test
    void testSaveAndFindByRoutineId() {
        UUID routineId = UUID.randomUUID();

        RoutineExercise exercise1 = new RoutineExercise();
        exercise1.setRoutineId(routineId);
        exercise1.setBaseExerciseId(UUID.randomUUID());
        exercise1.setSets(3);
        exercise1.setRepetitions(12);
        exercise1.setRestTime(30);
        exercise1.setSequenceOrder(1);

        RoutineExercise exercise2 = new RoutineExercise();
        exercise2.setRoutineId(routineId);
        exercise2.setBaseExerciseId(UUID.randomUUID());
        exercise2.setSets(4);
        exercise2.setRepetitions(10);
        exercise2.setRestTime(45);
        exercise2.setSequenceOrder(2);

        routineExerciseRepository.save(exercise1);
        routineExerciseRepository.save(exercise2);

        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutineId(routineId);

        assertThat(exercises).hasSize(2);
        assertThat(exercises).extracting("baseExerciseId")
                .containsExactlyInAnyOrder(exercise1.getBaseExerciseId(), exercise2.getBaseExerciseId());
    }

    @Test
    void testFindByBaseExerciseId() {
        UUID baseExerciseId = UUID.randomUUID();

        RoutineExercise exercise = new RoutineExercise();
        exercise.setRoutineId(UUID.randomUUID());
        exercise.setBaseExerciseId(baseExerciseId);
        exercise.setSets(3);
        exercise.setRepetitions(12);
        exercise.setRestTime(30);
        exercise.setSequenceOrder(1);

        routineExerciseRepository.save(exercise);

        List<RoutineExercise> foundExercises = routineExerciseRepository.findByBaseExerciseId(baseExerciseId);

        assertThat(foundExercises).hasSize(1);
        assertThat(foundExercises.get(0).getBaseExerciseId()).isEqualTo(baseExerciseId);
    }

    @Test
    void testFindByRoutineIdOrderBySequenceOrder() {
        UUID routineId = UUID.randomUUID();

        RoutineExercise exercise1 = new RoutineExercise();
        exercise1.setRoutineId(routineId);
        exercise1.setBaseExerciseId(UUID.randomUUID());
        exercise1.setSets(3);
        exercise1.setRepetitions(12);
        exercise1.setRestTime(30);
        exercise1.setSequenceOrder(2);

        RoutineExercise exercise2 = new RoutineExercise();
        exercise2.setRoutineId(routineId);
        exercise2.setBaseExerciseId(UUID.randomUUID());
        exercise2.setSets(4);
        exercise2.setRepetitions(10);
        exercise2.setRestTime(45);
        exercise2.setSequenceOrder(1);

        routineExerciseRepository.save(exercise1);
        routineExerciseRepository.save(exercise2);

        List<RoutineExercise> orderedExercises = routineExerciseRepository.findByRoutineIdOrderBySequenceOrder(routineId);

        assertThat(orderedExercises).hasSize(2);
        assertThat(orderedExercises.get(0).getSequenceOrder()).isEqualTo(1);
        assertThat(orderedExercises.get(1).getSequenceOrder()).isEqualTo(2);
    }

    @Test
    void testDeleteByRoutineId() {
        UUID routineId = UUID.randomUUID();

        RoutineExercise exercise = new RoutineExercise();
        exercise.setRoutineId(routineId);
        exercise.setBaseExerciseId(UUID.randomUUID());
        exercise.setSets(3);
        exercise.setRepetitions(12);
        exercise.setRestTime(30);
        exercise.setSequenceOrder(1);

        routineExerciseRepository.save(exercise);
        routineExerciseRepository.deleteByRoutineId(routineId);

        List<RoutineExercise> foundExercises = routineExerciseRepository.findByRoutineId(routineId);
        assertThat(foundExercises).isEmpty();
    }
}
