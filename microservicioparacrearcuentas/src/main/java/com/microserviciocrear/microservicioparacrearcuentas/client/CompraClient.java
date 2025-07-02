package com.microserviciocrear.microservicioparacrearcuentas.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;

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
    
    public Mono<String> enviarUsuarioParaCompra(CrearModel usuario) {
        return webClient.post()
            .uri("/api/compras/recibir-usuario")
            .bodyValue(usuario)
            .retrieve()
            .bodyToMono(String.class);
    }
} 