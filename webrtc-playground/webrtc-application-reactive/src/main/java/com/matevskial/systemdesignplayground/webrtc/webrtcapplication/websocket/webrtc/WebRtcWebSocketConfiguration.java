package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.webrtc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
class WebRtcWebSocketConfiguration {

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        int order = -1; // before annotated controllers
        map.put("/ws/webrtc", new WebRtcWebSocketHandler());
        return new SimpleUrlHandlerMapping(map, order);
    }
}
