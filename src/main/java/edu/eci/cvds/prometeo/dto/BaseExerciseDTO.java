package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BaseExerciseDTO {
    private UUID id;
    private String name;
    private String description;
    private String muscleGroup;
    private String equipment;
    private String videoUrl;
    private String imageUrl;

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
