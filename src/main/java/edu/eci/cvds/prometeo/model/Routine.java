package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "routines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Routine extends AuditableEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "goal")
    private String goal;

    @Column(name = "trainer_id")
    private UUID trainerId;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "routine_id")
    private List<RoutineExercise> exercises = new ArrayList<>();

    public void addExercise(RoutineExercise exercise) {
        exercises.add(exercise);
    }
    // TODO: Fix lombok issue with @Setter and @Getter for exercises
    public void removeExercise(UUID exerciseId) {
        exercises.removeIf(exercise -> {
            // Get the ID directly from the base class or through inheritance
            UUID id = exercise.getId(); // This should work if properly inherited
            
            // If still having issues, you could try accessing the field if it's visible:
            // UUID id = ((BaseEntity)exercise).getId();
            
            return exerciseId.equals(id);
        });
    }

    public void updateExerciseOrder(UUID exerciseId, int newOrder) {
        exercises.stream()
                .filter(exercise -> exercise.getId().equals(exerciseId))
                .findFirst()
                .ifPresent(exercise -> exercise.setSequenceOrder(newOrder));
    }

    public boolean isAppropriateFor(PhysicalProgress progress) {
        // Implementación simplificada
        return true;
    }

    public void setId(UUID id) {this.id = id;}

    public UUID getId() {return id;}

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public UUID getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(UUID trainerId) {
        this.trainerId = trainerId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }
}
