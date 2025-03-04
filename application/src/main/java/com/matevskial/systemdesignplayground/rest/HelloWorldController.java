package com.matevskial.systemdesignplayground.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/helloworld")
@Slf4j
public class HelloWorldController {

    @GetMapping
    public String helloWorld() {
        return "Hello World!";
    }
}
