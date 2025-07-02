package com.microserviciogenerar.microservicioparaxgenerarreportes.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;

import reactor.core.publisher.Mono;

@Component
public class SolucionClient {
    
    private final WebClient webClient;
    
    @Autowired
    public SolucionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8088") // Puerto del microservicio de responder reportes
            .build();
    }
    
    public Mono<String> enviarReporteParaSolucion(GenerarModel reporte) {
        return webClient.post()
            .uri("/api/soluciones/recibir-reporte")
            .bodyValue(reporte)
            .retrieve()
            .bodyToMono(String.class);
    }
} 