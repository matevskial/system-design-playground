package com.matevskial.systemdesignplayground.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/echo")
@Slf4j
public class EchoController {

    @PostMapping
    public Flux<DataBuffer> echo(ServerWebExchange exchange) {
        return exchange.getRequest().getBody();
    }
}
