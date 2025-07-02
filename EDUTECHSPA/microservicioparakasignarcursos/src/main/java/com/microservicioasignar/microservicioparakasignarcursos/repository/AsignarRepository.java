package com.microservicioasignar.microservicioparakasignarcursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;

@Repository
public interface AsignarRepository extends JpaRepository<AsignarModel, Long> {

}
