package com.microservicioasignar.microservicioparakasignarcursos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {
    private Long idCurso;
    private String nombreCurso;
    private String precioCurso;
    private int cantidadUsuarios;
    private String estadoCurso;
} 