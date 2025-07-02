package com.microservicioasignar.microservicioparakasignarcursos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;
import com.microservicioasignar.microservicioparakasignarcursos.repository.AsignarRepository;

@Service
public class AsignarService {
    @Autowired
    private AsignarRepository asignarRepository;

    // GET - Listar todas las asignaciones
    public List<AsignarModel> obtenerListadoAsignar() {
        return asignarRepository.findAll();
    }

    // GET - Buscar asignación por ID
    public AsignarModel buscarAsignarPorId(Long idAsignacion) {
        return asignarRepository.findById(idAsignacion)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
    }

    // POST - Guardar nueva asignación
    public AsignarModel guardarAsignar(AsignarModel asignarNuevo) {
        return asignarRepository.save(asignarNuevo);
    }

    // PUT - Actualizar asignación existente
    public AsignarModel actualizarAsignar(Long idAsignacion, AsignarModel asignarActualizado) {
        AsignarModel asignarActual = buscarAsignarPorId(idAsignacion);

        if (asignarActualizado.getNombreProfesor() != null) {
            asignarActual.setNombreProfesor(asignarActualizado.getNombreProfesor());
        }

        if (asignarActualizado.getApellidoProfesor() != null) {
            asignarActual.setApellidoProfesor(asignarActualizado.getApellidoProfesor());
        }

        return asignarRepository.save(asignarActual);
    }

    // DELETE - Borrar asignación
    public String borrarAsignar(Long idAsignacion) {
        AsignarModel asignar = buscarAsignarPorId(idAsignacion);
        asignarRepository.deleteById(asignar.getIdAsignacion());
        return "Asignación borrada";
    }
}
