package com.microservicioinicio.microservicioparainiciarcuentas.controller;

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

import com.microservicioinicio.microservicioparainiciarcuentas.assembler.InicioModelAssembler;
import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;
import com.microservicioinicio.microservicioparainiciarcuentas.service.InicioService;

import jakarta.validation.Valid;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("api/usuarios")
public class InicioController {

    @Autowired
    private InicioModelAssembler assembler;

    @Autowired
    private InicioService inicioService;

    @GetMapping()
    public ResponseEntity<List<InicioModel>> obtenerInicio() {
        List<InicioModel> exitenciaInicio= inicioService.obtenerListadoInicios();
        if (exitenciaInicio.isEmpty()){
            return ResponseEntity.noContent().build();
        } 
        return ResponseEntity.ok(exitenciaInicio);
    }
    //METODO NO ENCUENTRA ROLES
    @GetMapping("/{id}")
    public ResponseEntity<?>obtenerInicioId(@PathVariable Long id) {
        try {InicioModel inicio = inicioService.buscarInicioporId(id);
            return ResponseEntity.ok(inicio);    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<InicioModel>> listarUsuariosHateoas() {
        List<EntityModel<InicioModel>> usuarios = inicioService.obtenerListadoInicios().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(InicioController.class).listarUsuariosHateoas()).withSelfRel()
        );
    }    

    @PostMapping()
    public ResponseEntity<?> guardarInicio(@RequestBody @Valid InicioModel inicioNuevo){
        try {
            String inicio = inicioService.verificarLoginUsuario(inicioNuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(inicio);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());          
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?>actualizarInicioId(@PathVariable Long id, @RequestBody @Valid InicioModel inicioActualizado){
        try {InicioModel inicio = inicioService.actualizarInicio(id, inicioActualizado);
            return ResponseEntity.ok(inicio);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el usuario"+e.getMessage());    
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInicioId(@PathVariable Long id) {
        try {
            inicioService.borrarInicio(id);
            return ResponseEntity.ok("Inicio eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error al eliminar el inicio: " + e.getMessage());
        }
    }        
}
