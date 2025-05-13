package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.model.RoutineExercise;
import edu.eci.cvds.prometeo.model.UserRoutine;
import edu.eci.cvds.prometeo.repository.RoutineRepository;
import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
import edu.eci.cvds.prometeo.repository.UserRoutineRepository;
import edu.eci.cvds.prometeo.service.RoutineService;
import edu.eci.cvds.prometeo.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineExerciseRepository routineExerciseRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final NotificationService notificationService;

    @Autowired
    public RoutineServiceImpl(
            RoutineRepository routineRepository,
            RoutineExerciseRepository routineExerciseRepository,
            UserRoutineRepository userRoutineRepository,
            NotificationService notificationService) {
        this.routineRepository = routineRepository;
        this.routineExerciseRepository = routineExerciseRepository;
        this.userRoutineRepository = userRoutineRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Routine createRoutine(Routine routine, Optional<UUID> trainerId) {
        routine.setCreationDate(LocalDate.now());
        trainerId.ifPresent(routine::setTrainerId);
        
        return routineRepository.save(routine);
    }

    @Override
    public List<Routine> getRoutines(Optional<String> goal, Optional<String> difficulty) {
        if (goal.isPresent() && difficulty.isPresent()) {
            return routineRepository.findByGoalAndDifficulty(goal.get(), difficulty.get());
        } else if (goal.isPresent()) {
            return routineRepository.findByGoal(goal.get());
        } else if (difficulty.isPresent()) {
            return routineRepository.findByDifficulty(difficulty.get());
        } else {
            return routineRepository.findAll();
        }
    }

    @Override
    public List<Routine> getRoutinesByTrainer(UUID trainerId) {
        return routineRepository.findByTrainerIdAndDeletedAtIsNull(trainerId);
    }

    @Override
    @Transactional
    public UserRoutine assignRoutineToUser(UUID routineId, UUID userId, UUID trainerId, 
                                         Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        // Check if routine exists
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        
        // Deactivate any active routines
        List<UserRoutine> activeRoutines = userRoutineRepository.findByUserIdAndActiveTrue(userId);
        for (UserRoutine activeRoutine : activeRoutines) {
            activeRoutine.setActive(false);
            userRoutineRepository.save(activeRoutine);
        }
        
        // Create new assignment
        UserRoutine userRoutine = new UserRoutine();
        userRoutine.setUserId(userId);
        userRoutine.setRoutineId(routineId);
        userRoutine.setAssignmentDate(LocalDate.now());
        userRoutine.setActive(true);
        startDate.ifPresent(userRoutine::setStartDate);
        endDate.ifPresent(userRoutine::setEndDate);
        
        UserRoutine savedUserRoutine = userRoutineRepository.save(userRoutine);
        
        // Send notification
        if (notificationService != null) {
            Routine routine = routineRepository.findById(routineId).orElse(null);
            if (routine != null) {
                notificationService.sendNotification(
                    userId, 
                    "New Routine Assigned", 
                    "You have been assigned the routine: " + routine.getName(), 
                    "ROUTINE_ASSIGNMENT", 
                    Optional.of(routineId)
                );
            }
        }
        
        return savedUserRoutine;
    }

    @Override
    public List<Routine> getUserRoutines(UUID userId, boolean activeOnly) {
        List<UserRoutine> userRoutines;
        
        if (activeOnly) {
            userRoutines = userRoutineRepository.findByUserIdAndActiveTrue(userId);
        } else {
            userRoutines = userRoutineRepository.findByUserId(userId);
        }
        
        List<UUID> routineIds = userRoutines.stream()
                .map(UserRoutine::getRoutineId)
                .collect(Collectors.toList());
        
        return routineRepository.findAllById(routineIds);
    }

    @Override
    @Transactional
    public Routine updateRoutine(UUID routineId, Routine routine, UUID trainerId) {
        Routine existingRoutine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        
        existingRoutine.setName(routine.getName());
        existingRoutine.setDescription(routine.getDescription());
        existingRoutine.setDifficulty(routine.getDifficulty());
        existingRoutine.setGoal(routine.getGoal());
        
        return routineRepository.save(existingRoutine);
    }

    @Override
    @Transactional
    public RoutineExercise addExerciseToRoutine(UUID routineId, RoutineExercise exercise) {
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        
        exercise.setRoutineId(routineId);
        return routineExerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public boolean removeExerciseFromRoutine(UUID routineId, UUID exerciseId) {
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        
        Optional<RoutineExercise> exercise = routineExerciseRepository.findById(exerciseId);
        if (exercise.isPresent() && exercise.get().getRoutineId().equals(routineId)) {
            routineExerciseRepository.deleteById(exerciseId);
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<Routine> getRoutineById(UUID routineId) {
        return routineRepository.findById(routineId);
    }

    @Override
    public boolean deactivateUserRoutine(UUID userId, UUID routineId) {
        Optional<UserRoutine> userRoutineOpt = userRoutineRepository.findByUserIdAndRoutineId(userId, routineId);
        
        if (userRoutineOpt.isPresent()) {
            UserRoutine userRoutine = userRoutineOpt.get();
            userRoutine.setActive(false);
            userRoutineRepository.save(userRoutine);
            return true;
        }
        
        return false;
    }
}