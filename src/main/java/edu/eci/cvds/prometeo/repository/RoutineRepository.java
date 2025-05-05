package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, UUID> {
    
    List<Routine> findByTrainerId(UUID trainerId);
    
    List<Routine> findByDifficulty(String difficulty);
    
    List<Routine> findByGoal(String goal);
    
    List<Routine> findByTrainerIdAndDeletedAtIsNull(UUID trainerId);
}