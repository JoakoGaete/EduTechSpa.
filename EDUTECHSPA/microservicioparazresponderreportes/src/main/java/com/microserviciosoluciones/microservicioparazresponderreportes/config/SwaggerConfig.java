package com.microserviciosoluciones.microservicioparazresponderreportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI responderReportesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Respuesta a Reportes - EduTech")
                        .description("API REST para que el soporte técnico responda a reportes generados por usuarios en la plataforma EduTech")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Soporte EduTech")
                                .email("soporte@edutech.com")
                        )
                );
    }
}
