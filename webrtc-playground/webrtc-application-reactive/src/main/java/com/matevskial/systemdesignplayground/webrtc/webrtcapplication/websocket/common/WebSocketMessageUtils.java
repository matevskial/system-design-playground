package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketMessageUtils {

    public static String jsonErrorMessage(String message) {
        return "{\"type\":\"error\",\"body\": {\"errorMessage\": %s}}".formatted(message);
    }
}
