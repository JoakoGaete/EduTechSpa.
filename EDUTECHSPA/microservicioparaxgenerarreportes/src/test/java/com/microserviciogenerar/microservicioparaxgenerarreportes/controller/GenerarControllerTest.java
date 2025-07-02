package com.microserviciogenerar.microservicioparaxgenerarreportes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviciogenerar.microservicioparaxgenerarreportes.assembler.GenerarModelAssembler;
import com.microserviciogenerar.microservicioparaxgenerarreportes.client.SolucionClient;
import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;
import com.microserviciogenerar.microservicioparaxgenerarreportes.model.UsuarioDTO;
import com.microserviciogenerar.microservicioparaxgenerarreportes.service.GenerarService;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(GenerarController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class GenerarControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private GenerarService generarService;
    
    @MockBean
    private GenerarModelAssembler assembler;
    
    @MockBean
    private SolucionClient solucionClient;
    
    private ObjectMapper objectMapper;
    private GenerarModel reporte1;
    private GenerarModel reporte2;
    private UsuarioDTO usuarioDTO;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        reporte1 = new GenerarModel();
        reporte1.setIdReporte(1L);
        reporte1.setDescripcionReporte("Problema con el acceso al curso");
        reporte1.setFechaReporte("2024-01-15 10:30:00");
        reporte1.setIdUsuario(1L);
        reporte1.setNombreUsuario("Juan");
        reporte1.setCorreoUsuario("juan@gmail.com");
        reporte1.setRutUsuario("12345678-9");
        
        reporte2 = new GenerarModel();
        reporte2.setIdReporte(2L);
        reporte2.setDescripcionReporte("Error en la plataforma de pagos");
        reporte2.setFechaReporte("2024-01-16 14:20:00");
        reporte2.setIdUsuario(2L);
        reporte2.setNombreUsuario("Maria");
        reporte2.setCorreoUsuario("maria@gmail.com");
        reporte2.setRutUsuario("87654321-0");
        
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1L);
        usuarioDTO.setNombreUsuario("Juan");
        usuarioDTO.setApellidoUsuario("Perez");
        usuarioDTO.setCorreoUsuario("juan@gmail.com");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_crear_reporte_exitoso() throws Exception {
        when(generarService.guardarReporte(any(GenerarModel.class))).thenReturn(reporte1);

        mockMvc.perform(post("/api/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporte1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReporte").value(1))
                .andExpect(jsonPath("$.descripcionReporte").value("Problema con el acceso al curso"))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"));
    }
    
    @Test
    void test_crear_reporte_error() throws Exception {
        when(generarService.guardarReporte(any(GenerarModel.class)))
            .thenThrow(new RuntimeException("Error al crear el reporte"));

        mockMvc.perform(post("/api/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporte1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear reporte: Error al crear el reporte"));
    }
    
    @Test
    void test_crear_reporte_manual_exitoso() throws Exception {
        when(generarService.guardarReporte(any(GenerarModel.class))).thenReturn(reporte1);
        when(solucionClient.enviarReporteParaSolucion(any(GenerarModel.class))).thenReturn(Mono.just("OK"));

        mockMvc.perform(post("/api/generar/crear-reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporte1)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Reporte creado exitosamente")));
    }
    
    @Test
    void test_crear_reporte_manual_sin_datos_requeridos() throws Exception {
        GenerarModel reporteIncompleto = new GenerarModel();
        reporteIncompleto.setNombreUsuario("Juan");
        reporteIncompleto.setCorreoUsuario("juan@gmail.com");
        // Sin idUsuario ni descripcionReporte

        mockMvc.perform(post("/api/generar/crear-reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteIncompleto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Se requieren ID de usuario y descripción para crear el reporte"));
    }
    
    @Test
    void test_recibir_usuario() throws Exception {
        mockMvc.perform(post("/api/generar/recibir-usuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuario Juan Perez (ID: 1) recibido")));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todos_los_reportes_exitoso() throws Exception {
        List<GenerarModel> reportes = Arrays.asList(reporte1, reporte2);
        when(generarService.obtenerListadoReportes()).thenReturn(reportes);

        mockMvc.perform(get("/api/generar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReporte").value(1))
                .andExpect(jsonPath("$[0].descripcionReporte").value("Problema con el acceso al curso"))
                .andExpect(jsonPath("$[1].idReporte").value(2))
                .andExpect(jsonPath("$[1].descripcionReporte").value("Error en la plataforma de pagos"));
    }
    
    @Test
    void test_obtener_todos_los_reportes_vacio() throws Exception {
        when(generarService.obtenerListadoReportes()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/generar"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_reporte_por_id_exitoso() throws Exception {
        when(generarService.buscarReportePorId(1L)).thenReturn(reporte1);

        mockMvc.perform(get("/api/generar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReporte").value(1))
                .andExpect(jsonPath("$.descripcionReporte").value("Problema con el acceso al curso"))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"))
                .andExpect(jsonPath("$.fechaReporte").value("2024-01-15 10:30:00"));
    }
    
    @Test
    void test_obtener_reporte_por_id_no_encontrado() throws Exception {
        when(generarService.buscarReportePorId(999L))
            .thenThrow(new RuntimeException("Reporte no encontrado"));

        mockMvc.perform(get("/api/generar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: Reporte no encontrado"));
    }

    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_reporte_exitoso() throws Exception {
        GenerarModel reporteActualizado = new GenerarModel();
        reporteActualizado.setDescripcionReporte("Problema resuelto - acceso restaurado");
        
        GenerarModel reporteRespuesta = new GenerarModel();
        reporteRespuesta.setIdReporte(1L);
        reporteRespuesta.setDescripcionReporte("Problema resuelto - acceso restaurado");
        reporteRespuesta.setNombreUsuario("Juan");
        reporteRespuesta.setFechaReporte("2024-01-15 10:30:00");
        
        when(generarService.actualizarReporte(1L, reporteActualizado)).thenReturn(reporteRespuesta);

        mockMvc.perform(put("/api/generar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReporte").value(1))
                .andExpect(jsonPath("$.descripcionReporte").value("Problema resuelto - acceso restaurado"));
    }
    
    @Test
    void test_actualizar_reporte_no_encontrado() throws Exception {
        GenerarModel reporteActualizado = new GenerarModel();
        reporteActualizado.setDescripcionReporte("Nueva descripción");
        
        when(generarService.actualizarReporte(999L, reporteActualizado))
            .thenThrow(new RuntimeException("Reporte no encontrado"));

        mockMvc.perform(put("/api/generar/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteActualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar reporte: Reporte no encontrado"));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_reporte_exitoso() throws Exception {
        when(generarService.borrarReporte(1L)).thenReturn("Reporte eliminado correctamente");

        mockMvc.perform(delete("/api/generar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reporte eliminado correctamente"));
    }
    
    @Test
    void test_eliminar_reporte_no_encontrado() throws Exception {
        when(generarService.borrarReporte(999L))
            .thenThrow(new RuntimeException("Reporte no encontrado"));

        mockMvc.perform(delete("/api/generar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar reporte: Reporte no encontrado"));
    }
} 