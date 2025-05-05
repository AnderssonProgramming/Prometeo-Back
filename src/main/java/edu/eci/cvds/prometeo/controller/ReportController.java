package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.ReportDTO;
import edu.eci.cvds.prometeo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Generación de reportes y estadísticas")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "Generar reporte de progreso de usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ReportDTO> generateUserReport(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Fecha de inicio") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return ResponseEntity.ok(reportService.generateUserProgressReport(userId, startDate, endDate));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al generar reporte: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener estadísticas de uso de equipamiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estadísticas generadas correctamente")
    })
    @GetMapping("/equipment/usage")
    public ResponseEntity<Map<String, Integer>> getEquipmentUsageStats(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return ResponseEntity.ok(reportService.getEquipmentUsageStatistics(startDate, endDate));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener reportes de mantenimiento pendientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes obtenidos correctamente")
    })
    @GetMapping("/maintenance")
    public ResponseEntity<List<ReportDTO>> getMaintenanceReports() {
        try {
            return ResponseEntity.ok(reportService.getPendingMaintenanceReports());
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener reportes de mantenimiento: " + e.getMessage());
        }
    }
}
