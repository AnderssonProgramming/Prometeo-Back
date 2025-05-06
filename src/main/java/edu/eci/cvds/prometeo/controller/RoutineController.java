// package edu.eci.cvds.prometeo.controller;

// import edu.eci.cvds.prometeo.PrometeoExceptions;
// import edu.eci.cvds.prometeo.dto.RoutineDTO;
// import edu.eci.cvds.prometeo.service.RoutineService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import lombok.RequiredArgsConstructor;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/routines")
// @RequiredArgsConstructor
// @Tag(name = "Routines", description = "Gestión de rutinas de entrenamiento")
// public class RoutineController {

//     @Autowired
//     private RoutineService routineService;

//     @Operation(summary = "Listar todas las rutinas generales")
//     @ApiResponse(responseCode = "200", description = "Rutinas listadas")
//     @GetMapping
//     public ResponseEntity<List<RoutineDTO>> getAll() {
//         return ResponseEntity.ok(routineService.getAll());
//     }

//     @Operation(summary = "Consultar detalles de una rutina")
//     @ApiResponses({
//         @ApiResponse(responseCode = "200", description = "Rutina encontrada"),
//         @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
//     })
//     @GetMapping("/{id}")
//     public ResponseEntity<RoutineDTO> getById(@PathVariable UUID id) {
//         return ResponseEntity.ok(routineService.getById(id)
//                 .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_RUTINA)));
//     }

//     @Operation(summary = "Crear nueva rutina")
//     @ApiResponses({
//         @ApiResponse(responseCode = "201", description = "Rutina creada correctamente"),
//         @ApiResponse(responseCode = "400", description = "Datos inválidos")
//     })
//     @PostMapping
//     public ResponseEntity<RoutineDTO> create(@RequestBody RoutineDTO dto) {
//         try {
//             RoutineDTO created = routineService.createRoutine(dto);
//             return ResponseEntity.status(HttpStatus.CREATED).body(created);
//         } catch (PrometeoExceptions e) {
//             throw e;
//         } catch (Exception e) {
//             throw new PrometeoExceptions("Error al crear rutina: " + e.getMessage());
//         }
//     }

//     @Operation(summary = "Actualizar rutina existente")
//     @ApiResponses({
//         @ApiResponse(responseCode = "200", description = "Rutina actualizada correctamente"),
//         @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
//     })
//     @PutMapping("/{id}")
//     public ResponseEntity<RoutineDTO> update(
//             @Parameter(description = "ID de la rutina") @PathVariable UUID id,
//             @RequestBody RoutineDTO dto) {
//         try {
//             dto.setId(id);
//             return ResponseEntity.ok(routineService.updateRoutine(dto));
//         } catch (PrometeoExceptions e) {
//             throw e;
//         } catch (Exception e) {
//             throw new PrometeoExceptions("Error al actualizar rutina: " + e.getMessage());
//         }
//     }

//     @Operation(summary = "Eliminar rutina")
//     @ApiResponses({
//         @ApiResponse(responseCode = "204", description = "Rutina eliminada correctamente"),
//         @ApiResponse(responseCode = "404", description = "Rutina no encontrada")
//     })
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> delete(
//             @Parameter(description = "ID de la rutina") @PathVariable UUID id) {
//         try {
//             routineService.deleteRoutine(id);
//             return ResponseEntity.noContent().build();
//         } catch (PrometeoExceptions e) {
//             throw e;
//         } catch (Exception e) {
//             throw new PrometeoExceptions("Error al eliminar rutina: " + e.getMessage());
//         }
//     }

//     @Operation(summary = "Obtener rutinas de un usuario")
//     @ApiResponses({
//         @ApiResponse(responseCode = "200", description = "Rutinas obtenidas correctamente"),
//         @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
//     })
//     @GetMapping("/user/{userId}")
//     public ResponseEntity<List<RoutineDTO>> getByUserId(
//             @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
//         try {
//             return ResponseEntity.ok(routineService.getRoutinesByUser(userId));
//         } catch (PrometeoExceptions e) {
//             throw e;
//         } catch (Exception e) {
//             throw new PrometeoExceptions("Error al obtener rutinas: " + e.getMessage());
//         }
//     }

//     @Operation(summary = "Asignar rutina a usuario")
//     @ApiResponses({
//         @ApiResponse(responseCode = "200", description = "Rutina asignada"),
//         @ApiResponse(responseCode = "400", description = "Datos inválidos")
//     })
//     @PostMapping("/assign")
//     public ResponseEntity<Void> assignRoutine(@RequestParam UUID userId, @RequestParam UUID routineId) {
//         try {
//             routineService.assignRoutine(userId, routineId);
//             return ResponseEntity.ok().build();
//         } catch (PrometeoExceptions e) {
//             throw e;
//         }
//     }
// }
