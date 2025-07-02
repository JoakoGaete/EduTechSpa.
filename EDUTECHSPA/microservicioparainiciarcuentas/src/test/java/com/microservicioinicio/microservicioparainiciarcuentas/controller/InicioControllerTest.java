package com.microservicioinicio.microservicioparainiciarcuentas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import com.microservicioinicio.microservicioparainiciarcuentas.assembler.InicioModelAssembler;
import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;
import com.microservicioinicio.microservicioparainiciarcuentas.service.InicioService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(InicioController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class InicioControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private InicioService inicioService;
    
    @MockBean
    private InicioModelAssembler assembler;
    
    private InicioModel usuario1;
    private InicioModel usuario2;

    @BeforeEach 
    void setUp() {
        usuario1 = new InicioModel();
        usuario1.setId(1L);
        usuario1.setCorreoUsuario("juan@gmail.com");
        usuario1.setPassword("password123");
        
        usuario2 = new InicioModel();
        usuario2.setId(2L);
        usuario2.setCorreoUsuario("maria@gmail.com");
        usuario2.setPassword("password456");
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todos_los_usuarios_exitoso() throws Exception {
        List<InicioModel> usuarios = Arrays.asList(usuario1, usuario2);
        when(inicioService.obtenerListadoInicios()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].correoUsuario").value("juan@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].correoUsuario").value("maria@gmail.com"));
    }
    
    @Test
    void test_obtener_todos_los_usuarios_vacio() throws Exception {
        when(inicioService.obtenerListadoInicios()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_usuario_por_id_exitoso() throws Exception {
        when(inicioService.buscarInicioporId(1L)).thenReturn(usuario1);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("juan@gmail.com"))
                .andExpect(jsonPath("$.password").value("password123"));
    }
    
    @Test
    void test_obtener_usuario_por_id_no_encontrado() throws Exception {
        when(inicioService.buscarInicioporId(999L))
            .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }
} 