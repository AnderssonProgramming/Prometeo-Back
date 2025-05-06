package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.PhysicalProgressDTO;
import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.service.PhysicalProgressService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Physical Progress", description = "Endpoints para gestionar el progreso físico de los usuarios")
public class PhysicalProgressController {

    @Autowired
    private PhysicalProgressService physicalProgressService;

    @Operation(summary = "Registrar nueva medición física")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Medición registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<PhysicalProgressDTO> create(@RequestBody PhysicalProgressDTO dto) {
        try {
            PhysicalProgressDTO created = physicalProgressService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (PrometeoExceptions e) {
            throw e;
        }
    }

    @Operation(summary = "Listar historial de progreso de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<PhysicalProgressDTO>> getByUserId(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(physicalProgressService.getByUserId(userId));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener historial de progreso: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener la última medición de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Última medición obtenida"),
        @ApiResponse(responseCode = "404", description = "Usuario o medición no encontrada")
    })
    @GetMapping("/{userId}/latest")
    public ResponseEntity<PhysicalProgressDTO> getLatestByUserId(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(physicalProgressService.getLatestByUserId(userId));
        } catch (PrometeoExceptions e) {
            throw e;
        }
    }

    @Operation(summary = "Actualizar una medición física")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medición actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Medición no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PhysicalProgressDTO> update(
            @Parameter(description = "ID de la medición") @PathVariable UUID id,
            @RequestBody PhysicalProgressDTO dto) {
        try {
            PhysicalProgressDTO updated = physicalProgressService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (PrometeoExceptions e) {
            throw e;
        }
    }

    @Operation(summary = "Eliminar una medición física")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Medición eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Medición no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la medición") @PathVariable UUID id) {
        try {
            physicalProgressService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PrometeoExceptions e) {
            throw e;
        }
    }

    @Operation(summary = "Calcular métricas de progreso")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métricas calculadas correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/metrics")
    public ResponseEntity<Map<String, Double>> calculateMetrics(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Número de meses para analizar") @RequestParam(defaultValue = "3") int months) {
        try {
            return ResponseEntity.ok(physicalProgressService.calculateProgressMetrics(userId, months));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al calcular métricas: " + e.getMessage());
        }
    }
}
