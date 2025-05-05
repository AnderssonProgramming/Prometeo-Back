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
            // Custom query needed if the repository doesn't have the exact method
            List<Routine> routines = routineRepository.findByGoal(goal.get());
            return routines.stream()
                    .filter(r -> difficulty.get().equals(r.getDifficulty()))
                    .collect(Collectors.toList());
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
    public UserRoutine assignRoutineToUser(UUID routineId, UUID userId, UUID trainerId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        // Check if routine exists
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        
        // Deactivate any active routines
        List<UserRoutine> activeRoutines = userRoutineRepository.findByUserIdAndIsActiveTrue(userId);
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
            userRoutines = userRoutineRepository.findByUserIdAndIsActiveTrue(userId);
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
    public Routine updateRoutine(UUID routineId, Routine updatedRoutine, UUID trainerId) {
        return routineRepository.findById(routineId)
                .map(routine -> {
                    // Check if trainer is authorized
                    if (routine.getTrainerId() != null && !routine.getTrainerId().equals(trainerId)) {
                        throw new RuntimeException("Not authorized to update this routine");
                    }
                    
                    // Update fields
                    routine.setName(updatedRoutine.getName());
                    routine.setDescription(updatedRoutine.getDescription());
                    routine.setDifficulty(updatedRoutine.getDifficulty());
                    routine.setGoal(updatedRoutine.getGoal());
                    
                    return routineRepository.save(routine);
                })
                .orElseThrow(() -> new RuntimeException("Routine not found"));
    }

    @Override
    @Transactional
    public RoutineExercise addExerciseToRoutine(UUID routineId, RoutineExercise exercise) {
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        
        exercise.setRoutineId(routineId);
        
        // Determine the next sequence number
        List<RoutineExercise> existingExercises = routineExerciseRepository.findByRoutineIdOrderBySequenceOrder(routineId);
        int nextSequence = 1;
        if (!existingExercises.isEmpty()) {
            nextSequence = existingExercises.get(existingExercises.size() - 1).getSequenceOrder() + 1;
        }
        
        exercise.setSequenceOrder(nextSequence);
        
        return routineExerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public boolean removeExerciseFromRoutine(UUID routineId, UUID exerciseId) {
        try {
            // Verify that the routine and exercise exist
            if (!routineRepository.existsById(routineId)) {
                return false;
            }
            
            RoutineExercise exercise = routineExerciseRepository.findById(exerciseId)
                    .orElse(null);
            
            if (exercise == null || !exercise.getRoutineId().equals(routineId)) {
                return false;
            }
            
            routineExerciseRepository.deleteById(exerciseId);
            
            // Reorder remaining exercises
            List<RoutineExercise> remainingExercises = routineExerciseRepository.findByRoutineIdOrderBySequenceOrder(routineId);
            for (int i = 0; i < remainingExercises.size(); i++) {
                RoutineExercise ex = remainingExercises.get(i);
                ex.setSequenceOrder(i + 1);
                routineExerciseRepository.save(ex);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Routine> getRoutineById(UUID routineId) {
        return routineRepository.findById(routineId);
    }

    @Override
    @Transactional
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