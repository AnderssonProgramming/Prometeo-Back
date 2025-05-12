package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Routine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoutineRepositoryTest {

    @Autowired
    private RoutineRepository routineRepository;

    private Routine createRoutine(String name, String description, String difficulty, String goal, UUID trainerId) {
        Routine routine = new Routine();
        routine.setId(UUID.randomUUID());
        routine.setName(name);
        routine.setDescription(description);
        routine.setDifficulty(difficulty);
        routine.setGoal(goal);
        routine.setTrainerId(trainerId);
        routine.setCreationDate(LocalDate.now());
        routine.setExercises(List.of());
        return routine;
    }

    @Test
    void testSaveAndFindByTrainerId() {
        UUID trainerId = UUID.randomUUID();
        Routine routine1 = createRoutine("Strength Training", "Build muscle", "Intermediate", "Muscle Gain", trainerId);
        Routine routine2 = createRoutine("Cardio Blast", "Improve endurance", "Advanced", "Weight Loss", trainerId);

        routineRepository.save(routine1);
        routineRepository.save(routine2);

        List<Routine> foundRoutines = routineRepository.findByTrainerId(trainerId);

        assertThat(foundRoutines).hasSize(2);
        assertThat(foundRoutines).containsExactlyInAnyOrder(routine1, routine2);
    }

    @Test
    void testFindByDifficulty() {
        Routine routine = createRoutine("Leg Day", "Strength workout", "Beginner", "Muscle Gain", UUID.randomUUID());
        routineRepository.save(routine);

        List<Routine> foundRoutines = routineRepository.findByDifficulty("Beginner");

        assertThat(foundRoutines).hasSize(1);
        assertThat(foundRoutines.get(0)).isEqualTo(routine);
    }

    @Test
    void testFindByGoal() {
        Routine routine = createRoutine("Weight Loss Routine", "Cardio-based exercises", "Intermediate", "Weight Loss", UUID.randomUUID());
        routineRepository.save(routine);

        List<Routine> foundRoutines = routineRepository.findByGoal("Weight Loss");

        assertThat(foundRoutines).hasSize(1);
        assertThat(foundRoutines.get(0)).isEqualTo(routine);
    }

    @Test
    void testFindByTrainerIdAndDeletedAtIsNull() {
        UUID trainerId = UUID.randomUUID();
        Routine routine = createRoutine("Functional Training", "Full-body workout", "Advanced", "Strength", trainerId);
        routineRepository.save(routine);

        List<Routine> foundRoutines = routineRepository.findByTrainerIdAndDeletedAtIsNull(trainerId);

        assertThat(foundRoutines).hasSize(1);
        assertThat(foundRoutines.get(0)).isEqualTo(routine);
    }

    @Test
    void testFindCurrentRoutineByUserId() {
        UUID userId = UUID.randomUUID();
        Routine routine = createRoutine("Yoga Flow", "Flexibility and balance", "Intermediate", "Relaxation", UUID.randomUUID());
        routineRepository.save(routine);

        Optional<Routine> currentRoutine = routineRepository.findCurrentRoutineByUserId(userId);

        assertThat(currentRoutine).isEmpty();
    }

    @Test
    void testFindByGoalAndDifficulty() {
        Routine routine = createRoutine("HIIT Challenge", "High-intensity cardio", "Advanced", "Weight Loss", UUID.randomUUID());
        routineRepository.save(routine);

        List<Routine> foundRoutines = routineRepository.findByGoalAndDifficulty("Weight Loss", "Advanced");

        assertThat(foundRoutines).hasSize(1);
        assertThat(foundRoutines.get(0)).isEqualTo(routine);
    }
}
