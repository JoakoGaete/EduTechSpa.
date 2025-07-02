package com.microserviciocursos.microservicioparajcrearcursos.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;

import reactor.core.publisher.Mono;

@Component
public class AsignarClient {
    
    private final WebClient webClient;
    
    @Autowired
    public AsignarClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8084") // Puerto del microservicio de asignar cursos
            .build();
    }
    
    public Mono<String> asignarProfesorAutomaticamente(CursosModel curso) {
        return webClient.post()
            .uri("/api/asignar/asignacion-automatica")
            .bodyValue(curso)
            .retrieve()
            .bodyToMono(String.class);
    }
} 