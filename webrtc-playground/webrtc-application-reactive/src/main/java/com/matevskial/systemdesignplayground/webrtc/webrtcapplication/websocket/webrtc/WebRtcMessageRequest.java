package com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.webrtc;

import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.sdp.SdpMessageType;
import com.matevskial.systemdesignplayground.webrtc.webrtcapplication.websocket.common.WebSocketMessage;

import java.util.Map;

record WebRtcMessageRequest(
        String type,
        Body body
) implements WebSocketMessage {

    public static WebRtcMessageRequest sendSdpOffer(String callerUserId, String sdp) {
        Body body = new Body(null, callerUserId, null, sdp);
        return new WebRtcMessageRequest("receive_sdp_offer", body);
    }

    public static WebRtcMessageRequest sendSdpAnswerOffer(String sdp) {
        Body body = new Body(null, null, null, sdp);
        return new WebRtcMessageRequest("receive_sdp_answer_offer", body);
    }

    public static WebRtcMessageRequest sendSdpIceCandidate(String sdp) {
        Body body = new Body(null, null, null, sdp);
        return new WebRtcMessageRequest("receive_sdp_ice_candidate", body);
    }

    public boolean isJoin() {
        return "join".equals(type);
    }

    public boolean isSdp() {
        return type != null && type.contains("sdp");
    }

    public boolean isSdp(SdpMessageType type) {
        return type.getValue().equals(this.type);
    }

    record Body(
            String userId,
            String callerUserId,
            String targetUserId,
            String sdp
    ) {}
}
