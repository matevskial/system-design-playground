package com.matevskial.systemdesignplayground.rest;

import jakarta.servlet.ServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/echo")
public class EchoController {

    @PostMapping
    public InputStreamResource echo(ServletRequest request) throws IOException {
        return new InputStreamResource(request.getInputStream());
    }
}
