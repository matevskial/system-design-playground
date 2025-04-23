package com.matevskial.systemdesignplayground.urlshortener.config;

import io.hypersistence.tsid.TSID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
class TsIdConfig {

    /**
     * DO NOT CHANGE THIS, value(in UTC) is Wed Apr 23 2025 13:00:00.000
     */
    private static final Instant TSID_CUSTOM_EPOCH = Instant.ofEpochMilli(1745413200000L);

    /**
     * TODO: Implement configuration to set node in a dynamic way in order to prepare the service to be running
     *  with multiple instances. One example is calculating a node id based on the ip address of the pod where the application is running
     */
    @Bean
    public TSID.Factory tsIdFactory() {
        return TSID.Factory.builder()
                .withNodeBits(6)
                .withNode(42)
                .withCustomEpoch(TSID_CUSTOM_EPOCH)
                .build();
    }
}
