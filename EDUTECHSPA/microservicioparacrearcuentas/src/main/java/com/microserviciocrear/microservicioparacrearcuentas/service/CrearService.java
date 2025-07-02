package com.microserviciocrear.microservicioparacrearcuentas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;
import com.microserviciocrear.microservicioparacrearcuentas.repository.CrearRepository;

@Service
public class CrearService {

    @Autowired 
    private CrearRepository crearRepository;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    // GET - Listar todos los usuarios
    public List<CrearModel> obtenerListadoCrear() {
        return crearRepository.findAll();
    }

    // GET - Buscar usuario por idUsuario
    public CrearModel buscarCrearPorId(Long idUsuario) {
        return crearRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Cuenta creada no encontrada"));
    }

    // POST - Registrar usuario
    public CrearModel registrarUsuario(CrearModel modeloCrear) {
        if (crearRepository.findByCorreoUsuario(modeloCrear.getCorreoUsuario()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        String passwordEncriptada = passwordEncoder.encode(modeloCrear.getPassword());
        modeloCrear.setPassword(passwordEncriptada);

        return crearRepository.save(modeloCrear);
    }

    // GET - Buscar usuario por correo
    public CrearModel buscarUsuarioPorCorreo(CrearModel datosUsuario) {
        return crearRepository.findByCorreoUsuario(datosUsuario.getCorreoUsuario())
            .orElseThrow(() -> new RuntimeException("Correo no encontrado o no coincide."));
    }

    // PUT - Actualizar datos de usuario
    public CrearModel actualizarCrear(Long idUsuario, CrearModel crearActualizado) {
        CrearModel crearActual = buscarCrearPorId(idUsuario);

        if (crearActualizado.getNombreUsuario() != null) {
            crearActual.setNombreUsuario(crearActualizado.getNombreUsuario());
        }
        if (crearActualizado.getApellidoUsuario() != null) {
            crearActual.setApellidoUsuario(crearActualizado.getApellidoUsuario());
        }
        if (crearActualizado.getRutUsuario() != null) {
            crearActual.setRutUsuario(crearActualizado.getRutUsuario());
        }
        if (crearActualizado.getCorreoUsuario() != null) {
            crearActual.setCorreoUsuario(crearActualizado.getCorreoUsuario());
        }
        if (crearActualizado.getPassword() != null) {
            String passwordEncriptada = passwordEncoder.encode(crearActualizado.getPassword());
            crearActual.setPassword(passwordEncriptada);
        }
        if (crearActualizado.getRol() != null) {
            crearActual.setRol(crearActualizado.getRol());
        }

        return crearRepository.save(crearActual);
    }

    // DELETE - Eliminar cuenta
    public String borrarCrear(Long idUsuario) {
        if (!crearRepository.existsById(idUsuario)) {
            throw new RuntimeException("No se encontró la cuenta con ID: " + idUsuario);
        }

    crearRepository.deleteById(idUsuario);
    return "Cuenta borrada exitosamente con ID: " + idUsuario;
    }

}
