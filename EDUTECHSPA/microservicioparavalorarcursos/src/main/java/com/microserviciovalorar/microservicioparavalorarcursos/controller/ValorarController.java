package com.microserviciovalorar.microservicioparavalorarcursos.controller;

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

import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;
import com.microserviciovalorar.microservicioparavalorarcursos.model.CompraDTO;
import com.microserviciovalorar.microservicioparavalorarcursos.service.ValorarService;
import com.microserviciovalorar.microservicioparavalorarcursos.assembler.ValorarModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/valorar")
public class ValorarController {

    @Autowired
    private ValorarService valorarService;

    @Autowired
    private ValorarModelAssembler assembler;

    // MÉTODO POST - RECIBIR DATOS DE COMPRA DESDE MICROSERVICIO DE COMPRAS
    @PostMapping("/recibir-compra")
    public ResponseEntity<String> recibirCompra(@RequestBody CompraDTO compraDTO) {
        try {
            String mensaje = String.format(
                "Compra del curso '%s' con profesor %s %s (ID Compra: %d) recibida en el microservicio de valoraciones. " +
                "Use el endpoint /api/valorar/realizar-valoracion para realizar una valoración.",
                compraDTO.getNombreCurso(),
                compraDTO.getNombreProfesor(),
                compraDTO.getApellidoProfesor(),
                compraDTO.getIdCompra()
            );
            
            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al recibir compra: " + e.getMessage());
        }
    }

    // MÉTODO POST - REALIZAR VALORACIÓN MANUAL
    @PostMapping("/realizar-valoracion")
    public ResponseEntity<?> realizarValoracion(@RequestBody ValorarModel valoracionManual) {
        try {
            // Validar que los datos necesarios estén presentes
            if (valoracionManual.getIdCompra() == null || valoracionManual.getReseñaUsuario() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Se requieren ID de compra y reseña para realizar la valoración");
            }
            
            // Guardar la valoración
            ValorarModel valoracionGuardada = valorarService.guardarValoracion(valoracionManual);
            
            String mensaje = String.format(
                "Valoración realizada exitosamente. Usuario valoró el curso '%s' con el profesor %s %s. " +
                "ID de valoración: %d",
                valoracionManual.getNombreCurso(),
                valoracionManual.getNombreProfesor(),
                valoracionManual.getApellidoProfesor(),
                valoracionGuardada.getIdValoracion()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al realizar la valoración: " + e.getMessage());
        }
    }

    // GET: Listar todas las valoraciones
    @GetMapping()
    public ResponseEntity<List<ValorarModel>> obtenerValoraciones() {
        List<ValorarModel> valoraciones = valorarService.obtenerListadoValoraciones();
        if (valoraciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(valoraciones);
    }

    // GET: Obtener valoración por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerValoracionPorId(@PathVariable Long id) {
        try {
            ValorarModel valoracion = valorarService.buscarValoracionPorId(id);
            return ResponseEntity.ok(valoracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST: Guardar valoración
    @PostMapping()
    public ResponseEntity<?> guardarValoracion(@RequestBody @Valid ValorarModel nuevaValoracion) {
        try {
            ValorarModel valoracion = valorarService.guardarValoracion(nuevaValoracion);
            return ResponseEntity.status(HttpStatus.CREATED).body(valoracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT: Actualizar valoración por ID
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarValoracion(@PathVariable Long id, @RequestBody @Valid ValorarModel valoracionActualizada) {
        try {
            ValorarModel valoracion = valorarService.actualizarValoracion(id, valoracionActualizada);
            return ResponseEntity.ok(valoracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la valoración: " + e.getMessage());
        }
    }

    // DELETE: Eliminar valoración por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarValoracion(@PathVariable Long id) {
        try {
            valorarService.borrarValoracion(id);
            return ResponseEntity.ok("Valoración eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar la valoración: " + e.getMessage());
        }
    }

    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<ValorarModel>> listarValoracionesHateoas() {
        List<EntityModel<ValorarModel>> valoraciones = valorarService.obtenerListadoValoraciones().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                valoraciones,
                linkTo(methodOn(ValorarController.class).listarValoracionesHateoas()).withSelfRel()
        );
    }
}