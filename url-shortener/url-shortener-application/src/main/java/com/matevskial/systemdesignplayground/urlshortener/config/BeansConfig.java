package com.matevskial.systemdesignplayground.urlshortener.config;

import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShorteners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeansConfig {

    @Bean
    public UrlShortener urlShortener() {
        return UrlShorteners.base62Variant();
    }
}
