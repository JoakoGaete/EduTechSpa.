package com.microserviciovalorar.microservicioparavalorarcursos.model;

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
@Table(name = "tabla_valoraciones_cursos")
public class ValorarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idValoracion;
    private String reseñaUsuario;

    //DATOS SACADOS DEL MICROSERVICIO DE COMPRA DE CURSOS
    private Long idCompra;
    private String estadoCompra;
    private Long idCurso;
    private String nombreCurso;
    private Long idAsignacion;
    private String nombreProfesor;
    private String apellidoProfesor;
}
