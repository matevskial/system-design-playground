package com.matevskial.systemdesignplayground.urlshortener.config;

import com.matevskial.systemdesignplayground.urlshortener.core.TsIdGenerator;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShorteners;
import io.hypersistence.tsid.TSID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UrlShortenerConfig {

    @Bean
    public UrlShortener urlShortener(TSID.Factory tsIdFactory) {
        return UrlShorteners.base62Variant(new TsIdGenerator(tsIdFactory));
    }
}
