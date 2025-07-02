package com.microserviciosoluciones.microservicioparazresponderreportes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {
    private Long idReporte;
    private String descripcionReporte;
    private String fechaReporte;
    private Long idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private String rutUsuario;
} 