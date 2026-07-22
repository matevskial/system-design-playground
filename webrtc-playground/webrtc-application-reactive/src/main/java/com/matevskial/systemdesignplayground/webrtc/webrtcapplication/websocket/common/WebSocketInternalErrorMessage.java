package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common;

public record WebSocketInternalErrorMessage(String type, String message) implements WebSocketMessage {
    public static WebSocketInternalErrorMessage of(String message) {
        return new WebSocketInternalErrorMessage("internalError", message);
    }
}
