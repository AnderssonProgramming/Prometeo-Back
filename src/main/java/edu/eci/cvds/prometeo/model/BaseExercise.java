package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "base_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseExercise extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "muscle_group")
    private String muscleGroup;

    @Column(name = "equipment")
    private String equipment;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    public boolean requiresEquipment() {
        return equipment != null && !equipment.isEmpty() && !equipment.equalsIgnoreCase("none");
    }
}
