package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.Routine;
import edu.eci.cvds.prometeo.dto.RoutineDTO;
import edu.eci.cvds.prometeo.service.RoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
@Tag(name = "Routines", description = "Gestión de rutinas de entrenamiento")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    private RoutineDTO convertToDTO(Routine routine) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(routine.getId());
        dto.setName(routine.getName());
        dto.setDescription(routine.getDescription());
        dto.setDifficulty(routine.getDifficulty());
        dto.setGoal(routine.getGoal());
        dto.setCreationDate(routine.getCreationDate());
        dto.setTrainerId(routine.getTrainerId());
        return dto;
    }

    private Routine convertToEntity(RoutineDTO dto) {
        Routine routine = new Routine();
        if (dto.getId() != null) {
            routine.setId(dto.getId());
        }
        routine.setName(dto.getName());
        routine.setDescription(dto.getDescription());
        routine.setDifficulty(dto.getDifficulty());
        routine.setGoal(dto.getGoal());
        return routine;
    }

    @Operation(summary = "Listar todas las rutinas generales")
    @ApiResponse(responseCode = "200", description = "Rutinas listadas")
    @GetMapping
    public ResponseEntity<List<RoutineDTO>> getAll() {
        List<Routine> routines = routineService.getRoutines(Optional.empty(), Optional.empty());
        List<RoutineDTO> dtos = routines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Consultar detalles de una rutina")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutina encontrada"),
            @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoutineDTO> getById(@PathVariable UUID id) {
        Optional<Routine> routineOpt = routineService.getRoutineById(id);
        if (routineOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(routineOpt.get()));
        } else {
            throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RUTINA);
        }
    }

    @Operation(summary = "Crear nueva rutina")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rutina creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<RoutineDTO> create(@RequestBody RoutineDTO dto) {
        try {
            UUID trainerId = null;
            Routine routine = convertToEntity(dto);
            Routine created = routineService.createRoutine(routine, Optional.ofNullable(trainerId));
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(created));
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al crear rutina: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar rutina existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutina actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoutineDTO> update(
            @Parameter(description = "ID de la rutina") @PathVariable UUID id,
            @RequestBody RoutineDTO dto) {
        try {
            UUID trainerId = null;
            dto.setId(id);
            Routine routine = convertToEntity(dto);
            Routine updated = routineService.updateRoutine(id, routine, trainerId);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al actualizar rutina: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar rutina")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rutina eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la rutina") @PathVariable UUID id) {
        try {
            Optional<Routine> routineOpt = routineService.getRoutineById(id);
            if (!routineOpt.isPresent()) {
                throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RUTINA);
            }

            return ResponseEntity.noContent().build();
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al eliminar rutina: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener rutinas de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutinas obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoutineDTO>> getByUserId(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        try {
            List<Routine> routines = routineService.getUserRoutines(userId, true);
            List<RoutineDTO> dtos = routines.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener rutinas: " + e.getMessage());
        }
    }

    @Operation(summary = "Asignar rutina a usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rutina asignada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/assign")
    public ResponseEntity<Void> assignRoutine(@RequestParam UUID userId, @RequestParam UUID routineId) {
        try {
            UUID trainerId = null;
            routineService.assignRoutineToUser(routineId, userId, trainerId, Optional.empty(), Optional.empty());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al asignar rutina: " + e.getMessage());
        }
    }
}