package edu.eci.cvds.prometeo.controller;

import edu.eci.cvds.prometeo.dto.ReservationDTO;
import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.service.GymReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Gym Reservation", description = "Gestión de reservas de gimnasio y equipos")
public class GymReservationController {

    @Autowired
    private GymReservationService gymReservationService;

    @Operation(summary = "Crear nueva reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ReservationDTO> create(@RequestBody ReservationDTO dto) {
        try {
            ReservationDTO created = gymReservationService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al crear la reserva: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar reservas de un usuario")
    @ApiResponse(responseCode = "200", description = "Reservas listadas")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getByUserId(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok(gymReservationService.getByUserId(userId));
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al obtener reservas: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar detalles de una reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(gymReservationService.getById(id)
                    .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RESERVA)));
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al consultar reserva: " + e.getMessage());
        }
    }

    @Operation(summary = "Cancelar reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            gymReservationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PrometeoExceptions e) {
            throw e;
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al cancelar reserva: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar disponibilidad de espacios/equipos por fecha y hora")
    @ApiResponse(responseCode = "200", description = "Disponibilidad consultada")
    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(
            @RequestParam String date,
            @RequestParam String time) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalTime localTime = LocalTime.parse(time);
            return ResponseEntity.ok(gymReservationService.getAvailability(localDate, localTime));
        } catch (Exception e) {
            throw new PrometeoExceptions("Error al consultar disponibilidad: " + e.getMessage());
        }
    }
}
