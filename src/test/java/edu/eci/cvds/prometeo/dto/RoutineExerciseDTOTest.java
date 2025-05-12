package edu.eci.cvds.prometeo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class RoutineExerciseDTOTest {

    @Test
    void testRoutineExerciseDTOCreation() {
        UUID id = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();
        UUID baseExerciseId = UUID.randomUUID();
        int sets = 3;
        int repetitions = 10;
        int restTime = 30;
        int sequenceOrder = 1;

        RoutineExerciseDTO dto = new RoutineExerciseDTO();
        dto.setId(id);
        dto.setRoutineId(routineId);
        dto.setBaseExerciseId(baseExerciseId);
        dto.setSets(sets);
        dto.setRepetitions(repetitions);
        dto.setRestTime(restTime);
        dto.setSequenceOrder(sequenceOrder);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getRoutineId()).isEqualTo(routineId);
        assertThat(dto.getBaseExerciseId()).isEqualTo(baseExerciseId);
        assertThat(dto.getSets()).isEqualTo(sets);
        assertThat(dto.getRepetitions()).isEqualTo(repetitions);
        assertThat(dto.getRestTime()).isEqualTo(restTime);
        assertThat(dto.getSequenceOrder()).isEqualTo(sequenceOrder);
    }

    @Test
    void testDefaultValues() {
        RoutineExerciseDTO dto = new RoutineExerciseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getRoutineId()).isNull();
        assertThat(dto.getBaseExerciseId()).isNull();
        assertThat(dto.getSets()).isZero();
        assertThat(dto.getRepetitions()).isZero();
        assertThat(dto.getRestTime()).isZero();
        assertThat(dto.getSequenceOrder()).isZero();
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        RoutineExerciseDTO dto1 = new RoutineExerciseDTO();
        dto1.setId(id);

        RoutineExerciseDTO dto2 = new RoutineExerciseDTO();
        dto2.setId(id);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RoutineExerciseDTO dto = new RoutineExerciseDTO();
        dto.setId(UUID.randomUUID());
        dto.setRoutineId(UUID.randomUUID());
        dto.setBaseExerciseId(UUID.randomUUID());
        dto.setSets(3);
        dto.setRepetitions(10);
        dto.setRestTime(30);
        dto.setSequenceOrder(1);

        String json = objectMapper.writeValueAsString(dto);
        RoutineExerciseDTO deserialized = objectMapper.readValue(json, RoutineExerciseDTO.class);

        assertThat(deserialized).usingRecursiveComparison().isEqualTo(dto);
    }
}
