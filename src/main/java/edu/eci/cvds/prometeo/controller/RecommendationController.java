package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.RecommendationDTO;
import edu.eci.cvds.prometeo.service.RecommendationService;
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
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Gestión de recomendaciones personalizadas")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Operation(summary = "Obtener recomendaciones para un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recomendaciones obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getForUser(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(recommendationService.getRecommendationsForUser(userId));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener recomendaciones: " + e.getMessage());
        }
    }

    @Operation(summary = "Crear nueva recomendación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recomendación creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<RecommendationDTO> create(@RequestBody RecommendationDTO dto) {
        try {
            RecommendationDTO created = recommendationService.createRecommendation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al crear recomendación: " + e.getMessage());
        }
    }

    @Operation(summary = "Predecir progreso futuro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Predicción calculada correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/predict")
    public ResponseEntity<Map<String, Double>> predictProgress(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Semanas a predecir") @RequestParam(defaultValue = "4") int weeks) {
        try {
            return ResponseEntity.ok(recommendationService.predictProgress(userId, weeks));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al predecir progreso: " + e.getMessage());
        }
    }
}
