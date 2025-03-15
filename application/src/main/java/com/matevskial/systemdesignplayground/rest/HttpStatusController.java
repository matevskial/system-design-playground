package com.matevskial.systemdesignplayground.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing endpoint for controlling the http status code of the response
 */
@RestController
@RequestMapping("/api/v1/httpstatus")
@Slf4j
public class HttpStatusController {

    @GetMapping
    public ResponseEntity<Void> getStatus(@RequestParam(required = false) String status, @RequestHeader HttpHeaders headers) {
        log.info("HEADERS: {}", headers);
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.resolve(Integer.parseInt(status));
            if (httpStatus == null) {
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.OK;
        }
        return ResponseEntity.status(httpStatus.value()).build();
    }
}
