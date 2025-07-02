package com.microservicioinicio.microservicioparainiciarcuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;

@Repository
public interface InicioRepository extends JpaRepository<InicioModel, Long>{

}
