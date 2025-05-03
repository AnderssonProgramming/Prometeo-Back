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
    // public void removeExercise(UUID exerciseId) {
    //     exercises.removeIf(exercise -> exercise.getId().equals(exerciseId));
    // }

    // public void updateExerciseOrder(UUID exerciseId, int newOrder) {
    //     exercises.stream()
    //             .filter(exercise -> exercise.getId().equals(exerciseId))
    //             .findFirst()
    //             .ifPresent(exercise -> exercise.setSequenceOrder(newOrder));
    // }

    public boolean isAppropriateFor(PhysicalProgress progress) {
        // Implementación simplificada
        return true;
    }
}
