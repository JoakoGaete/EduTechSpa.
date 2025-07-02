package com.microserviciovalorar.microservicioparavalorarcursos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;

@Repository
public interface ValorarRepository extends JpaRepository<ValorarModel, Long> {

}
