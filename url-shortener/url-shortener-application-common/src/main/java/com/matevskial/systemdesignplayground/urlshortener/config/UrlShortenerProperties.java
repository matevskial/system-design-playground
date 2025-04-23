package com.matevskial.systemdesignplayground.urlshortener.config;


import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Data
@ConfigurationProperties(prefix = "url-shortener")
@Validated
public class UrlShortenerProperties {

    @URL
    private String baseUrl;
}
