package com.microserviciocrear.microservicioparacrearcuentas.model;

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
@Table(name = "tabla_usuarios")
public class CrearModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  idUsuario;
    
    private String nombreUsuario;
    private String apellidoUsuario;
    private String rutUsuario;
    private String correoUsuario;
    private String password;
    private String rol;
    private String tipoPago;

    public CrearModel(String correoUsuario, String password){
        this.correoUsuario = correoUsuario;
        this.password = password;
    }
}
