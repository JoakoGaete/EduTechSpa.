package com.microserviciovalorar.microservicioparavalorarcursos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;
import com.microserviciovalorar.microservicioparavalorarcursos.repository.ValorarRepository;

@Service
public class ValorarService {

    @Autowired
    private ValorarRepository valorarRepository;

    // GET - Listar todas las valoraciones
    public List<ValorarModel> obtenerListadoValoraciones() {
        return valorarRepository.findAll();
    }

    // GET - Buscar valoración por ID
    public ValorarModel buscarValoracionPorId(Long idValoracion) {
        return valorarRepository.findById(idValoracion)
            .orElseThrow(() -> new RuntimeException("Valoración no encontrada"));
    }

    // POST - Guardar nueva valoración
    public ValorarModel guardarValoracion(ValorarModel nuevaValoracion) {
        return valorarRepository.save(nuevaValoracion);
    }

    // PUT - Actualizar valoración existente
    public ValorarModel actualizarValoracion(Long idValoracion, ValorarModel valoracionActualizada) {
        ValorarModel valoracionActual = buscarValoracionPorId(idValoracion);

        if (valoracionActualizada.getReseñaUsuario() != null) {
            valoracionActual.setReseñaUsuario(valoracionActualizada.getReseñaUsuario());
        }

        return valorarRepository.save(valoracionActual);
    }

    // DELETE - Borrar valoración
    public String borrarValoracion(Long idValoracion) {
        ValorarModel valoracion = buscarValoracionPorId(idValoracion);
        valorarRepository.deleteById(valoracion.getIdValoracion());
        return "Valoración borrada";
    }
}
