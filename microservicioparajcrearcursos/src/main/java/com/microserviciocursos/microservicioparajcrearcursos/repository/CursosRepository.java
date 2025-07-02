package com.microserviciocursos.microservicioparajcrearcursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;

@Repository
public interface CursosRepository extends JpaRepository<CursosModel,Long> {

}
