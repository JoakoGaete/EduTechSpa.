package com.microserviciocursos.microservicioparajcrearcursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI crearCursosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Creación de Cursos - EduTech")
                        .description("API REST para la creación de cursos en la plataforma EduTech")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Soporte EduTech")
                                .email("soporte@edutech.com")
                        )
                );
    }
}
