package com.microserviciocrear.microservicioparacrearcuentas.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;

import reactor.core.publisher.Mono;

@Component
public class GenerarReportesClient {
    
    private final WebClient webClient;
    
    @Autowired
    public GenerarReportesClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8087") // Puerto del microservicio de generar reportes
            .build();
    }
    
    public Mono<String> enviarUsuarioParaReporte(CrearModel usuario) {
        return webClient.post()
            .uri("/api/generar/recibir-usuario")
            .bodyValue(usuario)
            .retrieve()
            .bodyToMono(String.class);
    }
} 