package com.matevskial.systemdesignplayground.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
class WebClientConfig {

    @Bean
    public WebClient webClient() {
        var provider = ConnectionProvider.builder("generic-client-for-calling-services").maxConnections(1).build();

        var httpClient = HttpClient.create(provider);

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
