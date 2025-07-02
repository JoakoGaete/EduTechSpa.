package com.microserviciovalorar.microservicioparavalorarcursos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    private Long idCompra;
    private String estadoCompra;
    private Long idCurso;
    private String nombreCurso;
    private Long idAsignacion;
    private String nombreProfesor;
    private String apellidoProfesor;
    private Long idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String tipoPago;
} 