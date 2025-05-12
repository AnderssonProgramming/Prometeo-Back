package edu.eci.cvds.prometeo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class RoutineDTOTest {

    @Test
    void testRoutineDTOCreation() {
        UUID id = UUID.randomUUID();
        String name = "Full Body Workout";
        String description = "Routine focused on full-body strength.";
        String difficulty = "Intermediate";
        String goal = "Muscle Gain";
        UUID trainerId = UUID.randomUUID();
        LocalDate creationDate = LocalDate.now();
        List<RoutineExerciseDTO> exercises = List.of(new RoutineExerciseDTO(), new RoutineExerciseDTO());

        RoutineDTO dto = new RoutineDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setDifficulty(difficulty);
        dto.setGoal(goal);
        dto.setTrainerId(trainerId);
        dto.setCreationDate(creationDate);
        dto.setExercises(exercises);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getDescription()).isEqualTo(description);
        assertThat(dto.getDifficulty()).isEqualTo(difficulty);
        assertThat(dto.getGoal()).isEqualTo(goal);
        assertThat(dto.getTrainerId()).isEqualTo(trainerId);
        assertThat(dto.getCreationDate()).isEqualTo(creationDate);
        assertThat(dto.getExercises()).isEqualTo(exercises);
    }

    @Test
    void testDefaultValues() {
        RoutineDTO dto = new RoutineDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getDifficulty()).isNull();
        assertThat(dto.getGoal()).isNull();
        assertThat(dto.getTrainerId()).isNull();
        assertThat(dto.getCreationDate()).isNull();
        assertThat(dto.getExercises()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        RoutineDTO dto1 = new RoutineDTO();
        dto1.setId(id);

        RoutineDTO dto2 = new RoutineDTO();
        dto2.setId(id);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RoutineDTO dto = new RoutineDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("Leg Day Routine");
        dto.setDescription("Focuses on lower body strength.");
        dto.setDifficulty("Advanced");
        dto.setGoal("Strength");
        dto.setTrainerId(UUID.randomUUID());
        dto.setCreationDate(LocalDate.now());
        dto.setExercises(List.of(new RoutineExerciseDTO()));

        String json = objectMapper.writeValueAsString(dto);
        RoutineDTO deserialized = objectMapper.readValue(json, RoutineDTO.class);

        assertThat(deserialized).usingRecursiveComparison().isEqualTo(dto);
    }
}
