package com.microserviciosoluciones.microservicioparazresponderreportes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microserviciosoluciones.microservicioparazresponderreportes.model.SolucionModel;
import com.microserviciosoluciones.microservicioparazresponderreportes.repository.SolucionRepository;

@Service
public class SolucionService {

    @Autowired
    private SolucionRepository solucionRepository;

    // GET - Listar todas las soluciones
    public List<SolucionModel> obtenerListadoSoluciones() {
        return solucionRepository.findAll();
    }

    // GET - Buscar solución por ID
    public SolucionModel buscarSolucionPorId(Long idSolucion) {
        return solucionRepository.findById(idSolucion)
            .orElseThrow(() -> new RuntimeException("Solución no encontrada"));
    }

    // POST - Guardar nueva solución
    public SolucionModel guardarSolucion(SolucionModel nuevaSolucion) {
        return solucionRepository.save(nuevaSolucion);
    }

    // PUT - Actualizar solución existente
    public SolucionModel actualizarSolucion(Long idSolucion, SolucionModel solucionActualizada) {
        SolucionModel solucionActual = buscarSolucionPorId(idSolucion);

        if (solucionActualizada.getRutSoporte() != null) {
            solucionActual.setRutSoporte(solucionActualizada.getRutSoporte());
        }
        if (solucionActualizada.getNombreSoporte() != null) {
            solucionActual.setNombreSoporte(solucionActualizada.getNombreSoporte());
        }
        if (solucionActualizada.getApellidoSoporte() != null) {
            solucionActual.setApellidoSoporte(solucionActualizada.getApellidoSoporte());
        }
        if (solucionActualizada.getSolucionReporte() != null) {
            solucionActual.setSolucionReporte(solucionActualizada.getSolucionReporte());
        }
        if (solucionActualizada.getFechaSolucion() != null) {
            solucionActual.setFechaSolucion(solucionActualizada.getFechaSolucion());
        }

        return solucionRepository.save(solucionActual);
    }

    // DELETE - Borrar solución
    public String borrarSolucion(Long idSolucion) {
        SolucionModel solucion = buscarSolucionPorId(idSolucion);
        solucionRepository.deleteById(solucion.getIdSolucion());
        return "Solución borrada";
    }
}