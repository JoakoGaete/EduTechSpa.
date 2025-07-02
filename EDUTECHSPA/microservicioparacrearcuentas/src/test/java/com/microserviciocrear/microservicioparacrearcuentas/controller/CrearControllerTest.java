package com.microserviciocrear.microservicioparacrearcuentas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviciocrear.microservicioparacrearcuentas.assemblers.CrearModelAssembler;
import com.microserviciocrear.microservicioparacrearcuentas.client.CompraClient;
import com.microserviciocrear.microservicioparacrearcuentas.client.GenerarReportesClient;
import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;
import com.microserviciocrear.microservicioparacrearcuentas.service.CrearService;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(CrearController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class CrearControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private CrearService service;
    
    @MockBean
    private CrearModelAssembler assembler;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private CompraClient compraClient;
    
    @MockBean
    private GenerarReportesClient generarReportesClient;
    
    private ObjectMapper objectMapper;
    private CrearModel crearUsuario;
    private CrearModel crearUsuario2;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        crearUsuario = new CrearModel();
        crearUsuario.setIdUsuario(1L);
        crearUsuario.setNombreUsuario("Juan");
        crearUsuario.setApellidoUsuario("Perez");
        crearUsuario.setRutUsuario("12345678-9");
        crearUsuario.setCorreoUsuario("juan.perez@gmail.com");
        crearUsuario.setPassword("password123");
        crearUsuario.setRol("ADMIN");
        crearUsuario.setTipoPago("Debito");
        
        crearUsuario2 = new CrearModel();
        crearUsuario2.setIdUsuario(2L);
        crearUsuario2.setNombreUsuario("Maria");
        crearUsuario2.setApellidoUsuario("Garcia");
        crearUsuario2.setRutUsuario("87654321-0");
        crearUsuario2.setCorreoUsuario("maria.garcia@gmail.com");
        crearUsuario2.setPassword("password456");
        crearUsuario2.setRol("USER");
        crearUsuario2.setTipoPago("Credito");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_registrar_usuario_exitoso() throws Exception {
        when(service.registrarUsuario(any(CrearModel.class))).thenReturn(crearUsuario);
        when(compraClient.enviarUsuarioParaCompra(any(CrearModel.class))).thenReturn(Mono.just("OK"));
        when(generarReportesClient.enviarUsuarioParaReporte(any(CrearModel.class))).thenReturn(Mono.just("OK"));

        mockMvc.perform(post("/api/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crearUsuario)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado con éxito: juan.perez@gmail.com"));
    }
    
    @Test
    void test_registrar_usuario_error() throws Exception {
        when(service.registrarUsuario(any(CrearModel.class)))
            .thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(post("/api/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crearUsuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al registrar usuario: El correo ya está registrado"));
    }
    
    @Test
    void test_login_exitoso() throws Exception {
        CrearModel loginRequest = new CrearModel();
        loginRequest.setCorreoUsuario("juan.perez@gmail.com");
        loginRequest.setPassword("password123");
        
        when(service.buscarUsuarioPorCorreo(any(CrearModel.class))).thenReturn(crearUsuario);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/registro/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("juan.perez@gmail.com"));
    }
    
    @Test
    void test_login_password_incorrecto() throws Exception {
        CrearModel loginRequest = new CrearModel();
        loginRequest.setCorreoUsuario("juan.perez@gmail.com");
        loginRequest.setPassword("password123");
        
        when(service.buscarUsuarioPorCorreo(any(CrearModel.class))).thenReturn(crearUsuario);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/registro/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error de autenticación: Contraseña incorrecta"));
    }
    
    @Test
    void test_login_usuario_no_encontrado() throws Exception {
        CrearModel loginRequest = new CrearModel();
        loginRequest.setCorreoUsuario("usuario.inexistente@gmail.com");
        loginRequest.setPassword("password123");
        
        when(service.buscarUsuarioPorCorreo(any(CrearModel.class)))
            .thenThrow(new RuntimeException("Correo no encontrado o no coincide."));

        mockMvc.perform(post("/api/registro/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error de autenticación: Correo no encontrado o no coincide."));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todos_los_usuarios_exitoso() throws Exception {
        List<CrearModel> usuarios = Arrays.asList(crearUsuario, crearUsuario2);
        when(service.obtenerListadoCrear()).thenReturn(usuarios);

        mockMvc.perform(get("/api/registro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("Juan"))
                .andExpect(jsonPath("$[1].idUsuario").value(2))
                .andExpect(jsonPath("$[1].nombreUsuario").value("Maria"));
    }
    
    @Test
    void test_obtener_todos_los_usuarios_vacio() throws Exception {
        when(service.obtenerListadoCrear()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/registro"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_usuario_por_id_exitoso() throws Exception {
        when(service.buscarCrearPorId(1L)).thenReturn(crearUsuario);

        mockMvc.perform(get("/api/registro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"))
                .andExpect(jsonPath("$.correoUsuario").value("juan.perez@gmail.com"));
    }
    
    @Test
    void test_obtener_usuario_por_id_no_encontrado() throws Exception {
        when(service.buscarCrearPorId(999L))
            .thenThrow(new RuntimeException("Cuenta creada no encontrada"));

        mockMvc.perform(get("/api/registro/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cuenta creada no encontrada"));
    }
    


    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_usuario_exitoso() throws Exception {
        CrearModel usuarioActualizado = new CrearModel();
        usuarioActualizado.setNombreUsuario("Juan Carlos");
        usuarioActualizado.setApellidoUsuario("Perez Gonzalez");
        
        CrearModel usuarioRespuesta = new CrearModel();
        usuarioRespuesta.setIdUsuario(1L);
        usuarioRespuesta.setNombreUsuario("Juan Carlos");
        usuarioRespuesta.setApellidoUsuario("Perez Gonzalez");
        usuarioRespuesta.setCorreoUsuario("juan.perez@gmail.com");
        
        when(service.actualizarCrear(1L, usuarioActualizado)).thenReturn(usuarioRespuesta);

        mockMvc.perform(put("/api/registro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellidoUsuario").value("Perez Gonzalez"));
    }
    
    @Test
    void test_actualizar_usuario_no_encontrado() throws Exception {
        CrearModel usuarioActualizado = new CrearModel();
        usuarioActualizado.setNombreUsuario("Juan Carlos");
        
        when(service.actualizarCrear(999L, usuarioActualizado))
            .thenThrow(new RuntimeException("Cuenta creada no encontrada"));

        mockMvc.perform(put("/api/registro/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar la cuenta: Cuenta creada no encontrada"));
    }
    
    @Test
    void test_actualizar_usuario_con_password() throws Exception {
        CrearModel usuarioActualizado = new CrearModel();
        usuarioActualizado.setPassword("nuevaPassword123");
        
        CrearModel usuarioRespuesta = new CrearModel();
        usuarioRespuesta.setIdUsuario(1L);
        usuarioRespuesta.setPassword("encodedPassword");
        
        when(service.actualizarCrear(1L, usuarioActualizado)).thenReturn(usuarioRespuesta);

        mockMvc.perform(put("/api/registro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_usuario_exitoso() throws Exception {
        when(service.borrarCrear(1L)).thenReturn("Cuenta borrada exitosamente con ID: 1");

        mockMvc.perform(delete("/api/registro/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cuenta borrada exitosamente con ID: 1"));
    }
    
    @Test
    void test_eliminar_usuario_no_encontrado() throws Exception {
        when(service.borrarCrear(999L))
            .thenThrow(new RuntimeException("No se encontró la cuenta con ID: 999"));

        mockMvc.perform(delete("/api/registro/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar la cuenta: No se encontró la cuenta con ID: 999"));
    }
    
    // ========== VALIDATION TESTS ==========
    
    @Test
    void test_registrar_usuario_sin_datos_requeridos() throws Exception {
        CrearModel usuarioIncompleto = new CrearModel();
        // Solo establecemos algunos campos, faltan otros requeridos
        
        mockMvc.perform(post("/api/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioIncompleto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void test_actualizar_usuario_con_datos_invalidos() throws Exception {
        CrearModel usuarioInvalido = new CrearModel();
        usuarioInvalido.setCorreoUsuario("correo-invalido");
        
        // Mock del servicio para simular un error de validación
        when(service.actualizarCrear(1L, usuarioInvalido))
            .thenThrow(new RuntimeException("Datos de usuario inválidos"));
        
        mockMvc.perform(put("/api/registro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar la cuenta: Datos de usuario inválidos"));
    }
}