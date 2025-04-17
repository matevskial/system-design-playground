package com.matevskial.systemdesignplayground.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Endpoint that calls the `application` service
 */
@RestController
@RequestMapping("/api/v1/client/application")
@RequiredArgsConstructor
public class ApplicationClientController {

    @Value("${routes.application.uri}")
    private String baseUri;

    private final WebClient webClient;

    /**
     * Note: .body(Flux.range(1, 10).map(String::valueOf).delayElements(Duration.ofSeconds(2)), String.class) actually streams the body,
     * it does NOT wait to collect all the body before sending it
     * @return
     */
    @GetMapping("/withDelay")
    public Mono<String> clientWithDelay() {
        return webClient.post()
                .uri(baseUri + "/api/v1/echo")
                .body(Flux.range(1, 10).map(String::valueOf).delayElements(Duration.ofSeconds(2)), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/noDelay")
    public Mono<String> clientNoDelay() {
        return webClient.post()
                .uri(baseUri + "/api/v1/echo")
                .body(Flux.range(1, 10).map(String::valueOf), String.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}
