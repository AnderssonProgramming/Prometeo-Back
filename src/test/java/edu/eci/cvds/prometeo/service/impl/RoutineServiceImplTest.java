package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.RoutineExercise;
import edu.eci.cvds.prometeo.model.UserRoutine;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.repository.UserRoutineRepository;
import edu.eci.cvds.prometeo.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






public class RoutineServiceImplTest {

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

    private UUID routineId;
    private UUID userId;
    private UUID trainerId;
    private Routine routine;
    private RoutineExercise routineExercise;
    private UserRoutine userRoutine;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        routineId = UUID.randomUUID();
        userId = UUID.randomUUID();
        trainerId = UUID.randomUUID();
        
        routine = new Routine();
        routine.setId(routineId);
        routine.setName("Test Routine");
        routine.setDescription("Test Description");
        routine.setDifficulty("Medium");
        routine.setGoal("Strength");
        
        routineExercise = new RoutineExercise();
        routineExercise.setId(UUID.randomUUID());
        routineExercise.setRoutineId(routineId);
        
        userRoutine = new UserRoutine();
        userRoutine.setUserId(userId);
        userRoutine.setRoutineId(routineId);
        userRoutine.setActive(true);
        userRoutine.setAssignmentDate(LocalDate.now());
    }

    @Test
    public void testCreateRoutine() {
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);
        
        Routine result = routineService.createRoutine(routine, Optional.of(trainerId));
        
        assertEquals(routine.getName(), result.getName());
        assertEquals(LocalDate.now(), result.getCreationDate());
        assertEquals(trainerId, result.getTrainerId());
        verify(routineRepository).save(routine);
    }

    @Test
    public void testGetRoutines_AllParametersPresent() {
        String goal = "Strength";
        String difficulty = "Medium";
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(routineRepository.findByGoalAndDifficulty(goal, difficulty))
            .thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getRoutines(
            Optional.of(goal), Optional.of(difficulty));
        
        assertEquals(expectedRoutines, result);
        verify(routineRepository).findByGoalAndDifficulty(goal, difficulty);
    }

    @Test
    public void testGetRoutines_OnlyGoalPresent() {
        String goal = "Strength";
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(routineRepository.findByGoal(goal)).thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getRoutines(
            Optional.of(goal), Optional.empty());
        
        assertEquals(expectedRoutines, result);
        verify(routineRepository).findByGoal(goal);
    }

    @Test
    public void testGetRoutines_OnlyDifficultyPresent() {
        String difficulty = "Medium";
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(routineRepository.findByDifficulty(difficulty)).thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getRoutines(
            Optional.empty(), Optional.of(difficulty));
        
        assertEquals(expectedRoutines, result);
        verify(routineRepository).findByDifficulty(difficulty);
    }

    @Test
    public void testGetRoutines_NoParametersPresent() {
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(routineRepository.findAll()).thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getRoutines(Optional.empty(), Optional.empty());
        
        assertEquals(expectedRoutines, result);
        verify(routineRepository).findAll();
    }

    @Test
    public void testGetRoutinesByTrainer() {
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(routineRepository.findByTrainerIdAndDeletedAtIsNull(trainerId))
            .thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getRoutinesByTrainer(trainerId);
        
        assertEquals(expectedRoutines, result);
        verify(routineRepository).findByTrainerIdAndDeletedAtIsNull(trainerId);
    }

    @Test
    public void testAssignRoutineToUser() {
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(userRoutineRepository.findByUserIdAndActiveTrue(userId))
            .thenReturn(Collections.singletonList(userRoutine));
        when(userRoutineRepository.save(any(UserRoutine.class))).thenReturn(userRoutine);
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
        
        UserRoutine result = routineService.assignRoutineToUser(
            routineId, userId, trainerId, Optional.empty(), Optional.empty());
        
        assertNotNull(result);
        verify(userRoutineRepository).findByUserIdAndActiveTrue(userId);
        verify(userRoutineRepository, times(2)).save(any(UserRoutine.class));
        verify(notificationService).sendNotification(
            eq(userId), anyString(), anyString(), anyString(), any(Optional.class));
    }

    @Test
    public void testAssignRoutineToUser_RoutineNotFound() {
        when(routineRepository.existsById(routineId)).thenReturn(false);
        
        routineService.assignRoutineToUser(
            routineId, userId, trainerId, Optional.empty(), Optional.empty());
    }

    @Test
    public void testGetUserRoutines_ActiveOnly() {
        List<UserRoutine> userRoutines = Collections.singletonList(userRoutine);
        List<UUID> routineIds = Collections.singletonList(routineId);
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(userRoutineRepository.findByUserIdAndActiveTrue(userId)).thenReturn(userRoutines);
        when(routineRepository.findAllById(routineIds)).thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getUserRoutines(userId, true);
        
        assertEquals(expectedRoutines, result);
        verify(userRoutineRepository).findByUserIdAndActiveTrue(userId);
        verify(routineRepository).findAllById(routineIds);
    }

    @Test
    public void testGetUserRoutines_AllRoutines() {
        List<UserRoutine> userRoutines = Collections.singletonList(userRoutine);
        List<UUID> routineIds = Collections.singletonList(routineId);
        List<Routine> expectedRoutines = Collections.singletonList(routine);
        
        when(userRoutineRepository.findByUserId(userId)).thenReturn(userRoutines);
        when(routineRepository.findAllById(routineIds)).thenReturn(expectedRoutines);
        
        List<Routine> result = routineService.getUserRoutines(userId, false);
        
        assertEquals(expectedRoutines, result);
        verify(userRoutineRepository).findByUserId(userId);
        verify(routineRepository).findAllById(routineIds);
    }

    @Test
    public void testUpdateRoutine() {
        Routine updatedRoutine = new Routine();
        updatedRoutine.setName("Updated Routine");
        updatedRoutine.setDescription("Updated Description");
        updatedRoutine.setDifficulty("Hard");
        updatedRoutine.setGoal("Endurance");
        
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);
        
        Routine result = routineService.updateRoutine(routineId, updatedRoutine, trainerId);
        
        assertEquals(updatedRoutine.getName(), result.getName());
        assertEquals(updatedRoutine.getDescription(), result.getDescription());
        assertEquals(updatedRoutine.getDifficulty(), result.getDifficulty());
        assertEquals(updatedRoutine.getGoal(), result.getGoal());
        verify(routineRepository).save(routine);
    }

    @Test
    public void testUpdateRoutine_RoutineNotFound() {
        when(routineRepository.findById(routineId)).thenReturn(Optional.empty());
        
        routineService.updateRoutine(routineId, routine, trainerId);
    }

    @Test
    public void testAddExerciseToRoutine() {
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(routineExerciseRepository.save(routineExercise)).thenReturn(routineExercise);
        
        RoutineExercise result = routineService.addExerciseToRoutine(routineId, routineExercise);
        
        assertEquals(routineId, result.getRoutineId());
        verify(routineExerciseRepository).save(routineExercise);
    }

    @Test
    public void testAddExerciseToRoutine_RoutineNotFound() {
        when(routineRepository.existsById(routineId)).thenReturn(false);
        
        routineService.addExerciseToRoutine(routineId, routineExercise);
    }

    @Test
    public void testRemoveExerciseFromRoutine_Success() {
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(routineExerciseRepository.findById(routineExercise.getId()))
            .thenReturn(Optional.of(routineExercise));
        
        boolean result = routineService.removeExerciseFromRoutine(routineId, routineExercise.getId());
        
        assertTrue(result);
        verify(routineExerciseRepository).deleteById(routineExercise.getId());
    }

    @Test
    public void testRemoveExerciseFromRoutine_RoutineNotFound() {
        when(routineRepository.existsById(routineId)).thenReturn(false);
        
        routineService.removeExerciseFromRoutine(routineId, routineExercise.getId());
    }

    @Test
    public void testGetRoutineById() {
        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
        
        Optional<Routine> result = routineService.getRoutineById(routineId);
        
        assertTrue(result.isPresent());
        assertEquals(routine, result.get());
    }

    @Test
    public void testDeactivateUserRoutine_Success() {
        when(userRoutineRepository.findByUserIdAndRoutineId(userId, routineId))
            .thenReturn(Optional.of(userRoutine));
        
        boolean result = routineService.deactivateUserRoutine(userId, routineId);
        
        assertTrue(result);
        assertFalse(userRoutine.isActive());
        verify(userRoutineRepository).save(userRoutine);
    }

    @Test
    public void testDeactivateUserRoutine_NotFound() {
        when(userRoutineRepository.findByUserIdAndRoutineId(userId, routineId))
            .thenReturn(Optional.empty());
        
        boolean result = routineService.deactivateUserRoutine(userId, routineId);
        
        assertFalse(result);
    }
}