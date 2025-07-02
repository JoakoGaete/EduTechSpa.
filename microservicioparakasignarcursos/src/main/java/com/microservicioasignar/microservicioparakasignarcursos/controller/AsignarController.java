package com.microservicioasignar.microservicioparakasignarcursos.controller;

import java.util.List;

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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.microservicioasignar.microservicioparakasignarcursos.assembler.AsignarModelAssembler;
import com.microservicioasignar.microservicioparakasignarcursos.client.CompraClient;
import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;
import com.microservicioasignar.microservicioparakasignarcursos.model.CursoDTO;
import com.microservicioasignar.microservicioparakasignarcursos.model.ProfesorModel;
import com.microservicioasignar.microservicioparakasignarcursos.service.AsignarService;
import com.microservicioasignar.microservicioparakasignarcursos.service.ProfesorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/asignar")
public class AsignarController {

    @Autowired
    private AsignarModelAssembler assembler;

    @Autowired
    private AsignarService asignarService;
    
    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private CompraClient compraClient;

    // MÉTODO GET - LISTAR TODOS LOS PROFESORES DISPONIBLES
    @GetMapping("/profesores")
    public ResponseEntity<List<ProfesorModel>> obtenerProfesores() {
        List<ProfesorModel> listaProfesores = profesorService.obtenerListadoProfesores();
        if (listaProfesores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listaProfesores);
    }

    // MÉTODO GET - LISTAR CURSOS PENDIENTES DE ASIGNACIÓN
    @GetMapping("/cursos-pendientes")
    public ResponseEntity<String> obtenerCursosPendientes() {
        try {
            // Aquí podrías implementar lógica para obtener cursos pendientes
            // Por ahora, devolvemos un mensaje informativo
            return ResponseEntity.ok("Para ver cursos pendientes, consulte el microservicio de crear cursos en /api/cursos");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener cursos pendientes: " + e.getMessage());
        }
    }

    // MÉTODO GET - LISTAR TODOS LOS REGISTROS DE ASIGNAR
    @GetMapping()
    public ResponseEntity<List<AsignarModel>> obtenerAsignaciones() {
        List<AsignarModel> listaAsignaciones = asignarService.obtenerListadoAsignar();
        if (listaAsignaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listaAsignaciones);
    }

    // MÉTODO GET - OBTENER UN REGISTRO DE ASIGNAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsignarPorId(@PathVariable Long id) {
        try {
            AsignarModel asignar = asignarService.buscarAsignarPorId(id);
            return ResponseEntity.ok(asignar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<AsignarModel>> listarAsignacionesHateoas() {
        List<EntityModel<AsignarModel>> asignaciones = asignarService.obtenerListadoAsignar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(asignaciones,
                linkTo(methodOn(AsignarController.class).listarAsignacionesHateoas()).withSelfRel());
    }

    // MÉTODO POST - RECIBIR CURSO PARA ASIGNACIÓN MANUAL
    @PostMapping("/asignacion-automatica")
    public ResponseEntity<String> recibirCursoParaAsignacion(@RequestBody CursoDTO cursoDTO) {
        try {
            // Solo recibir el curso, sin asignar profesor automáticamente
            String mensaje = String.format(
                "Curso '%s' (ID: %d) recibido en el microservicio de asignación. " +
                "Use el endpoint /api/asignar/asignacion-manual para asignar un profesor.",
                cursoDTO.getNombreCurso(),
                cursoDTO.getIdCurso()
            );
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al recibir curso: " + e.getMessage());
        }
    }

    // MÉTODO POST - ASIGNACIÓN MANUAL DE PROFESOR A CURSO
    @PostMapping("/asignacion-manual")
    public ResponseEntity<String> asignacionManual(@RequestBody AsignarModel asignacionManual) {
        try {
            // Verificar que el profesor existe
            List<ProfesorModel> profesores = profesorService.obtenerListadoProfesores();
            boolean profesorExiste = profesores.stream()
                .anyMatch(p -> p.getNombreProfesor().equals(asignacionManual.getNombreProfesor()) 
                           && p.getApellidoProfesor().equals(asignacionManual.getApellidoProfesor()));
            
            if (!profesorExiste) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El profesor especificado no existe en la base de datos");
            }
            
            // Guardar la asignación
            AsignarModel asignacionGuardada = asignarService.guardarAsignar(asignacionManual);
            
            // Enviar datos de la asignación al microservicio de compras
            compraClient.enviarAsignacionParaCompra(asignacionGuardada)
                .subscribe(resultado -> {
                    System.out.println("Asignación enviada al microservicio de compras: " + resultado);
                }, error -> {
                    System.err.println("Error al enviar asignación a compras: " + error.getMessage());
                });
            
            String mensaje = String.format(
                "Profesor %s %s asignado manualmente al curso '%s' (ID: %d)",
                asignacionManual.getNombreProfesor(),
                asignacionManual.getApellidoProfesor(),
                asignacionManual.getNombreCurso(),
                asignacionManual.getIdCurso()
            );
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al asignar profesor manualmente: " + e.getMessage());
        }
    }

    // MÉTODO POST - GUARDAR ASIGNACIÓN
    @PostMapping()
    public ResponseEntity<?> guardarAsignar(@RequestBody @Valid AsignarModel asignarNuevo) {
        try {
            AsignarModel asignar = asignarService.guardarAsignar(asignarNuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(asignar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MÉTODO PUT - ACTUALIZAR ASIGNACIÓN POR ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAsignar(@PathVariable Long id, @RequestBody @Valid AsignarModel asignarActualizado) {
        try {
            AsignarModel asignar = asignarService.actualizarAsignar(id, asignarActualizado);
            return ResponseEntity.ok(asignar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la asignación: " + e.getMessage());
        }
    }

    // MÉTODO DELETE - ELIMINAR ASIGNACIÓN POR ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarAsignar(@PathVariable Long id) {
        try {
            asignarService.borrarAsignar(id);
            return ResponseEntity.ok("Asignación eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar la asignación: " + e.getMessage());
        }
    }
}
