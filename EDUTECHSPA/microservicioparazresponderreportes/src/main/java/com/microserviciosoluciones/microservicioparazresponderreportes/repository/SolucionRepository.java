package com.microserviciosoluciones.microservicioparazresponderreportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviciosoluciones.microservicioparazresponderreportes.model.SolucionModel;

@Repository
public interface SolucionRepository extends JpaRepository<SolucionModel,Long> {

}
