package com.microserviciosoluciones.microservicioparazresponderreportes.controller;

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
import com.microserviciosoluciones.microservicioparazresponderreportes.assembler.SolucionModelAssembler;
import com.microserviciosoluciones.microservicioparazresponderreportes.model.SolucionModel;
import com.microserviciosoluciones.microservicioparazresponderreportes.model.ReporteDTO;
import com.microserviciosoluciones.microservicioparazresponderreportes.service.SolucionService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(SolucionController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class SolucionControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private SolucionService solucionService;
    
    @MockBean
    private SolucionModelAssembler assembler;
    
    private ObjectMapper objectMapper;
    private SolucionModel solucion1;
    private SolucionModel solucion2;
    private ReporteDTO reporteDTO;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        solucion1 = new SolucionModel();
        solucion1.setIdSolucion(1L);
        solucion1.setRutSoporte("12345678-9");
        solucion1.setNombreSoporte("Carlos");
        solucion1.setApellidoSoporte("Garcia");
        solucion1.setSolucionReporte("Se ha restaurado el acceso al curso");
        solucion1.setFechaSolucion("2024-01-15 11:30:00");
        solucion1.setIdReporte(1L);
        solucion1.setDescripcionReporte("Problema con el acceso al curso");
        solucion1.setFechaReporte("2024-01-15 10:30:00");
        solucion1.setIdUsuario(1L);
        solucion1.setRutUsuario("12345678-9");
        solucion1.setNombreUsuario("Juan");
        
        solucion2 = new SolucionModel();
        solucion2.setIdSolucion(2L);
        solucion2.setRutSoporte("87654321-0");
        solucion2.setNombreSoporte("Ana");
        solucion2.setApellidoSoporte("Lopez");
        solucion2.setSolucionReporte("Problema de pagos resuelto");
        solucion2.setFechaSolucion("2024-01-16 15:20:00");
        solucion2.setIdReporte(2L);
        solucion2.setDescripcionReporte("Error en la plataforma de pagos");
        solucion2.setFechaReporte("2024-01-16 14:20:00");
        solucion2.setIdUsuario(2L);
        solucion2.setRutUsuario("87654321-0");
        solucion2.setNombreUsuario("Maria");
        
        reporteDTO = new ReporteDTO();
        reporteDTO.setIdReporte(1L);
        reporteDTO.setNombreUsuario("Juan");
        reporteDTO.setDescripcionReporte("Problema con el acceso al curso");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_crear_solucion_exitoso() throws Exception {
        when(solucionService.guardarSolucion(any(SolucionModel.class))).thenReturn(solucion1);

        mockMvc.perform(post("/api/soluciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucion1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSolucion").value(1))
                .andExpect(jsonPath("$.solucionReporte").value("Se ha restaurado el acceso al curso"))
                .andExpect(jsonPath("$.nombreSoporte").value("Carlos"));
    }
    
    @Test
    void test_crear_solucion_error() throws Exception {
        when(solucionService.guardarSolucion(any(SolucionModel.class)))
            .thenThrow(new RuntimeException("Error al crear la solución"));

        mockMvc.perform(post("/api/soluciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucion1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear solución: Error al crear la solución"));
    }
    
    @Test
    void test_crear_solucion_manual_exitoso() throws Exception {
        when(solucionService.guardarSolucion(any(SolucionModel.class))).thenReturn(solucion1);

        mockMvc.perform(post("/api/soluciones/crear-solucion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucion1)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Solución creada exitosamente")));
    }
    
    @Test
    void test_crear_solucion_manual_sin_datos_requeridos() throws Exception {
        SolucionModel solucionIncompleta = new SolucionModel();
        solucionIncompleta.setNombreSoporte("Carlos");
        solucionIncompleta.setApellidoSoporte("Garcia");
        // Sin idReporte ni solucionReporte

        mockMvc.perform(post("/api/soluciones/crear-solucion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucionIncompleta)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Se requieren ID de reporte y solución para crear la respuesta"));
    }
    
    @Test
    void test_recibir_reporte() throws Exception {
        mockMvc.perform(post("/api/soluciones/recibir-reporte")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporteDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Reporte del usuario Juan (ID Reporte: 1) recibido")));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todas_las_soluciones_exitoso() throws Exception {
        List<SolucionModel> soluciones = Arrays.asList(solucion1, solucion2);
        when(solucionService.obtenerListadoSoluciones()).thenReturn(soluciones);

        mockMvc.perform(get("/api/soluciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSolucion").value(1))
                .andExpect(jsonPath("$[0].solucionReporte").value("Se ha restaurado el acceso al curso"))
                .andExpect(jsonPath("$[1].idSolucion").value(2))
                .andExpect(jsonPath("$[1].solucionReporte").value("Problema de pagos resuelto"));
    }
    
    @Test
    void test_obtener_todas_las_soluciones_vacio() throws Exception {
        when(solucionService.obtenerListadoSoluciones()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/soluciones"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_solucion_por_id_exitoso() throws Exception {
        when(solucionService.buscarSolucionPorId(1L)).thenReturn(solucion1);

        mockMvc.perform(get("/api/soluciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSolucion").value(1))
                .andExpect(jsonPath("$.solucionReporte").value("Se ha restaurado el acceso al curso"))
                .andExpect(jsonPath("$.nombreSoporte").value("Carlos"))
                .andExpect(jsonPath("$.nombreUsuario").value("Juan"));
    }
    
    @Test
    void test_obtener_solucion_por_id_no_encontrado() throws Exception {
        when(solucionService.buscarSolucionPorId(999L))
            .thenThrow(new RuntimeException("Solución no encontrada"));

        mockMvc.perform(get("/api/soluciones/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: Solución no encontrada"));
    }

    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_solucion_exitoso() throws Exception {
        SolucionModel solucionActualizada = new SolucionModel();
        solucionActualizada.setSolucionReporte("Problema completamente resuelto");
        
        SolucionModel solucionRespuesta = new SolucionModel();
        solucionRespuesta.setIdSolucion(1L);
        solucionRespuesta.setSolucionReporte("Problema completamente resuelto");
        solucionRespuesta.setNombreSoporte("Carlos");
        solucionRespuesta.setNombreUsuario("Juan");
        
        when(solucionService.actualizarSolucion(1L, solucionActualizada)).thenReturn(solucionRespuesta);

        mockMvc.perform(put("/api/soluciones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucionActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSolucion").value(1))
                .andExpect(jsonPath("$.solucionReporte").value("Problema completamente resuelto"));
    }
    
    @Test
    void test_actualizar_solucion_no_encontrado() throws Exception {
        SolucionModel solucionActualizada = new SolucionModel();
        solucionActualizada.setSolucionReporte("Nueva solución");
        
        when(solucionService.actualizarSolucion(999L, solucionActualizada))
            .thenThrow(new RuntimeException("Solución no encontrada"));

        mockMvc.perform(put("/api/soluciones/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solucionActualizada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar solución: Solución no encontrada"));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_solucion_exitoso() throws Exception {
        when(solucionService.borrarSolucion(1L)).thenReturn("Solución eliminada correctamente");

        mockMvc.perform(delete("/api/soluciones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solución eliminada correctamente"));
    }
    
    @Test
    void test_eliminar_solucion_no_encontrado() throws Exception {
        when(solucionService.borrarSolucion(999L))
            .thenThrow(new RuntimeException("Solución no encontrada"));

        mockMvc.perform(delete("/api/soluciones/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar solución: Solución no encontrada"));
    }
} 