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
}
