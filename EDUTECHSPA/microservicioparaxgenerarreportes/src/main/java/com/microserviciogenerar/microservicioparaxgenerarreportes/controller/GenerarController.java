package com.microserviciogenerar.microservicioparaxgenerarreportes.controller;

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

import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;
import com.microserviciogenerar.microservicioparaxgenerarreportes.model.UsuarioDTO;
import com.microserviciogenerar.microservicioparaxgenerarreportes.client.SolucionClient;
import com.microserviciogenerar.microservicioparaxgenerarreportes.service.GenerarService;
import com.microserviciogenerar.microservicioparaxgenerarreportes.assembler.GenerarModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/generar")
public class GenerarController {

    @Autowired
    private GenerarService generarService;

    @Autowired
    private GenerarModelAssembler assembler;

    @Autowired
    private SolucionClient solucionClient;

    // MÉTODO POST - RECIBIR DATOS DE USUARIO DESDE MICROSERVICIO DE CREAR CUENTAS
    @PostMapping("/recibir-usuario")
    public ResponseEntity<String> recibirUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            String mensaje = String.format(
                "Usuario %s %s (ID: %d) recibido en el microservicio de generar reportes. " +
                "Use el endpoint /api/generar/crear-reporte para generar un reporte manual.",
                usuarioDTO.getNombreUsuario(),
                usuarioDTO.getApellidoUsuario(),
                usuarioDTO.getIdUsuario()
            );
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al recibir usuario: " + e.getMessage());
        }
    }

    // MÉTODO POST - CREAR REPORTE MANUAL
    @PostMapping("/crear-reporte")
    public ResponseEntity<?> crearReporteManual(@RequestBody GenerarModel reporteManual) {
        try {
            // Validar que los datos necesarios estén presentes
            if (reporteManual.getIdUsuario() == null || reporteManual.getDescripcionReporte() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Se requieren ID de usuario y descripción para crear el reporte");
            }
            
            // Establecer fecha automática si no se proporciona
            if (reporteManual.getFechaReporte() == null || reporteManual.getFechaReporte().isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                reporteManual.setFechaReporte(now.format(formatter));
            }
            
            // Guardar el reporte
            GenerarModel reporteGuardado = generarService.guardarReporte(reporteManual);
            
            // Enviar datos del reporte al microservicio de responder reportes
            solucionClient.enviarReporteParaSolucion(reporteGuardado)
                .subscribe(resultado -> {
                    System.out.println("Reporte enviado al microservicio de soluciones: " + resultado);
                }, error -> {
                    System.err.println("Error al enviar reporte a soluciones: " + error.getMessage());
                });
            
            String mensaje = String.format(
                "Reporte creado exitosamente para el usuario %s. " +
                "ID de reporte: %d, Fecha: %s",
                reporteManual.getNombreUsuario(),
                reporteGuardado.getIdReporte(),
                reporteManual.getFechaReporte()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el reporte: " + e.getMessage());
        }
    }

    // GET - Listar todos los reportes
    @GetMapping
    public ResponseEntity<List<GenerarModel>> obtenerTodosLosReportes() {
        List<GenerarModel> reportes = generarService.obtenerListadoReportes();
        if (reportes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reportes);
    }

    // GET - Obtener un reporte por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReportePorId(@PathVariable Long id) {
        try {
            GenerarModel reporte = generarService.buscarReportePorId(id);
            return ResponseEntity.ok(reporte);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    // POST - Crear un nuevo reporte
    @PostMapping
    public ResponseEntity<?> crearReporte(@Valid @RequestBody GenerarModel nuevoReporte) {
        try {
            GenerarModel reporteGuardado = generarService.guardarReporte(nuevoReporte);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporteGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear reporte: " + e.getMessage());
        }
    }

    // PUT - Actualizar un reporte existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReporte(@PathVariable Long id, @Valid @RequestBody GenerarModel reporteActualizado) {
        try {
            GenerarModel reporte = generarService.actualizarReporte(id, reporteActualizado);
            return ResponseEntity.ok(reporte);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar reporte: " + e.getMessage());
        }
    }

    // DELETE - Eliminar un reporte por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            String mensaje = generarService.borrarReporte(id);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar reporte: " + e.getMessage());
        }
    }

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<GenerarModel>> listarReportesHateoas() {
        List<EntityModel<GenerarModel>> reportes = generarService.obtenerListadoReportes().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                reportes,
                linkTo(methodOn(GenerarController.class).listarReportesHateoas()).withSelfRel()
        );
    }
}

