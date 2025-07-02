package com.microserviciocrear.microservicioparacrearcuentas.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviciocrear.microservicioparacrearcuentas.assemblers.CrearModelAssembler;
import com.microserviciocrear.microservicioparacrearcuentas.client.CompraClient;
import com.microserviciocrear.microservicioparacrearcuentas.client.GenerarReportesClient;
import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;
import com.microserviciocrear.microservicioparacrearcuentas.service.CrearService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/registro")
public class CrearController {
    
    private final CrearService service;

    public CrearController(CrearService service) {
        this.service = service;
    }
    
    @Autowired
    private CrearModelAssembler assembler;

    @Autowired
    private CrearService crearService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CompraClient compraClient;

    @Autowired
    private GenerarReportesClient generarReportesClient;

    // GET - Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<CrearModel>> obtenerCrear() {
        List<CrearModel> existenciaCrear = crearService.obtenerListadoCrear();
        if (existenciaCrear.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(existenciaCrear);
    }

    // GET - Buscar usuario por ID
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerCrearPorId(@PathVariable Long idUsuario) {
        try {
            CrearModel usuario = crearService.buscarCrearPorId(idUsuario);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //GET - Listar todos los usuarios con Hateoas
    
    @GetMapping("/hateoas")
    public CollectionModel<EntityModel<CrearModel>> listarUsuariosHateoas() {
        List<EntityModel<CrearModel>> usuarios = crearService.obtenerListadoCrear().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(
                usuarios,
                linkTo(methodOn(CrearController.class).listarUsuariosHateoas()).withSelfRel()
        );
    }


    // POST - Validar login
    @PostMapping("/login")
    public ResponseEntity<?> obtenerDatosUsuario(@RequestBody CrearModel login) {
        try {
            CrearModel usuario = crearService.buscarUsuarioPorCorreo(login);
            if (!passwordEncoder.matches(login.getPassword(), usuario.getPassword())) {
                throw new RuntimeException("Contraseña incorrecta");
            }
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error de autenticación: " + e.getMessage());
        }
    }

    // POST - Registrar nuevo usuario y enviar al microservicio de compras y generar reportes
    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody CrearModel request) {
        try {
            CrearModel nuevoUsuario = crearService.registrarUsuario(request);
            
            // Enviar datos del usuario al microservicio de compras
            compraClient.enviarUsuarioParaCompra(nuevoUsuario)
                .subscribe(resultado -> {
                    System.out.println("Usuario enviado al microservicio de compras: " + resultado);
                }, error -> {
                    System.err.println("Error al enviar usuario a compras: " + error.getMessage());
                });
            
            // Enviar datos del usuario al microservicio de generar reportes
            generarReportesClient.enviarUsuarioParaReporte(nuevoUsuario)
                .subscribe(resultado -> {
                    System.out.println("Usuario enviado al microservicio de generar reportes: " + resultado);
                }, error -> {
                    System.err.println("Error al enviar usuario a generar reportes: " + error.getMessage());
                });
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado con éxito: " + nuevoUsuario.getCorreoUsuario());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al registrar usuario: " + e.getMessage());
        }
    }

    // PUT - Actualizar usuario por ID
    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> actualizarCrear(@PathVariable Long idUsuario, @RequestBody @Valid CrearModel crearActualizado) {
        try {
            CrearModel actualizado = crearService.actualizarCrear(idUsuario, crearActualizado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la cuenta: " + e.getMessage());
        }
    }

    // DELETE - Eliminar usuario por ID
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<String> eliminarCrear(@PathVariable Long idUsuario) {
        try {
            String mensaje = crearService.borrarCrear(idUsuario);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar la cuenta: " + e.getMessage());
        }
    }
}
