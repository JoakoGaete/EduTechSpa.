package com.microserviciogenerar.microservicioparaxgenerarreportes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;
import com.microserviciogenerar.microservicioparaxgenerarreportes.repository.GenerarRepository;

@Service
public class GenerarService {

    @Autowired
    private GenerarRepository generarRepository;

    // GET - Listar todos los reportes
    public List<GenerarModel> obtenerListadoReportes() {
        return generarRepository.findAll();
    }

    // GET - Buscar reporte por ID
    public GenerarModel buscarReportePorId(Long idReporte) {
        return generarRepository.findById(idReporte)
            .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    // POST - Guardar nuevo reporte
    public GenerarModel guardarReporte(GenerarModel nuevoReporte) {
        return generarRepository.save(nuevoReporte);
    }

    // PUT - Actualizar reporte existente
    public GenerarModel actualizarReporte(Long idReporte, GenerarModel reporteActualizado) {
        GenerarModel reporteActual = buscarReportePorId(idReporte);

        if (reporteActualizado.getDescripcionReporte() != null) {
            reporteActual.setDescripcionReporte(reporteActualizado.getDescripcionReporte());
        }
        if (reporteActualizado.getFechaReporte() != null) {
            reporteActual.setFechaReporte(reporteActualizado.getFechaReporte());
        }

        return generarRepository.save(reporteActual);
    }

    // DELETE - Borrar reporte
    public String borrarReporte(Long idReporte) {
        GenerarModel reporte = buscarReportePorId(idReporte);
        generarRepository.deleteById(reporte.getIdReporte());
        return "Reporte borrado";
    }
}
