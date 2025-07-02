package com.microserviciosoluciones.microservicioparazresponderreportes.model;

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
@Table(name = "tabla_solucion_reportes")
public class SolucionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolucion;
    private String rutSoporte;
    private String nombreSoporte;
    private String apellidoSoporte;
    private String solucionReporte;
    private String fechaSolucion;

    //DATOS SACADOS DEL MICROSERVICIO GENERAR REPORTES 
    private Long idReporte;
    private String descripcionReporte;
    private String fechaReporte;
    private Long idUsuario;
    private String rutUsuario;
    private String nombreUsuario;


}
