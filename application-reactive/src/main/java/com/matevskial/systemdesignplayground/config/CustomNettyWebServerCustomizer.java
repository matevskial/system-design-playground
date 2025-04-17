package com.matevskial.systemdesignplayground.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CustomNettyWebServerCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    /**
     * We are setting request timeout to test how webclient or the httpclient in api-gateway  that accesses this service behaves
     * @param factory
     */
    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(webServer -> {
            return webServer.requestTimeout(Duration.ofSeconds(2));
        });
    }
}
