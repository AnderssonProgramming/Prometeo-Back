package edu.eci.cvds.prometeo.sevice;

import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.RoutineExercise;
import edu.eci.cvds.prometeo.model.UserRoutine;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.UserRoutineRepository;
import edu.eci.cvds.prometeo.service.NotificationService;
import edu.eci.cvds.prometeo.service.impl.RoutineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private RoutineExerciseRepository routineExerciseRepository;

    @Mock
    private UserRoutineRepository userRoutineRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RoutineServiceImpl routineService;

    private Routine routine;
    private UUID routineId;
    private UUID trainerId;
    private UUID userId;
    private UUID exerciseId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routineId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        exerciseId = UUID.randomUUID();

        routine = new Routine();
        routine.setId(routineId);
        routine.setName("Full Body Routine");
        routine.setDifficulty("Intermediate");
        routine.setGoal("Strength");
        routine.setTrainerId(trainerId);
        routine.setCreationDate(LocalDate.now());
    }

    @Test
    void testCreateRoutine() {
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        Routine createdRoutine = routineService.createRoutine(routine, Optional.of(trainerId));

        assertThat(createdRoutine).isNotNull();
        assertThat(createdRoutine.getName()).isEqualTo("Full Body Routine");

        verify(routineRepository, times(1)).save(routine);
    }

    @Test
    void testGetRoutines() {
        when(routineRepository.findByGoalAndDifficulty("Strength", "Intermediate"))
                .thenReturn(List.of(routine));

        List<Routine> routines = routineService.getRoutines(Optional.of("Strength"), Optional.of("Intermediate"));

        assertThat(routines).hasSize(1);
        assertThat(routines.get(0).getGoal()).isEqualTo("Strength");

        verify(routineRepository, times(1)).findByGoalAndDifficulty("Strength", "Intermediate");
    }

    @Test
    void testAssignRoutineToUser() {
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(userRoutineRepository.findByUserIdAndActiveTrue(userId)).thenReturn(List.of());

        UserRoutine userRoutine = new UserRoutine();
        userRoutine.setUserId(userId);
        userRoutine.setRoutineId(routineId);
        userRoutine.setActive(true);

        when(userRoutineRepository.save(any(UserRoutine.class))).thenReturn(userRoutine);

        UserRoutine assignedRoutine = routineService.assignRoutineToUser(routineId, userId, trainerId, Optional.empty(), Optional.empty());

        assertThat(assignedRoutine).isNotNull();
        assertThat(assignedRoutine.getRoutineId()).isEqualTo(routineId);
        assertThat(assignedRoutine.isActive()).isTrue();

        verify(userRoutineRepository, times(1)).save(userRoutine);
        verify(notificationService, times(1)).sendNotification(
                eq(userId),
                anyString(),
                anyString(),
                anyString(),
                any());
    }

    @Test
    void testAddExerciseToRoutine() {
        when(routineRepository.existsById(routineId)).thenReturn(true);

        RoutineExercise exercise = new RoutineExercise();
        exercise.setRoutineId(routineId);
        exercise.setBaseExerciseId(UUID.randomUUID());
        exercise.setSets(3);
        exercise.setRepetitions(12);
        exercise.setRestTime(30);
        exercise.setSequenceOrder(1);

        when(routineExerciseRepository.save(any(RoutineExercise.class))).thenReturn(exercise);

        RoutineExercise addedExercise = routineService.addExerciseToRoutine(routineId, exercise);

        assertThat(addedExercise).isNotNull();
        assertThat(addedExercise.getRoutineId()).isEqualTo(routineId);

        verify(routineExerciseRepository, times(1)).save(exercise);
    }

    @Test
    void testRemoveExerciseFromRoutine() {
        when(routineRepository.existsById(routineId)).thenReturn(true);

        RoutineExercise exercise = new RoutineExercise();
        exercise.setRoutineId(routineId);
        exercise.setBaseExerciseId(exerciseId);
        exercise.setSets(3);
        exercise.setRepetitions(12);
        exercise.setRestTime(30);
        exercise.setSequenceOrder(1);

        when(routineExerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        boolean removed = routineService.removeExerciseFromRoutine(routineId, exerciseId);

        assertThat(removed).isTrue();
        verify(routineExerciseRepository, times(1)).deleteById(exerciseId);
    }

    @Test
    void testGetRoutineById() {
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));

        Optional<Routine> foundRoutine = routineService.getRoutineById(routineId);

        assertThat(foundRoutine).isPresent();
        assertThat(foundRoutine.get().getId()).isEqualTo(routineId);

        verify(routineRepository, times(1)).findById(routineId);
    }
}
