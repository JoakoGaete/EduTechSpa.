package com.microservicioasignar.microservicioparakasignarcursos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicioasignar.microservicioparakasignarcursos.model.ProfesorModel;
import com.microservicioasignar.microservicioparakasignarcursos.repository.ProfesorRepository;

@Service
public class ProfesorService {
    
    @Autowired
    private ProfesorRepository profesorRepository;
    
    public List<ProfesorModel> obtenerListadoProfesores() {
        return profesorRepository.findAll();
    }
    
    public ProfesorModel buscarProfesorPorId(Long id) {
        return profesorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profesor no encontrado con ID: " + id));
    }
    
    public ProfesorModel guardarProfesor(ProfesorModel profesor) {
        return profesorRepository.save(profesor);
    }
} 