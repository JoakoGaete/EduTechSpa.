package com.microservicioasignar.microservicioparakasignarcursos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservicioasignar.microservicioparakasignarcursos.model.ProfesorModel;
import com.microservicioasignar.microservicioparakasignarcursos.repository.ProfesorRepository;
import com.microservicioasignar.microservicioparakasignarcursos.service.ProfesorService;

@Configuration
public class TablaProfesores {
    @Autowired
    private ProfesorService profesorService;

    @Bean
    CommandLineRunner initDataBase(ProfesorRepository profesorRepository) {
        return args -> {
            if (profesorRepository.count() == 0) {
                profesorService.guardarProfesor(new ProfesorModel(null, "August", "Vidal"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Camila", "Rojas"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Pedro", "Martínez"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Lucía", "Gómez"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Matías", "Fernández"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Sofía", "López"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Diego", "Sánchez"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Valentina", "Torres"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Benjamín", "Muñoz"));
                profesorService.guardarProfesor(new ProfesorModel(null, "Isidora", "Pérez"));
            }
        };
    }
}