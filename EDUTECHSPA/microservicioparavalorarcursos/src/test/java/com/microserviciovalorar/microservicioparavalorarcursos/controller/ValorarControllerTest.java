package com.microserviciovalorar.microservicioparavalorarcursos.controller;

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
import com.microserviciovalorar.microservicioparavalorarcursos.assembler.ValorarModelAssembler;
import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;
import com.microserviciovalorar.microservicioparavalorarcursos.model.CompraDTO;
import com.microserviciovalorar.microservicioparavalorarcursos.service.ValorarService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ValorarController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class ValorarControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private ValorarService valorarService;
    
    @MockBean
    private ValorarModelAssembler assembler;
    
    private ObjectMapper objectMapper;
    private ValorarModel valoracion1;
    private ValorarModel valoracion2;
    private CompraDTO compraDTO;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        valoracion1 = new ValorarModel();
        valoracion1.setIdValoracion(1L);
        valoracion1.setReseñaUsuario("Excelente curso, muy bien explicado");
        valoracion1.setIdCompra(1L);
        valoracion1.setEstadoCompra("Comprado");
        valoracion1.setIdCurso(1L);
        valoracion1.setNombreCurso("Java Básico");
        valoracion1.setIdAsignacion(1L);
        valoracion1.setNombreProfesor("Juan");
        valoracion1.setApellidoProfesor("Perez");
        
        valoracion2 = new ValorarModel();
        valoracion2.setIdValoracion(2L);
        valoracion2.setReseñaUsuario("Muy bueno, aprendí mucho");
        valoracion2.setIdCompra(2L);
        valoracion2.setEstadoCompra("Comprado");
        valoracion2.setIdCurso(2L);
        valoracion2.setNombreCurso("Python Avanzado");
        valoracion2.setIdAsignacion(2L);
        valoracion2.setNombreProfesor("Maria");
        valoracion2.setApellidoProfesor("Garcia");
        
        compraDTO = new CompraDTO();
        compraDTO.setIdCompra(1L);
        compraDTO.setNombreCurso("Java Básico");
        compraDTO.setNombreProfesor("Juan");
        compraDTO.setApellidoProfesor("Perez");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_guardar_valoracion_exitoso() throws Exception {
        when(valorarService.guardarValoracion(any(ValorarModel.class))).thenReturn(valoracion1);

        mockMvc.perform(post("/api/valorar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracion1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idValoracion").value(1))
                .andExpect(jsonPath("$.reseñaUsuario").value("Excelente curso, muy bien explicado"))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"));
    }
    
    @Test
    void test_guardar_valoracion_error() throws Exception {
        when(valorarService.guardarValoracion(any(ValorarModel.class)))
            .thenThrow(new RuntimeException("Error al guardar la valoración"));

        mockMvc.perform(post("/api/valorar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracion1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al guardar la valoración"));
    }
    
    @Test
    void test_realizar_valoracion_manual_exitoso() throws Exception {
        when(valorarService.guardarValoracion(any(ValorarModel.class))).thenReturn(valoracion1);

        mockMvc.perform(post("/api/valorar/realizar-valoracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracion1)))
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Valoración realizada exitosamente")));
    }
    
    @Test
    void test_realizar_valoracion_sin_datos_requeridos() throws Exception {
        ValorarModel valoracionIncompleta = new ValorarModel();
        valoracionIncompleta.setNombreCurso("Java Básico");
        valoracionIncompleta.setNombreProfesor("Juan");
        // Sin idCompra ni reseñaUsuario

        mockMvc.perform(post("/api/valorar/realizar-valoracion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracionIncompleta)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Se requieren ID de compra y reseña para realizar la valoración"));
    }
    
    @Test
    void test_recibir_compra() throws Exception {
        mockMvc.perform(post("/api/valorar/recibir-compra")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compraDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Compra del curso 'Java Básico' con profesor Juan Perez")));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todas_las_valoraciones_exitoso() throws Exception {
        List<ValorarModel> valoraciones = Arrays.asList(valoracion1, valoracion2);
        when(valorarService.obtenerListadoValoraciones()).thenReturn(valoraciones);

        mockMvc.perform(get("/api/valorar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idValoracion").value(1))
                .andExpect(jsonPath("$[0].reseñaUsuario").value("Excelente curso, muy bien explicado"))
                .andExpect(jsonPath("$[1].idValoracion").value(2))
                .andExpect(jsonPath("$[1].reseñaUsuario").value("Muy bueno, aprendí mucho"));
    }
    
    @Test
    void test_obtener_todas_las_valoraciones_vacio() throws Exception {
        when(valorarService.obtenerListadoValoraciones()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/valorar"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_valoracion_por_id_exitoso() throws Exception {
        when(valorarService.buscarValoracionPorId(1L)).thenReturn(valoracion1);

        mockMvc.perform(get("/api/valorar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idValoracion").value(1))
                .andExpect(jsonPath("$.reseñaUsuario").value("Excelente curso, muy bien explicado"))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"))
                .andExpect(jsonPath("$.nombreProfesor").value("Juan"));
    }
    
    @Test
    void test_obtener_valoracion_por_id_no_encontrado() throws Exception {
        when(valorarService.buscarValoracionPorId(999L))
            .thenThrow(new RuntimeException("Valoración no encontrada"));

        mockMvc.perform(get("/api/valorar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Valoración no encontrada"));
    }

    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_valoracion_exitoso() throws Exception {
        ValorarModel valoracionActualizada = new ValorarModel();
        valoracionActualizada.setReseñaUsuario("Curso muy bueno, lo recomiendo");
        
        ValorarModel valoracionRespuesta = new ValorarModel();
        valoracionRespuesta.setIdValoracion(1L);
        valoracionRespuesta.setReseñaUsuario("Curso muy bueno, lo recomiendo");
        valoracionRespuesta.setNombreCurso("Java Básico");
        valoracionRespuesta.setNombreProfesor("Juan");
        
        when(valorarService.actualizarValoracion(1L, valoracionActualizada)).thenReturn(valoracionRespuesta);

        mockMvc.perform(put("/api/valorar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracionActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idValoracion").value(1))
                .andExpect(jsonPath("$.reseñaUsuario").value("Curso muy bueno, lo recomiendo"));
    }
    
    @Test
    void test_actualizar_valoracion_no_encontrado() throws Exception {
        ValorarModel valoracionActualizada = new ValorarModel();
        valoracionActualizada.setReseñaUsuario("Nueva reseña");
        
        when(valorarService.actualizarValoracion(999L, valoracionActualizada))
            .thenThrow(new RuntimeException("Valoración no encontrada"));

        mockMvc.perform(put("/api/valorar/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(valoracionActualizada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar la valoración: Valoración no encontrada"));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_valoracion_exitoso() throws Exception {
        when(valorarService.borrarValoracion(1L)).thenReturn("Valoración borrada");

        mockMvc.perform(delete("/api/valorar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Valoración eliminada correctamente"));
    }
    
    @Test
    void test_eliminar_valoracion_no_encontrado() throws Exception {
        doThrow(new RuntimeException("Valoración no encontrada")).when(valorarService).borrarValoracion(999L);

        mockMvc.perform(delete("/api/valorar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar la valoración: Valoración no encontrada"));
    }
} 