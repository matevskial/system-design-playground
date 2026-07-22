package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.webrtc;

import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage;

record WebRtcMessageResponse(
        String type,
        Body body
) implements WebSocketMessage {

    public static WebRtcMessageResponse error(String errorMessage) {
        Body body = new Body(errorMessage);
        return new WebRtcMessageResponse("error", body);
    }

    public static WebRtcMessageResponse joined() {
        return new WebRtcMessageResponse("joined", null);
    }

    public static WebRtcMessageResponse successAck() {
        return new WebRtcMessageResponse("successAck", null);
    }

    record Body(
            String errorMessage
    ) {}
}
