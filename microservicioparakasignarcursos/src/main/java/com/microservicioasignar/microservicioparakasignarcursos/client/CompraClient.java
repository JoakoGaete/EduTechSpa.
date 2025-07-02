package com.microservicioasignar.microservicioparakasignarcursos.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;

import reactor.core.publisher.Mono;

@Component
public class CompraClient {
    
    private final WebClient webClient;
    
    @Autowired
    public CompraClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8085") // Puerto del microservicio de comprar cursos
            .build();
    }
    
    public Mono<String> enviarAsignacionParaCompra(AsignarModel asignacion) {
        return webClient.post()
            .uri("/api/compras/recibir-asignacion")
            .bodyValue(asignacion)
            .retrieve()
            .bodyToMono(String.class);
    }
} 