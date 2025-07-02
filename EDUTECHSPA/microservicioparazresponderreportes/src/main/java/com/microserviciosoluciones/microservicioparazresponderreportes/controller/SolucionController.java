package com.microserviciosoluciones.microservicioparazresponderreportes.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviciosoluciones.microservicioparazresponderreportes.model.SolucionModel;
import com.microserviciosoluciones.microservicioparazresponderreportes.model.ReporteDTO;
import com.microserviciosoluciones.microservicioparazresponderreportes.service.SolucionService;
import com.microserviciosoluciones.microservicioparazresponderreportes.assembler.SolucionModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/soluciones")
public class SolucionController {

    @Autowired
    private SolucionService solucionService;

    @Autowired
    private SolucionModelAssembler assembler;

    // MÉTODO POST - RECIBIR DATOS DE REPORTE DESDE MICROSERVICIO DE GENERAR REPORTES
    @PostMapping("/recibir-reporte")
    public ResponseEntity<String> recibirReporte(@RequestBody ReporteDTO reporteDTO) {
        try {
            String mensaje = String.format(
                "Reporte del usuario %s (ID Reporte: %d) recibido en el microservicio de soluciones. " +
                "Use el endpoint /api/soluciones/crear-solucion para crear una solución manual.",
                reporteDTO.getNombreUsuario(),
                reporteDTO.getIdReporte()
            );
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al recibir reporte: " + e.getMessage());
        }
    }

    // MÉTODO POST - CREAR SOLUCIÓN MANUAL
    @PostMapping("/crear-solucion")
    public ResponseEntity<?> crearSolucionManual(@RequestBody SolucionModel solucionManual) {
        try {
            // Validar que los datos necesarios estén presentes
            if (solucionManual.getIdReporte() == null || solucionManual.getSolucionReporte() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Se requieren ID de reporte y solución para crear la respuesta");
            }
            
            // Establecer fecha automática si no se proporciona
            if (solucionManual.getFechaSolucion() == null || solucionManual.getFechaSolucion().isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                solucionManual.setFechaSolucion(now.format(formatter));
            }
            
            // Guardar la solución
            SolucionModel solucionGuardada = solucionService.guardarSolucion(solucionManual);
            
            String mensaje = String.format(
                "Solución creada exitosamente para el reporte del usuario %s. " +
                "ID de solución: %d, Fecha: %s",
                solucionManual.getNombreUsuario(),
                solucionGuardada.getIdSolucion(),
                solucionManual.getFechaSolucion()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la solución: " + e.getMessage());
        }
    }

    // GET - Listar todas las soluciones
    @GetMapping
    public ResponseEntity<List<SolucionModel>> obtenerTodasLasSoluciones() {
        List<SolucionModel> soluciones = solucionService.obtenerListadoSoluciones();
        if (soluciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(soluciones);
    }

    // GET - Obtener solución por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSolucionPorId(@PathVariable Long id) {
        try {
            SolucionModel solucion = solucionService.buscarSolucionPorId(id);
            return ResponseEntity.ok(solucion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // POST - Crear nueva solución
    @PostMapping
    public ResponseEntity<?> crearSolucion(@Valid @RequestBody SolucionModel nuevaSolucion) {
        try {
            SolucionModel solucionCreada = solucionService.guardarSolucion(nuevaSolucion);
            return ResponseEntity.status(HttpStatus.CREATED).body(solucionCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear solución: " + e.getMessage());
        }
    }

    // PUT - Actualizar solución por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSolucion(@PathVariable Long id, @Valid @RequestBody SolucionModel solucionActualizada) {
        try {
            SolucionModel solucion = solucionService.actualizarSolucion(id, solucionActualizada);
            return ResponseEntity.ok(solucion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar solución: " + e.getMessage());
        }
    }

    // DELETE - Eliminar solución por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolucion(@PathVariable Long id) {
        try {
            String mensaje = solucionService.borrarSolucion(id);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar solución: " + e.getMessage());
        }
    }

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<SolucionModel>> listarSolucionesHateoas() {
        List<EntityModel<SolucionModel>> soluciones = solucionService.obtenerListadoSoluciones().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                soluciones,
                linkTo(methodOn(SolucionController.class).listarSolucionesHateoas()).withSelfRel()
        );
    }
}