package com.microserviciocursos.microservicioparajcrearcursos.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;
import com.microserviciocursos.microservicioparajcrearcursos.repository.CursosRepository;

@Service
public class CursosService {

    @Autowired 
    private CursosRepository cursosRepository;

    // GET - Listar todos los cursos
    public List<CursosModel> obtenerListadoCursos() {
        return cursosRepository.findAll();
    }

    // GET - Buscar curso por ID
    public CursosModel buscarCursoPorId(Long idCurso) {
        return cursosRepository.findById(idCurso)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    // POST - Guardar nuevo curso
    public CursosModel guardarCurso(CursosModel cursoNuevo) {
        return cursosRepository.save(cursoNuevo);
    }

    // PUT - Actualizar curso existente
    public CursosModel actualizarCurso(Long idCurso, CursosModel cursoActualizado) {
        CursosModel cursoActual = buscarCursoPorId(idCurso);

        if (cursoActualizado.getNombreCurso() != null) {
            cursoActual.setNombreCurso(cursoActualizado.getNombreCurso());
        }

        if (cursoActualizado.getCantidadUsuarios() != 0) {
            cursoActual.setCantidadUsuarios(cursoActualizado.getCantidadUsuarios());
        }

        if (cursoActualizado.getEstadoCurso() != null) {
            cursoActual.setEstadoCurso(cursoActualizado.getEstadoCurso());
        }

        if (cursoActualizado.getPrecioCurso() != null) {
            cursoActual.setPrecioCurso(cursoActualizado.getPrecioCurso());
        }
        return cursosRepository.save(cursoActual);
    }

    // DELETE - Borrar curso
    public String borrarCurso(Long idCurso) {
        CursosModel curso = buscarCursoPorId(idCurso);
        cursosRepository.deleteById(curso.getIdCurso());
        return "Curso borrado";
    }
}

