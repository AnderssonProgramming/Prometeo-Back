package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.BaseExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BaseExerciseRepository extends JpaRepository<BaseExercise, UUID> {
    
    List<BaseExercise> findByMuscleGroup(String muscleGroup);
    
    List<BaseExercise> findByEquipment(String equipment);
    
    List<BaseExercise> findByNameContainingIgnoreCase(String name);
    
    List<BaseExercise> findByDeletedAtIsNull();
}