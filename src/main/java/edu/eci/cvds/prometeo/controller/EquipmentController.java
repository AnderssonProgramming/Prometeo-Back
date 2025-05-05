package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.EquipmentDTO;
import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.service.EquipmentService;
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

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@Tag(name = "Equipment", description = "Gestión y consulta de equipos")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Operation(summary = "Listar todos los equipos")
    @ApiResponse(responseCode = "200", description = "Equipos listados")
    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAll() {
        try {
            return ResponseEntity.ok(equipmentService.getAll());
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener equipos: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar detalles de un equipo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(equipmentService.getById(id)
                    .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO)));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al consultar equipo: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar equipos disponibles para reserva")
    @ApiResponse(responseCode = "200", description = "Equipos disponibles listados")
    @GetMapping("/available")
    public ResponseEntity<List<EquipmentDTO>> getAvailable() {
        try {
            return ResponseEntity.ok(equipmentService.getAvailable());
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener equipos disponibles: " + e.getMessage());
        }
    }

    @Operation(summary = "Crear nuevo equipo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Equipo creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EquipmentDTO> create(@Valid @RequestBody EquipmentDTO equipmentDTO) {
        try {
            EquipmentDTO created = equipmentService.save(equipmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al crear equipo: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar equipo existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo actualizado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody EquipmentDTO equipmentDTO) {
        try {
            if (!equipmentService.exists(id)) {
                throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO);
            }
            equipmentDTO.setId(id);
            return ResponseEntity.ok(equipmentService.update(equipmentDTO));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al actualizar equipo: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar equipo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Equipo eliminado"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            if (!equipmentService.exists(id)) {
                throw new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_EQUIPO);
            }
            equipmentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al eliminar equipo: " + e.getMessage());
        }
    }

    @Operation(summary = "Marcar equipo como en mantenimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo marcado para mantenimiento"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<EquipmentDTO> setMaintenance(
            @PathVariable UUID id,
            @RequestParam @Parameter(description = "Fecha estimada de finalización del mantenimiento") 
            LocalDate endDate) {
        try {
            EquipmentDTO updated = equipmentService.sendToMaintenance(id, endDate);
            return ResponseEntity.ok(updated);
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al configurar mantenimiento: " + e.getMessage());
        }
    }

    @Operation(summary = "Marcar equipo como disponible después de mantenimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo marcado como disponible"),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    @PatchMapping("/{id}/available")
    public ResponseEntity<EquipmentDTO> setAvailable(@PathVariable UUID id) {
        try {
            EquipmentDTO updated = equipmentService.completeMaintenance(id);
            return ResponseEntity.ok(updated);
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al marcar como disponible: " + e.getMessage());
        }
    }

    @Operation(summary = "Filtrar equipos por tipo")
    @ApiResponse(responseCode = "200", description = "Equipos filtrados")
    @GetMapping("/filter")
    public ResponseEntity<List<EquipmentDTO>> filterByType(
            @RequestParam @Parameter(description = "Tipo de equipo") String type) {
        try {
            return ResponseEntity.ok(equipmentService.findByType(type));
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al filtrar equipos: " + e.getMessage());
        }
    }
}
