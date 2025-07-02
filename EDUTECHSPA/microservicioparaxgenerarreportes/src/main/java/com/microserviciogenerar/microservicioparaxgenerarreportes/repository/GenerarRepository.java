package com.microserviciogenerar.microservicioparaxgenerarreportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;

@Repository
public interface GenerarRepository extends JpaRepository<GenerarModel,Long>{

}
