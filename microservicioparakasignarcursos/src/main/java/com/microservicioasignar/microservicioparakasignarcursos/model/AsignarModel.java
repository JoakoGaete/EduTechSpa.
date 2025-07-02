package com.microservicioasignar.microservicioparakasignarcursos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tabla_asignaciones")
public class AsignarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsignacion;
    private String nombreProfesor;
    private String apellidoProfesor;
    
    //ESTOS DATOS VIENEN DEL MICROSERVICIO DE CREAR CURSOS
    private Long idCurso;
    private String nombreCurso;
    private String precioCurso;
}
