package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketMessageConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebSocketSession session;

    /**
     * Build converter using the provided websocket session.
     * The websocket session contains factory methods for converting string payload to its own WebSocketMessage
     * @param session
     * @return
     */
    public static WebSocketMessageConverter build(WebSocketSession session) {
        return new WebSocketMessageConverter(session);
    }

    /**
     * Converts to reactive websocket message
     * If error occurs during conversion, build a WebSocketMessage with a payload that represents the error message
     * @param message
     * @return
     */
    public WebSocketMessage convert(com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            return session.textMessage(payload);
        } catch (Exception e) {
            return session.textMessage(WebSocketMessageUtils.jsonErrorMessage(e.getMessage()));
        }
    }

    /**
     * Converts to `business logic` websocket message
     * If error occurs during conversion, convert to a `business logic` websocket message that represents internal error
     * @param message
     * @param clazz
     * @return
     */
    public com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage convertTextPayload(WebSocketMessage message, Class<? extends com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage> clazz) {
        String textPayload = message.getPayloadAsText();
        try {
            return objectMapper.readValue(textPayload, clazz);
        } catch (Exception e) {
            return WebSocketInternalErrorMessage.of(e.getMessage());
        }
    }
}
