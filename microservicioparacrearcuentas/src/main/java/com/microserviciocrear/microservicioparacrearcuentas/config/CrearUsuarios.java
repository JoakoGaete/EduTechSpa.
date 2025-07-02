package com.microserviciocrear.microservicioparacrearcuentas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;
import com.microserviciocrear.microservicioparacrearcuentas.repository.CrearRepository;
import com.microserviciocrear.microservicioparacrearcuentas.service.CrearService;

@Configuration
public class CrearUsuarios {

    @Autowired
    private CrearService crearService;

    @Bean
    CommandLineRunner initDataBase(CrearRepository crearRepository) {
        return args -> {
            if (crearRepository.count() == 0) {
                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Gabriel",
                    "Vidal",
                    "20123123-2",
                    "ga.vidal@duocuc.cl",
                    "password123",
                    "ADMIN",
                    "Debito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Raul",
                    "Fernandez",
                    "20456789-5",
                    "ra.fernandez@duocuc.cl",
                    "123",
                    "SOPORTE",
                    "Credito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Joaquin",
                    "Gaete",
                    "20333444-1",
                    "jo.gaete@duocuc.cl",
                    "password789",
                    "USUARIO",
                    "Debito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Simon",
                    "Muñoz",
                    "20987654-8",
                    "si.munoz@duocuc.cl",
                    "clave123",
                    "PROFESOR",
                    "Credito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Valentina",
                    "Torres",
                    "20888888-3",
                    "va.torres@duocuc.cl",
                    "pass123",
                    "USUARIO",
                    "Debito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Catalina",
                    "Rojas",
                    "20777777-2",
                    "ca.rojas@duocuc.cl",
                    "pass456",
                    "PROFESOR",
                    "Credito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Felipe",
                    "Salinas",
                    "20666666-4",
                    "fe.salinas@duocuc.cl",
                    "admin2024",
                    "ADMIN",
                    "Debito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Daniela",
                    "Pérez",
                    "20555555-7",
                    "da.perez@duocuc.cl",
                    "usuario321",
                    "USUARIO",
                    "Credito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Ignacio",
                    "Morales",
                    "20444444-9",
                    "ig.morales@duocuc.cl",
                    "soporte456",
                    "SOPORTE",
                    "Debito"
                ));

                crearService.registrarUsuario(new CrearModel(
                    null,
                    "Fernanda",
                    "González",
                    "20333333-1",
                    "fe.gonzalez@duocuc.cl",
                    "profesor789",
                    "PROFESOR",
                    "Credito"
                ));

                System.out.println("Datos de usuarios cargados correctamente.");
            }
        };
    }
}

