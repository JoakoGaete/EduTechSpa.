package com.microservicioinicio.microservicioparainiciarcuentas.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservicioinicio.microservicioparainiciarcuentas.client.CrearClient;
import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;
import com.microservicioinicio.microservicioparainiciarcuentas.repository.InicioRepository;


@Service
public class InicioService {
    //GET PUT POST DELETE
    @Autowired 
    private InicioRepository inicioRepository;

        
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CrearClient crearClient;

    // public boolean InicioModel(String correo, String passwordPlana){
    //     Optional<?> optionalCrear = crearRepository.findByCorreo(correo);
    //     if (optionalCrear.isEmpty()){
    //         return false;
    //     }

    //     boolean coinciden = passwordEncoder.matches(passwordPlana, crear.getPassword()); 
    //     return coinciden;
    // }

    //GET
    public List<InicioModel> obtenerListadoInicios(){
        return inicioRepository.findAll();
    }
    //GET
    public InicioModel buscarInicioporId(Long id){
        return inicioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    // //POST
    // public InicioModel guardarInicio(InicioModel inicioNuevo){
    //     return inicioRepository.save(inicioNuevo);
    // }

    public String verificarLoginUsuario(InicioModel informacionUsuario) {
        Map<String, Object> datos = crearClient.verificarLogin(informacionUsuario);

        return "Se inicio sesión correctamente.";
    }



    //PUT
    public InicioModel actualizarInicio(Long id, InicioModel inicioActualizado){
        InicioModel inicioActual = buscarInicioporId(id);
        inicioActual.setId(id);
        
        return inicioRepository.save(inicioActual);
    }
    //DELETE
    public String borrarInicio(Long id){
        InicioModel inicio = buscarInicioporId(id);
        inicioRepository.deleteById(inicio.getId());
        return "Usuario borrado";
    }
}