package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, UUID> {
    
    List<RoutineExercise> findByRoutineId(UUID routineId);
    
    List<RoutineExercise> findByRoutineIdOrderBySequenceOrder(UUID routineId);
    
    List<RoutineExercise> findByBaseExerciseId(UUID baseExerciseId);
    
    void deleteByRoutineId(UUID routineId);
}