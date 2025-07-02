package com.microserviciogenerar.microservicioparaxgenerarreportes.model;

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
@Table(name = "tabla_reportes")
public class GenerarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReporte;
    private String descripcionReporte;
    private String fechaReporte;

    //DATOS SACADOS DEL MICROSERVICIO CREAR CUENTAS
    private Long idUsuario; 
    private String nombreUsuario;
    private String correoUsuario;
    private String rutUsuario;
}
