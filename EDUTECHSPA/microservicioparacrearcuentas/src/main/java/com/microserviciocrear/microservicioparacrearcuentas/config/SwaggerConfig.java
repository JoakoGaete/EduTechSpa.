package com.microserviciocrear.microservicioparacrearcuentas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI crearCuentasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Creación de Cuentas - EduTech")
                        .description("API REST para la gestión y creación de cuentas de usuario en la plataforma EduTech")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Soporte EduTech")
                                .email("soporte@edutech.com")
                        )
                );
    }
}
