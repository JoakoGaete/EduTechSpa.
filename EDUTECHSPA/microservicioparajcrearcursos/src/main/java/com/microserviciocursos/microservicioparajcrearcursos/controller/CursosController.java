package com.microserviciocursos.microservicioparajcrearcursos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.microserviciocursos.microservicioparajcrearcursos.assembler.CursosModelAssembler;
import com.microserviciocursos.microservicioparajcrearcursos.client.AsignarClient;
import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;
import com.microserviciocursos.microservicioparajcrearcursos.service.CursosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/cursos")
public class CursosController {
    @Autowired
    private CursosModelAssembler assembler;
    
    @Autowired
    private CursosService cursosService;
    
    @Autowired
    private AsignarClient asignarClient;

    // MÉTODO GET - LISTAR TODOS LOS CURSOS
    @GetMapping()
    public ResponseEntity<List<CursosModel>> obtenerCursos() {
        List<CursosModel> listaCursos = cursosService.obtenerListadoCursos();
        if (listaCursos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listaCursos);
    }

    // MÉTODO GET - OBTENER CURSO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCursoPorId(@PathVariable Long id) {
        try {
            CursosModel curso = cursosService.buscarCursoPorId(id);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<CursosModel>> listarCursosHateoas() {
        List<EntityModel<CursosModel>> cursos = cursosService.obtenerListadoCursos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(cursos,
                linkTo(methodOn(CursosController.class).listarCursosHateoas()).withSelfRel());
    }

    // MÉTODO POST - GUARDAR CURSO Y ENVIAR A ASIGNACIÓN
    @PostMapping()
    public ResponseEntity<?> guardarCurso(@RequestBody @Valid CursosModel cursoNuevo) {
        try {
            // Guardar el curso
            CursosModel cursoGuardado = cursosService.guardarCurso(cursoNuevo);
            
            // Enviar el curso al microservicio de asignación para asignación manual
            asignarClient.asignarProfesorAutomaticamente(cursoGuardado)
                .subscribe(resultado -> {
                    System.out.println("Curso enviado al microservicio de asignación: " + resultado);
                }, error -> {
                    System.err.println("Error al enviar curso a asignación: " + error.getMessage());
                });
            
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MÉTODO POST - GUARDAR CURSO CON ASIGNACIÓN AUTOMÁTICA
    @PostMapping("/con-asignacion")
    public ResponseEntity<?> guardarCursoConAsignacion(@RequestBody @Valid CursosModel cursoNuevo) {
        try {
            // Guardar el curso
            CursosModel cursoGuardado = cursosService.guardarCurso(cursoNuevo);
            
            // Luego asignar un profesor automáticamente
            asignarClient.asignarProfesorAutomaticamente(cursoGuardado)
                .subscribe(resultado -> {
                    System.out.println("Profesor asignado automáticamente: " + resultado);
                }, error -> {
                    System.err.println("Error al asignar profesor: " + error.getMessage());
                });
            
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MÉTODO PUT - ACTUALIZAR CURSO POR ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCurso(@PathVariable Long id, @RequestBody @Valid CursosModel cursoActualizado) {
        try {
            CursosModel curso = cursosService.actualizarCurso(id, cursoActualizado);
            return ResponseEntity.ok(curso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el curso: " + e.getMessage());
        }
    }

    // MÉTODO DELETE - ELIMINAR CURSO POR ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCurso(@PathVariable Long id) {
        try {
            cursosService.borrarCurso(id);
            return ResponseEntity.ok("Curso eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el curso: " + e.getMessage());
        }
    }
}
