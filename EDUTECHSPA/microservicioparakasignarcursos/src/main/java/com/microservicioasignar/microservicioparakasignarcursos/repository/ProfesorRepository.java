package com.microservicioasignar.microservicioparakasignarcursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicioasignar.microservicioparakasignarcursos.model.ProfesorModel;

@Repository
public interface ProfesorRepository extends JpaRepository<ProfesorModel, Long> {
} 