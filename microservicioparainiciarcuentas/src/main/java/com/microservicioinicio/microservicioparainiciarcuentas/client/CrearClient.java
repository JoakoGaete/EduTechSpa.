package com.microservicioinicio.microservicioparainiciarcuentas.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;

@Component
public class CrearClient {

    private final WebClient webClient;

    public CrearClient(@Value("${crear-service.url}") String crearServiceUrl) {
    this.webClient = WebClient.builder().baseUrl(crearServiceUrl).build();
    }

    public Map<String, Object> verificarLogin(InicioModel datosUsuarioInicioSesion) {
    return this.webClient.post()
        .uri("/api/registro/login")
        .bodyValue(datosUsuarioInicioSesion)
        .retrieve()
        .onStatus(status -> status.is4xxClientError(),
                    response -> response.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Login inválido: " + body)))
        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
        .block();
    }
}
