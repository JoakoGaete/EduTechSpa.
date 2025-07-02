package com.microserviciocursos.microservicioparajcrearcursos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;
import com.microserviciocursos.microservicioparajcrearcursos.repository.CursosRepository;
import com.microserviciocursos.microservicioparajcrearcursos.service.CursosService;

@Configuration
public class CrearCursos {

    @Autowired
    private CursosService cursosService;

@Bean
CommandLineRunner initDataBase(CursosRepository cursosRepository) {
    return args -> {
        if (cursosRepository.count() == 0) {
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion Web",
                "10.0000",
                20,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion Movil",
                "15.000",
                15,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion IA",
                "20.000",
                9,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion de Videojuegos",
                "25.000",
                8,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion de Sistemas",
                "30.000",
                3,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion de Redes",
                "35.000",
                100,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion de Base de Datos",
                "40.000",
                150,
                "Activo"
            ));
            cursosService.guardarCurso(new CursosModel(
                null,
                "Programacion de Seguridad",
                "45.000",
                100,
                "Activo"
            ));
        }
    };
}
}
