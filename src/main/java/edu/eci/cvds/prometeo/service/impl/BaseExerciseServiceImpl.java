package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.BaseExercise;
import edu.eci.cvds.prometeo.dto.BaseExerciseDTO;
import edu.eci.cvds.prometeo.repository.BaseExerciseRepository;
import edu.eci.cvds.prometeo.service.BaseExerciseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BaseExerciseServiceImpl implements BaseExerciseService {

    @Autowired
    private BaseExerciseRepository baseExerciseRepository;
    
    @Override
    @Transactional
    public BaseExercise createExercise(BaseExerciseDTO exerciseDTO) {
        BaseExercise exercise = new BaseExercise();
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setMuscleGroup(exerciseDTO.getMuscleGroup());
        exercise.setEquipment(exerciseDTO.getEquipment());
        exercise.setVideoUrl(exerciseDTO.getVideoUrl());
        exercise.setImageUrl(exerciseDTO.getImageUrl());
        
        return baseExerciseRepository.save(exercise);
    }
    
    @Override
    public List<BaseExercise> getAllExercises() {
        return baseExerciseRepository.findByDeletedAtIsNull();
    }
    
    @Override
    public List<BaseExercise> getExercisesByMuscleGroup(String muscleGroup) {
        return baseExerciseRepository.findByMuscleGroup(muscleGroup);
    }
    
    @Override
    public Optional<BaseExercise> getExerciseById(UUID id) {
        return baseExerciseRepository.findById(id);
    }
    
    @Override
    @Transactional
    public BaseExercise updateExercise(UUID id, BaseExerciseDTO exerciseDTO) {
        BaseExercise exercise = baseExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + id));
        
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setMuscleGroup(exerciseDTO.getMuscleGroup());
        exercise.setEquipment(exerciseDTO.getEquipment());
        exercise.setVideoUrl(exerciseDTO.getVideoUrl());
        exercise.setImageUrl(exerciseDTO.getImageUrl());
        
        return baseExerciseRepository.save(exercise);
    }
    
    @Override
    @Transactional
    public void deleteExercise(UUID id) {
        BaseExercise exercise = baseExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + id));
        
        exercise.setDeletedAt(LocalDateTime.now());
        baseExerciseRepository.save(exercise);
    }
    
    @Override
    public List<BaseExercise> searchExercisesByName(String name) {
        return baseExerciseRepository.findByNameContainingIgnoreCase(name);
    }
}