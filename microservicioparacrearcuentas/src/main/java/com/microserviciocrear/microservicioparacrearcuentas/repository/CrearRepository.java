package com.microserviciocrear.microservicioparacrearcuentas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;


@Repository
public interface CrearRepository extends JpaRepository<CrearModel,Long>{
    Optional<CrearModel> findByCorreoUsuario(String correo);
    Optional<CrearModel> findByPassword(String password);
}

