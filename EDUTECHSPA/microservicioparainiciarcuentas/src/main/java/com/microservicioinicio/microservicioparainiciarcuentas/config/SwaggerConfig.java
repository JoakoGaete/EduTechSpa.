package com.microservicioinicio.microservicioparainiciarcuentas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI iniciarCuentasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Inicio de Sesión - EduTech")
                        .description("API REST para iniciar sesión en la plataforma EduTech")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Soporte EduTech")
                                .email("soporte@edutech.com")
                        )
                );
    }
}
