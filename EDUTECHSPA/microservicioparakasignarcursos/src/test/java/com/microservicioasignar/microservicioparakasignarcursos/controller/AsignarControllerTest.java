package com.microservicioasignar.microservicioparakasignarcursos.controller;

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
import com.microservicioasignar.microservicioparakasignarcursos.assembler.AsignarModelAssembler;
import com.microservicioasignar.microservicioparakasignarcursos.client.CompraClient;
import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;
import com.microservicioasignar.microservicioparakasignarcursos.model.CursoDTO;
import com.microservicioasignar.microservicioparakasignarcursos.model.ProfesorModel;
import com.microservicioasignar.microservicioparakasignarcursos.service.AsignarService;
import com.microservicioasignar.microservicioparakasignarcursos.service.ProfesorService;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(AsignarController.class)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class AsignarControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    @MockBean
    private AsignarService asignarService;
    
    @MockBean
    private ProfesorService profesorService;
    
    @MockBean
    private AsignarModelAssembler assembler;
    
    @MockBean
    private CompraClient compraClient;
    
    private ObjectMapper objectMapper;
    private AsignarModel asignacion1;
    private AsignarModel asignacion2;
    private ProfesorModel profesor1;
    private ProfesorModel profesor2;
    private CursoDTO cursoDTO;

    @BeforeEach 
    void setUp() {
        objectMapper = new ObjectMapper();
        
        profesor1 = new ProfesorModel();
        profesor1.setIdProfesor(1L);
        profesor1.setNombreProfesor("Juan");
        profesor1.setApellidoProfesor("Perez");
        
        profesor2 = new ProfesorModel();
        profesor2.setIdProfesor(2L);
        profesor2.setNombreProfesor("Maria");
        profesor2.setApellidoProfesor("Garcia");
        
        asignacion1 = new AsignarModel();
        asignacion1.setIdAsignacion(1L);
        asignacion1.setNombreProfesor("Juan");
        asignacion1.setApellidoProfesor("Perez");
        asignacion1.setIdCurso(1L);
        asignacion1.setNombreCurso("Java Básico");
        asignacion1.setPrecioCurso("50000");
        
        asignacion2 = new AsignarModel();
        asignacion2.setIdAsignacion(2L);
        asignacion2.setNombreProfesor("Maria");
        asignacion2.setApellidoProfesor("Garcia");
        asignacion2.setIdCurso(2L);
        asignacion2.setNombreCurso("Python Avanzado");
        asignacion2.setPrecioCurso("75000");
        
        cursoDTO = new CursoDTO();
        cursoDTO.setIdCurso(1L);
        cursoDTO.setNombreCurso("Java Básico");
    }

    // ========== CREATE (POST) TESTS ==========
    
    @Test
    void test_guardar_asignacion_exitoso() throws Exception {
        when(asignarService.guardarAsignar(any(AsignarModel.class))).thenReturn(asignacion1);

        mockMvc.perform(post("/api/asignar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacion1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAsignacion").value(1))
                .andExpect(jsonPath("$.nombreProfesor").value("Juan"))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"));
    }
    
    @Test
    void test_guardar_asignacion_error() throws Exception {
        when(asignarService.guardarAsignar(any(AsignarModel.class)))
            .thenThrow(new RuntimeException("Error al guardar la asignación"));

        mockMvc.perform(post("/api/asignar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacion1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al guardar la asignación"));
    }
    
    @Test
    void test_asignacion_manual_exitoso() throws Exception {
        List<ProfesorModel> profesores = Arrays.asList(profesor1, profesor2);
        when(profesorService.obtenerListadoProfesores()).thenReturn(profesores);
        when(asignarService.guardarAsignar(any(AsignarModel.class))).thenReturn(asignacion1);
        when(compraClient.enviarAsignacionParaCompra(any(AsignarModel.class))).thenReturn(Mono.just("OK"));

        mockMvc.perform(post("/api/asignar/asignacion-manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacion1)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Profesor Juan Perez asignado manualmente")));
    }
    
    @Test
    void test_asignacion_manual_profesor_no_existe() throws Exception {
        List<ProfesorModel> profesores = Arrays.asList(profesor1, profesor2);
        when(profesorService.obtenerListadoProfesores()).thenReturn(profesores);

        AsignarModel asignacionInexistente = new AsignarModel();
        asignacionInexistente.setNombreProfesor("Profesor");
        asignacionInexistente.setApellidoProfesor("Inexistente");
        asignacionInexistente.setNombreCurso("Curso Test");

        mockMvc.perform(post("/api/asignar/asignacion-manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionInexistente)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El profesor especificado no existe en la base de datos"));
    }
    
    @Test
    void test_recibir_curso_para_asignacion() throws Exception {
        mockMvc.perform(post("/api/asignar/asignacion-automatica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Curso 'Java Básico' (ID: 1) recibido")));
    }

    // ========== READ (GET) TESTS ==========
    
    @Test
    void test_obtener_todas_las_asignaciones_exitoso() throws Exception {
        List<AsignarModel> asignaciones = Arrays.asList(asignacion1, asignacion2);
        when(asignarService.obtenerListadoAsignar()).thenReturn(asignaciones);

        mockMvc.perform(get("/api/asignar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAsignacion").value(1))
                .andExpect(jsonPath("$[0].nombreProfesor").value("Juan"))
                .andExpect(jsonPath("$[1].idAsignacion").value(2))
                .andExpect(jsonPath("$[1].nombreProfesor").value("Maria"));
    }
    
    @Test
    void test_obtener_todas_las_asignaciones_vacio() throws Exception {
        when(asignarService.obtenerListadoAsignar()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/asignar"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_asignacion_por_id_exitoso() throws Exception {
        when(asignarService.buscarAsignarPorId(1L)).thenReturn(asignacion1);

        mockMvc.perform(get("/api/asignar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAsignacion").value(1))
                .andExpect(jsonPath("$.nombreProfesor").value("Juan"))
                .andExpect(jsonPath("$.nombreCurso").value("Java Básico"));
    }
    
    @Test
    void test_obtener_asignacion_por_id_no_encontrado() throws Exception {
        when(asignarService.buscarAsignarPorId(999L))
            .thenThrow(new RuntimeException("Asignación no encontrada"));

        mockMvc.perform(get("/api/asignar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Asignación no encontrada"));
    }
    
    @Test
    void test_obtener_profesores_exitoso() throws Exception {
        List<ProfesorModel> profesores = Arrays.asList(profesor1, profesor2);
        when(profesorService.obtenerListadoProfesores()).thenReturn(profesores);

        mockMvc.perform(get("/api/asignar/profesores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProfesor").value(1))
                .andExpect(jsonPath("$[0].nombreProfesor").value("Juan"))
                .andExpect(jsonPath("$[1].idProfesor").value(2))
                .andExpect(jsonPath("$[1].nombreProfesor").value("Maria"));
    }
    
    @Test
    void test_obtener_profesores_vacio() throws Exception {
        when(profesorService.obtenerListadoProfesores()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/asignar/profesores"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void test_obtener_cursos_pendientes() throws Exception {
        mockMvc.perform(get("/api/asignar/cursos-pendientes"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Para ver cursos pendientes")));
    }

    // ========== UPDATE (PUT) TESTS ==========
    
    @Test
    void test_actualizar_asignacion_exitoso() throws Exception {
        AsignarModel asignacionActualizada = new AsignarModel();
        asignacionActualizada.setNombreProfesor("Juan Carlos");
        asignacionActualizada.setApellidoProfesor("Perez Gonzalez");
        
        AsignarModel asignacionRespuesta = new AsignarModel();
        asignacionRespuesta.setIdAsignacion(1L);
        asignacionRespuesta.setNombreProfesor("Juan Carlos");
        asignacionRespuesta.setApellidoProfesor("Perez Gonzalez");
        asignacionRespuesta.setNombreCurso("Java Básico");
        
        when(asignarService.actualizarAsignar(1L, asignacionActualizada)).thenReturn(asignacionRespuesta);

        mockMvc.perform(put("/api/asignar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAsignacion").value(1))
                .andExpect(jsonPath("$.nombreProfesor").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellidoProfesor").value("Perez Gonzalez"));
    }
    
    @Test
    void test_actualizar_asignacion_no_encontrado() throws Exception {
        AsignarModel asignacionActualizada = new AsignarModel();
        asignacionActualizada.setNombreProfesor("Juan Carlos");
        
        when(asignarService.actualizarAsignar(999L, asignacionActualizada))
            .thenThrow(new RuntimeException("Asignación no encontrada"));

        mockMvc.perform(put("/api/asignar/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionActualizada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al actualizar la asignación: Asignación no encontrada"));
    }

    // ========== DELETE TESTS ==========
    
    @Test
    void test_eliminar_asignacion_exitoso() throws Exception {
        when(asignarService.borrarAsignar(1L)).thenReturn("Asignación borrada");

        mockMvc.perform(delete("/api/asignar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Asignación eliminada correctamente"));
    }
    
    @Test
    void test_eliminar_asignacion_no_encontrado() throws Exception {
        doThrow(new RuntimeException("Asignación no encontrada")).when(asignarService).borrarAsignar(999L);

        mockMvc.perform(delete("/api/asignar/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error al eliminar la asignación: Asignación no encontrada"));
    }
} 