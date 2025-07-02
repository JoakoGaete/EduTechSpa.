package com.microserviciovalorar.microservicioparavalorarcursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI valorarCursosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Valoración de Cursos - EduTech")
                        .description("API REST para la valoración de cursos en la plataforma EduTech")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Soporte EduTech")
                                .email("soporte@edutech.com")
                        )
                );
    }
}
