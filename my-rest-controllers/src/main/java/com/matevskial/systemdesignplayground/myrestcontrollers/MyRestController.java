package com.matevskial.systemdesignplayground.myrestcontrollers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/myrestcontrollers")
public class MyRestController {

    @GetMapping
    public String myRestController() {
        return "myrestcontrollers";
    }
}
