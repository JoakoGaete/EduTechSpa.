package com.microserviciocursos.microservicioparajcrearcursos.controller;

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
import com.microserviciocursos.microservicioparajcrearcursos.assembler.CursosModelAssembler;
import com.microserviciocursos.microservicioparajcrearcursos.client.AsignarClient;
import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;
import com.microserviciocursos.microservicioparajcrearcursos.service.CursosService;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(CursosController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class CursosControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private CursosService service;
    
    @MockBean
    private CursosModelAssembler assembler;
    
    @MockBean
    private AsignarClient asignarClient;
    
    private ObjectMapper objectMapper;
    private CursosModel curso1;
    private CursosModel curso2;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        curso1 = new CursosModel();
        curso1.setIdCurso(1L);
        curso1.setNombreCurso("Java Básico");
        curso1.setPrecioCurso("50000");
        curso1.setCantidadUsuarios(25);
        curso1.setEstadoCurso("Activo");
        
        curso2 = new CursosModel();
        curso2.setIdCurso(2L);
        curso2.setNombreCurso("Python Avanzado");
        curso2.setPrecioCurso("75000");
        curso2.setCantidadUsuarios(30);
        curso2.setEstadoCurso("Activo");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_guardar_curso_exitoso() throws Exception {
        when(service.guardarCurso(any(CursosModel.class))).thenReturn(curso1);
        when(asignarClient.asignarProfesorAutomaticamente(any(CursosModel.class))).thenReturn(Mono.just("OK"));

        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(curso1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"));
    }
    
    @Test
    void test_guardar_curso_error() throws Exception {
        when(service.guardarCurso(any(CursosModel.class)))
            .thenThrow(new RuntimeException("Error al guardar el curso"));

        mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(curso1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al guardar el curso"));
    }
    
    @Test
    void test_guardar_curso_con_asignacion_exitoso() throws Exception {
        when(service.guardarCurso(any(CursosModel.class))).thenReturn(curso1);
        when(asignarClient.asignarProfesorAutomaticamente(any(CursosModel.class))).thenReturn(Mono.just("OK"));

        mockMvc.perform(post("/api/cursos/con-asignacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(curso1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todos_los_cursos_exitoso() throws Exception {
        List<CursosModel> cursos = Arrays.asList(curso1, curso2);
        when(service.obtenerListadoCursos()).thenReturn(cursos);

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCurso").value(1))
                .andExpect(jsonPath("$[0].nombreCurso").value("Java Básico"))
                .andExpect(jsonPath("$[1].idCurso").value(2))
                .andExpect(jsonPath("$[1].nombreCurso").value("Python Avanzado"));
    }
    
    @Test
    void test_obtener_todos_los_cursos_vacio() throws Exception {
        when(service.obtenerListadoCursos()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_curso_por_id_exitoso() throws Exception {
        when(service.buscarCursoPorId(1L)).thenReturn(curso1);

        mockMvc.perform(get("/api/cursos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"))
                .andExpect(jsonPath("$.precioCurso").value("50000"));
    }
    
    @Test
    void test_obtener_curso_por_id_no_encontrado() throws Exception {
        when(service.buscarCursoPorId(999L))
            .thenThrow(new RuntimeException("Curso no encontrado"));

        mockMvc.perform(get("/api/cursos/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Curso no encontrado"));
    }

    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_curso_exitoso() throws Exception {
        CursosModel cursoActualizado = new CursosModel();
        cursoActualizado.setNombreCurso("Java Avanzado");
        cursoActualizado.setPrecioCurso("60000");
        
        CursosModel cursoRespuesta = new CursosModel();
        cursoRespuesta.setIdCurso(1L);
        cursoRespuesta.setNombreCurso("Java Avanzado");
        cursoRespuesta.setPrecioCurso("60000");
        
        when(service.actualizarCurso(1L, cursoActualizado)).thenReturn(cursoRespuesta);

        mockMvc.perform(put("/api/cursos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCurso").value(1))
                .andExpect(jsonPath("$.nombreCurso").value("Java Avanzado"))
                .andExpect(jsonPath("$.precioCurso").value("60000"));
    }
    
    @Test
    void test_actualizar_curso_no_encontrado() throws Exception {
        CursosModel cursoActualizado = new CursosModel();
        cursoActualizado.setNombreCurso("Java Avanzado");
        
        when(service.actualizarCurso(999L, cursoActualizado))
            .thenThrow(new RuntimeException("Curso no encontrado"));

        mockMvc.perform(put("/api/cursos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoActualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar el curso: Curso no encontrado"));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_curso_exitoso() throws Exception {
        when(service.borrarCurso(1L)).thenReturn("Curso borrado");

        mockMvc.perform(delete("/api/cursos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Curso eliminado correctamente"));
    }
    
    @Test
    void test_eliminar_curso_no_encontrado() throws Exception {
        doThrow(new RuntimeException("Curso no encontrado")).when(service).borrarCurso(999L);

        mockMvc.perform(delete("/api/cursos/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar el curso: Curso no encontrado"));
    }
} 