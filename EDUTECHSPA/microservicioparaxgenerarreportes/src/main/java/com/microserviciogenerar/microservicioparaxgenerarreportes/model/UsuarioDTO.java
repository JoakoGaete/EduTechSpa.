package com.microserviciogenerar.microservicioparaxgenerarreportes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String rutUsuario;
    private String correoUsuario;
    private String password;
    private String rol;
    private String tipoPago;
} 